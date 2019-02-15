package com.shidebin.mongodb.spring_mongodb;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Update.PushOperatorBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.WriteResult;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.operation.FindAndUpdateOperation;
import com.shidebin.mongo.entity.Address;
import com.shidebin.mongo.entity.Comment;
import com.shidebin.mongo.entity.Favorites;
import com.shidebin.mongo.entity.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SpringPojoTest {
	private static final Logger logger = LoggerFactory.getLogger(SpringPojoTest.class);
	@Autowired
	private MongoOperations operation;
	@Test
	public void insertDemo(){
    	User user = new User();
    	user.setUsername("cang");
    	user.setCountry("USA");
    	user.setAge(20);
    	user.setLenght(1.77f);
    	user.setSalary(new BigDecimal("6265.22"));
    	Address address1 = new Address();
    	address1.setaCode("411222");
    	address1.setAdd("sdfsdf");
    	user.setAddress(address1);
    	Favorites favorites1 = new Favorites();
    	favorites1.setCites(Arrays.asList("东莞","东京"));
    	favorites1.setMovies(Arrays.asList("西游记","一路向西"));
    	user.setFavorites(favorites1);
    	
    	User user1 = new User();
    	user1.setUsername("chen");
    	user1.setCountry("China");
    	user1.setAge(30);
    	user1.setLenght(1.77f);
    	user1.setSalary(new BigDecimal("6885.22"));
    	Address address2 = new Address();
    	address2.setaCode("411000");
    	address2.setAdd("我的地址2");
    	user1.setAddress(address2);
    	Favorites favorites2 = new Favorites();
    	favorites2.setCites(Arrays.asList("珠海","东京"));
    	favorites2.setMovies(Arrays.asList("东游记","一路向东"));
    	user1.setFavorites(favorites2);
    	
    	operation.insertAll(Arrays.asList(user,user1));
    	
    }
	@Test
    public void testDelete(){
    	
    	//delete from users where username = ‘lison’
    	
		WriteResult result = operation.remove(Query.query(Criteria.where("username").is("lison")),
    			User.class);
    	logger.info("删除条数"+result.getN());
    	
    	//delete from users where age >8 and age <25
    	WriteResult result2 = operation.remove(Query.query(new Criteria().andOperator(Criteria.where("age")
    			.gt(8),Criteria.where("age").lt(25))), User.class);
    	logger.info("删除条数"+result2.getN());
    }
	@Test
    public void testUpdate(){
    	//update  users  set age=6 where username = 'lison' 
		WriteResult updateMany = operation.updateMulti(Query.query(Criteria.where("username").is("lison")), 
    			Update.update("age", 6), User.class);
    	logger.info(String.valueOf(updateMany.getN()));
    	
    	//update users  set favorites.movies add "小电影2 ", "小电影3" where favorites.cites  has "东莞"
    	WriteResult updateMany2 = operation.updateMulti(Query.query(Criteria.where("favorites.cites")
    			.is("东莞")),new Update().addToSet("favorites.movies").each("小电影2","小电影3"),
    			User.class);
    	logger.info(String.valueOf(updateMany2.getN()));
    }
	@Test
	public void update1() {
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
    	 */
    	Query query = Query.query(Criteria.where("username").is("mark"));
    	Update update = new Update();
    	Comment comment = new Comment();
    	comment.setAuthor("lison5");
    	comment.setContent("lison是苍老师的小迷弟");
    	comment.setCommentTime(new Date());
    	PushOperatorBuilder push = update.push("comments");
    	push.each(comment);
    	push.sort(new Sort(Direction.DESC,"commentTime"));
    	WriteResult updateMany3 = operation.updateFirst(query,update, User.class);
    	logger.info("++++++++++++++++++++++++");
    	logger.info(String.valueOf(updateMany3.getN()));
	}
	/**
	 * db.users.updateMany({"username":"lison"},{"$unset":{"country":"","age":""}})
	 */
	@Test
	public void update2() {
		Query query = Query.query(Criteria.where("username").is("lison"));
		Update update = new Update();
		update.unset("country");
		update.unset("age");
		WriteResult updateFirst = operation.updateFirst(query, update, User.class);
		System.out.println("++++++"+updateFirst.getN());
	}
	/**
	 * db.users.updateMany({"username":"jack"},{"$rename":{"country":"guojia","age":"nianling"}})
	 */
	@Test
	public void update3() {
		Query query = Query.query(Criteria.where("username").is("jack"));
		Update update = new Update();
		update.rename("country", "guojia");
		update.rename("age", "nianling");
		WriteResult updateMulti = operation.updateMulti(query, update, User.class);
		System.out.println("++++++"+updateMulti.getN());
	}
	/**
	 * db.users.updateMany({ "username" : "jack"}, { "$addToSet" : { "favorites.movies" : 
	 * { "$each" : [ "小电影2 " , "小电影3"]}}})
	 */
	@Test
	public void update4() {
		Query query = Query.query(Criteria.where("username").is("jack"));
		Update update = new Update();
		update.addToSet("favorites.movies").each("小电影2 " , "小电影3");
		WriteResult updateFirst = operation.updateFirst(query, update, User.class);
		System.out.println("++++++"+updateFirst.getN());
	}
	/**
	 * db.users.updateMany({ "username" : "jack"}, { "$pullAll" : { "favorites.movies" : [ "小电影2 " , "小电影3"]}})
	 */
	@Test
	public void update5() {
		Query query = Query.query(Criteria.where("username").is("jack"));
		Update update = new Update();
		update.pullAll("favorites.movies", new Object[] {"小电影2 " , "小电影3"});
		WriteResult updateMulti = operation.updateMulti(query, update, User.class);
		System.out.println("++++++"+updateMulti.getN());
	}
	/**
	 * db.users.updateOne({"username":"jack"},{"$push":{"comments":{"author":"lison23","content":"ydddyyytttt"}}})
	 */
	@Test
	public void update6() {
		Query query = Query.query(Criteria.where("username").is("jack"));
		Update update = new Update();
		Comment comment = new Comment();
		comment.setAuthor("lison23");
		comment.setContent("ydddyyytttt");
		update.push("comments",comment);
		WriteResult updateMulti = operation.updateMulti(query, update, User.class);
		System.out.println("++++++"+updateMulti.getN());
	}
	/**
	 * db.users.updateOne({"username":"jack"},     
       {"$push":{"comments":
                  {"$each":[{"author":"lison24","content":"yyyytttt"},
                                  {"author":"lison25","content":"ydddyyytttt"}]}}})
	 */
	@Test
	public void update7() {
		Query query = Query.query(Criteria.where("username").is("jack"));
		Update update = new Update();
		Comment comment = new Comment();
		comment.setAuthor("lison24");
		comment.setContent("ydddyyytttt");
		Comment comment1 = new Comment();
		comment1.setAuthor("lison25");
		comment1.setContent("ydddyyytttt");
		update.push("comments").each(comment,comment1);
		WriteResult updateMulti = operation.updateMulti(query, update, User.class);
		System.out.println("++++++"+updateMulti.getN());
	}
	/**
	 * db.users.updateOne({"username":"jack"}, 
      {"$push": {"comments":
                {"$each":[ {"author":"lison22","content":"yyyytttt"},
                                {"author":"lison23","content":"ydddyyytttt"} ], 
                  $sort: {"author":1} } } })
	 */
	@Test
	public void update8() {
		Query query = Query.query(Criteria.where("username").is("jack"));
		Update update = new Update();
		Comment comment = new Comment();
		comment.setAuthor("lison24");
		comment.setContent("ydddyyytttt");
		Comment comment1 = new Comment();
		comment1.setAuthor("lison25");
		comment1.setContent("ydddyyytttt");
		PushOperatorBuilder push = update.push("comments");
		push.each(comment,comment1);
		push.sort(new Sort(Direction.ASC,Arrays.asList("author")));
		WriteResult updateMulti = operation.updateMulti(query, update, User.class);
		System.out.println("++++++"+updateMulti.getN());
	}
	/**
	 * db.users.update({"username":"jack"},
                               {"$pull":{"comments":{"author":"lison22"}}})
	 */
	@Test
	public void update9() {
		Query query = Query.query(Criteria.where("username").is("jack"));
		Update update = new Update();
		Comment comment = new Comment();
		comment.setAuthor("lison22");
		update.pull("comments", comment);
		WriteResult updateFirst = operation.updateFirst(query, update, User.class);
		System.out.println("++++++"+updateFirst.getN());
		
	}
	/**
	 * db.users.update({"username":"lison"},
                               {"$pull":{"comments":{"author":"lison5",
                                                                 "content":"lison是苍老师的小迷弟"}}})
	 */
	@Test
	public void update10() {
		Query query = Query.query(Criteria.where("username").is("lison"));
		Update update = new Update();
		Comment comment = new Comment();
		comment.setAuthor("lison5");
		comment.setContent("lison是苍老师的小迷弟");
		update.pull("comments", comment);
		WriteResult updateFirst = operation.updateFirst(query, update, User.class);
		System.out.println("++++++"+updateFirst.getN());
	}
	/**
	 * db.users.updateMany({"username":"jack","comments.author":"lison1"},
                    {"$set":{"comments.$.content":"xxoo",
                                "comments.$.author":"lison10" }})
	 */
	@Test
	public void update11() {
			Query query = Query.query(Criteria.where("username").is("jack")
					.andOperator(Criteria.where("comments.author").is("lison1")));
			Update update = Update.update("comments.$.content", "xxoo").set("comments.$.author", "lison10");
			WriteResult updateMulti = operation.updateMulti(query, update, User.class);
			System.out.println("++++++"+updateMulti.getN());
	}
	/**
	 * db.users.findAndModify({{"username":"lison","comments.author":"lison1"},
                    {"$set":{"comments.$.content":"xxoo",
                                "comments.$.author":"lison10" }},"new":true})
	 */
	@Test
	public void update14() {
		Query query = Query.query(Criteria.where("username").is("lison").
				andOperator(Criteria.where("comments.author").is("lison1")));
		Update update = Update.update("comments.$.content", "xxoo").set("comments.$.author", "lison10");
		FindAndModifyOptions returnNew = FindAndModifyOptions.options().returnNew(true);
		User findAndModify = operation.findAndModify(query, update, returnNew, User.class);
		System.out.println("++++++"+findAndModify.toString());
	}
	@Test
	public void find2() {
		// db.users.find({"comments":{"$elemMatch":{"author" : "lison5","content" :
		// "lison是苍老师的小迷弟"}}}) .pretty()
		Query query = Query.query(Criteria.where("comments").elemMatch(
				new Criteria().andOperator(Criteria.where("author").is("lison5"),
						Criteria.where("content").is("lison是苍老师的小迷弟"))));
		List<User> find = operation.find(query, User.class);
		logger.info("++++++++++++++++++++++++");
    	find.stream().forEach(user ->{System.out.println(user);});
	}
	/**
	 * db.users.aggregate([{"$match":{"username":"lison"}},
	                       {"$unwind":"$comments"},
	                       {$sort:{"comments.commentTime":-1}},
	                       {"$project":{"comments":1}},
	                       {"$skip":6},
	                       {"$limit":3}])
	                       
	 */
	@Test
	public void tesFind() {
		AggregationOperation  matchOperation = new MatchOperation(Criteria.where("username").is("lison"));
		AggregationOperation unwindOperation = new UnwindOperation(Fields.field("$comments"));
		AggregationOperation sortOperation = new SortOperation(new Sort(Direction.DESC,"comments.commentTime"));
		AggregationOperation projectionOperation = new ProjectionOperation(Fields.fields("comments"));
		AggregationOperation skipOperation = new SkipOperation(6);
		AggregationOperation limitOperation = new LimitOperation(3);
		Aggregation newAggregation = Aggregation.newAggregation(matchOperation,unwindOperation,sortOperation,projectionOperation,skipOperation,limitOperation);
		AggregationResults<Object> agg = operation.aggregate(newAggregation,"users",Object.class);
		agg.forEach(user ->{System.out.println(user);});
	}
	//测试DbRef
	@Test
	public void test() {
		List<User> userList = operation.findAll(User.class);
		userList.stream().forEach(user ->{System.out.println(user);});
	}
	@Test
    public void testFind(){
    	//select * from users  where favorites.cites has "东莞"、"东京"
		List<User> userList = operation.find(Query.query(new Criteria().andOperator(Criteria.where("favorites.cites").is("东莞"),
				Criteria.where("favorites.cites").is("东京"))), User.class);
		userList.stream().forEach(consumer -> {System.out.println(consumer);});
    	
    	//select * from users  where username like '%s%' and (country= English or country = USA)
		List<User> userList2 = operation.find(Query.query(new Criteria().andOperator(new Criteria().orOperator(Criteria.where("country").is("English"),
				Criteria.where("country").is("USA")),Criteria.where("username").regex(".*s.*"))), User.class);
		userList2.stream().forEach(consumer -> {System.out.println(consumer);});
		//db.users.find({"username":"lison"},{"comments":{"$slice":[0,3]},"$id":1}).pretty();
		Query query = Query.query(Criteria.where("username").is("lison"));
		query.fields().include("comments").slice("comments",0,3).include("id");
		List<User> userList3 = operation.find(query, User.class);
		userList3.stream().forEach(consumer -> {System.out.println(consumer);});
		//db.users.find({"username":"lison"},{"comments":{"$slice":[3,3]},"$id":1}).pretty();
		query.fields().include("comments").slice("comments",3,3).include("id");
		List<User> userList4 = operation.find(query, User.class);
		userList4.stream().forEach(consumer -> {System.out.println(consumer);});
    }
}
