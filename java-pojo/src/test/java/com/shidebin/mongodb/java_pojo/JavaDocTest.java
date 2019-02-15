package com.shidebin.mongodb.java_pojo;


import static com.mongodb.client.model.Filters.eq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.PushOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.operation.UpdateOperation;

public class JavaDocTest {
	private static final Logger logger = LoggerFactory.getLogger(JavaDocTest.class);
	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	@Before
	public void init() {
		MongoClientOptions mco = MongoClientOptions.builder()
				.writeConcern(WriteConcern.ACKNOWLEDGED)
				.connectionsPerHost(100)
				.threadsAllowedToBlockForConnectionMultiplier(5)
				.maxWaitTime(120000).connectTimeout(10000).build();
		//安全认证
		MongoCredential createCredential = MongoCredential.createCredential("lison", "lison", "lison".toCharArray());
		client = new MongoClient(new ServerAddress("192.168.126.128",27022),Arrays.asList(createCredential));
		client = new MongoClient("192.168.126.128",27022);
		database = client.getDatabase("lison");
		collection = database.getCollection("users");
	}
	@Test
	public void insert() {
		Document doc1 = new Document();
    	doc1.append("username", "cang");
    	doc1.append("country", "USA");
    	doc1.append("age", 20);
    	doc1.append("lenght", 1.77f);
    	doc1.append("salary", new BigDecimal("6565.22"));
    	
    	Map<String, String> address1 = new HashMap<String, String>();
    	address1.put("aCode", "0000");
    	address1.put("add", "xxx000");
    	doc1.append("address", address1);
    	
    	Map<String, Object> favorites1 = new HashMap<String, Object>();
    	favorites1.put("movies", Arrays.asList("aa","bb"));
    	favorites1.put("cites", Arrays.asList("东莞","东京"));
    	doc1.append("favorites", favorites1);
    	
    	Document doc2  = new Document();
    	doc2.append("username", "chen");
    	doc2.append("country", "China");
    	doc2.append("age", 30);
    	doc2.append("lenght", 1.77f);
    	doc2.append("salary", new BigDecimal("8888.22"));
    	Map<String, String> address2 = new HashMap<String, String>();
    	address2.put("aCode", "411000");
    	address2.put("add", "我的地址2");
    	doc1.append("address", address2);
    	Map<String, Object> favorites2 = new HashMap<String, Object>();
    	favorites2.put("movies", Arrays.asList("东游记","一路向东"));
    	favorites2.put("cites", Arrays.asList("珠海","东京"));
    	doc2.append("favorites", favorites2);
    	collection.insertMany(Arrays.asList(doc1,doc2));
	}
	@Test
	public void delete() {
		//删除username为cang的人
		DeleteResult result = collection.deleteMany(Filters.eq("username", "cang"));
		logger.info("删除的数量："+result.getDeletedCount());
		//删除age大于8小于25的人
		long count = collection.count(Filters.and(Filters.lt("age", 25),Filters.gt("age", 8)));
		logger.info("age大于8小于25的人:"+count);
		DeleteResult result2 = collection.deleteMany(Filters.and(Filters.gt("age", 8),Filters.lt("age", 25)));
		logger.info("删除的数量2："+result2.getDeletedCount());
	}
	@Test
	public void update() {
		//修改username为chen的age为8
		UpdateResult result = collection.updateMany(Filters.eq("username", "chen"), Updates.set("age", 8));
		logger.info("修改的数量："+result.getModifiedCount());
		//给喜爱东京的人也爱好小电影2和小电影3
		UpdateResult result2 = collection.updateMany(Filters.eq("favorites.cites", "东京"), 
				Updates.addEachToSet("favorites.movies", Arrays.asList("小电影2","小电影3")));
		logger.info("修改的数量2："+result2.getModifiedCount());
	}
	/**
	 * db.users.updateOne({"username":"lison",},
					{
					  "$push": {
						 "comments": {
						   $each: [
								{
									"author" : "james",
									"content" : "lison是个好老师！",
									"commentTime" : ISODate("2018-01-06T04:26:18.354Z")
								}
							],
						   $sort: {"commentTime":-1}
						 }
					  }
					}
				);
	新增评论时，使用$sort运算符进行排序，插入评论后，再按照评论时间降序排序；
	 */
	@Test
	public void update1() {
		Document doc = new Document().append("author", "james").append("content", "lison是个好老师！").
				append("commentTime", new Date());
		PushOptions sortDocument = new PushOptions().sortDocument(new Document().append("commentTime", -1));
		Bson pushEach = Updates.pushEach("comments", Arrays.asList(doc), sortDocument);
		UpdateResult updateOne = collection.updateOne(Filters.eq("username", "lison"),pushEach);
		System.out.println("++++++++++"+updateOne.getModifiedCount());
	}
	@Test
	/**
	 * db.users.update({"":""},{"username":"leilei"},{"upsert":true})

	 */
	public void update3() {
		UpdateOptions upsert = new UpdateOptions().upsert(true);
		UpdateResult updateOne = collection.updateOne(Filters.eq("username", ""),Updates.setOnInsert("username", "leilei"),upsert);
		System.out.println("++++++++++"+updateOne.getModifiedCount());
	}
	/**
	 * db.users.updateMany({"username":"lison"},{"$unset":{"country":"","age":""}})
	 */
	@Test
	public void update4() {
		UpdateResult updateMany = collection.updateMany(Filters.eq("username", "lison"), 
				Updates.combine(Updates.unset("country"),Updates.unset("age")));
		System.out.println("++++++++++"+updateMany.getModifiedCount());
	}
	/**
	 * db.users.updateMany({"username":"jack"},{"$rename":{"country":"guojia","age":"nianling"}})
	 */
	@Test
	public void update5() {
		UpdateResult updateMany = collection.updateMany(Filters.eq("username", "jack"), 
				Updates.combine(Updates.rename("country", "guojia"),Updates.rename("age", "nianling")));
		System.out.println("++++++++++"+updateMany.getModifiedCount());
	}
	/**
	 * db.users.updateMany({ "username" : "jack"}, { "$addToSet" : { "favorites.movies" : { "$each" : [ "小电影2 " , "小电影3"]}}})
	 */
	@Test
	public void updat6() {
		UpdateResult updateMany = collection.updateMany(Filters.eq("username", "jack"), 
				Updates.addEachToSet("favorites.movies", Arrays.asList("小电影2 " , "小电影3")));
		System.out.println("++++++++++"+updateMany.getModifiedCount());
	}
	/**
	 * db.users.updateMany({ "username" : "jack"}, { "$pull" : { "favorites.movies" : [ "小电影2 " , "小电影3"]}})
	 */
	@Test
	public void update7() {
		UpdateResult updateMany = collection.updateMany(Filters.eq("username", "jack"), 
				Updates.pullAll("favorites.movies", Arrays.asList("小电影2 " , "小电影3")));
		System.out.println("++++++++++"+updateMany.getModifiedCount());
	}
	/**
	 * db.users.updateOne({"username":"jack"},
	 * {"$push":{"comments":{"author":"lison23","content":"ydddyyytttt"}}})
	 */
	@Test
	public void update8() {
		Document document = new Document();
		document.append("author", "lison23");
		document.append("content", "ydddyyytttt");
		UpdateResult updateOne = collection.updateOne(Filters.eq("username", "jack"), Updates.push("comments", document));
		System.out.println("++++++++++"+updateOne.getModifiedCount());
	}
	/**
	 * db.users.updateOne({"username":"jack"},     
       {"$push":{"comments":
                  {"$each":[{"author":"lison21","content":"yyyytttt"},
                                  {"author":"lison22","content":"ydddyyytttt"}]}}})
	 */
	@Test
	public void update9() {
		Document document = new Document();
		document.append("author", "lison21");
		document.append("content", "ydddyyytttt");
		Document document2 = new Document();
		document2.append("author", "lison22");
		document2.append("content", "ydddyyytttt");
		UpdateResult updateOne = collection.updateOne(Filters.eq("username", "jack"), 
				Updates.pushEach("comments", Arrays.asList(document,document2)));
		System.out.println("++++++++++"+updateOne.getModifiedCount());
	}
	/**
	 * db.users.updateOne({"username":"jack"}, 
      {"$push": {"comments":
                {"$each":[ {"author":"lison24","content":"yyyytttt"},
                                {"author":"lison25","content":"ydddyyytttt"} ], 
                  $sort: {"author":1} } } })
	 */
	@Test
	public void update10() {
		Document document = new Document();
		document.append("author", "lison24");
		document.append("content", "ydddyyytttt");
		Document document2 = new Document();
		document2.append("author", "lison25");
		document2.append("content", "ydddyyytttt");
		PushOptions sortDocument = new PushOptions().sortDocument(Sorts.ascending("author"));
		UpdateResult updateOne = collection.updateOne(Filters.eq("username", "jack"), 
				Updates.pushEach("comments", Arrays.asList(document,document2),sortDocument));
		System.out.println("++++++++++"+updateOne.getModifiedCount());
	}
	/**
	 * db.users.update({"username":"jack"},
                               {"$pull":{"comments":{"author":"lison22"}}})
	 */
	@Test
	public void update11() {
		Document document = new Document();
		document.append("author", "lison22");
		UpdateResult updateMany = collection.updateMany(Filters.eq("username", "jack"), 
				Updates.pull("comments",document));
		System.out.println("++++++++++"+updateMany.getModifiedCount());
	}
	/**
	 * db.users.update({"username":"lison"},
                               {"$pull":{"comments":{"author":"lison5",
                                   "content":"lison是苍老师的小迷弟"}}})
	 */
	@Test
	public void update12() {
		Document doc = new Document();
		doc.append("author", "lison5");
		doc.append("content", "lison是苍老师的小迷弟");
		UpdateResult updateOne = collection.updateOne(Filters.eq("username", "lison"), 
				Updates.pull("comments", doc));
		System.out.println("++++++++++"+updateOne.getModifiedCount());
	}
	/**
	 * db.users.updateMany({"username":"jack","comments.author":"lison1"},
                    {"$set":{"comments.$.content":"xxoo",
                                "comments.$.author":"lison10" }})
	 */
	@Test
	public void update13() {
		UpdateResult updateMany = collection.updateMany(Filters.and(Filters.eq("username", "jack"),Filters.eq("comments.author", "lison16")),
				Updates.combine(Updates.set("comments.$.content", "xxoo"),
						Updates.set("comments.$.author", "lison10")));
//		Document doc = new Document();
//		doc.append("content","xxoo");
//		doc.append("author", "lison10");
//		UpdateResult updateMany = collection.updateMany(Filters.and(Filters.eq("username", "jack"),Filters.eq("comments.author", "lison16")),
//				Updates.set("comments", doc));
		System.out.println("++++++++++"+updateMany.getModifiedCount());
	}
	/**
	 * db.users.findAndModify({{"username":"json","comments.author":"lison1"},
                    {"$set":{"comments.$.content":"xxoo",
                                "comments.$.author":"lison10" }},"new":true})
	 */
	@Test
	public void update14() {
		Document findOneAndUpdate = collection.findOneAndUpdate(Filters.and(Filters.eq("username", "lison"),Filters.eq("comments.author", "lison2")), 
				Updates.combine(Updates.set("comments.$.content", "xxoo2"),
						Updates.set("comments.$.author", "lison12")),new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
		System.out.println("++++++++++"+findOneAndUpdate.toJson());
	}
	/**
	 * 查看人员时加载最新的三条评论；
	db.users.find({"username":"lison"},{"comments":{"$slice":[0,3]}}).pretty()
	 */
	@Test
	public void query2() {
		FindIterable<Document> projection = collection.find(Filters.eq("username", "lison")).
		projection(Projections.slice("comments", 0, 3));
		Block<Document> block = getBlock();
		forEach(projection,block);
	}
	/**
	 * 点击评论的下一页按钮，新加载三条评论
	db.users.find({"username":"lison"},{"comments":{"$slice":[3,3]},"$id":1}).pretty();
	 */
	@Test
	public void query3() {
		Bson slice = Projections.slice("comments", 3, 3);
		Bson include = Projections.include("$id");
		FindIterable<Document> projection = collection.find(Filters.eq("username", "lison")).
		projection(Projections.fields(slice,include));
		Block<Document> block = getBlock();
		forEach(projection,block);
	}
	@Test
	public void query() {
		//查询总共有多少数据
		logger.info("总共有多少数据："+collection.count());
		//查询喜好城市为东莞和东京的人
		/*FindIterable<Document> result = collection.find(Filters.and(Filters.eq("favorites.cites","东莞"),
				Filters.eq("favorites.cites","东京")));*/
		FindIterable<Document> result = collection.find(Filters.all("favorites.cites", "东京","东莞"));
		@SuppressWarnings("unchecked")
		Block<Document> block = new Block() {
			@Override
			public void apply(Object t) {
				logger.info(t.toString());
			}
			
		};
		result.forEach(block);
		//查询username中含有“s”，country为“English”或"USA"
		FindIterable<Document> result2 = collection.find(Filters.and(Filters.regex("username", ".*s.*"),
				Filters.in("country", "English","USA")));
		result2.forEach(block);
		//db.users.find({"username":"lison"},{"comments":{"$slice":[0,3]},"$id":1}).pretty();
		FindIterable<Document> result3 = collection.find(Filters.eq("username", "lison"))
				.projection(Projections.fields(Projections.include("$id"),Projections.slice("comments", 0, 3)));
		result3.forEach(block);
		FindIterable<Document> result4 = collection.find(Filters.eq("username", "lison"))
				.projection(Projections.fields(Projections.include("$id"),Projections.slice("comments", 3, 3)));
		result4.forEach(block);
	}
	
	/**
	 * db.users.aggregate([{"$match":{"username":"lison"}},
	                       {"$unwind":"$comments"},
	                       {$sort:{"comments.commentTime":-1}},
	                       {"$project":{"comments":1}},
	                       {"$skip":6},
	                       {"$limit":3}])
	              如果有多种排序需求怎么处理？使用聚合         
	 */
	@Test
	public void query1() {
		Block<Document> block = new Block<Document>() {
			@Override
			public void apply(Document t) {
				logger.info(t.toJson());
			}
			
		};
		AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(Aggregates.match(Filters.eq("username", "lison")),
				Aggregates.unwind("$comments"),Aggregates.sort(Sorts.descending("commentTime")),
				Aggregates.project(Projections.include("comments")),Aggregates.skip(6),Aggregates.limit(3)));
		aggregate.forEach(block);
	}
	//测试DbRef
	@Test
	public void test() {
		Block<Document> block = new Block<Document>() {
			@Override
			public void apply(Document t) {
				logger.info("---------------------");
//				logger.info(t.toJson());
				Object object = t.get("comments");
				System.out.println(object);
				logger.info("---------------------");
			}
			
		};
		FindIterable<Document> find = collection.find(Filters.eq("username", "lison"));
		find.forEach(block);
	}
	public Block<Document> getBlock(){
		return new Block<Document>() {
			@Override
			public void apply(Document t) {
				logger.info(t.toJson());
			}
			
		};
	}
	public void forEach(FindIterable<Document> projection,Block<Document> block) {
		projection.forEach(block);
	}
	
}
