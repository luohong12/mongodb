package com.shidebin.mongo.entity;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="users")
public class User {
	
	private ObjectId id;
		
	private String username;
	
	private String country;
	
	private Address address;
	
	private Favorites favorites;
	
	private int age;
	
	private BigDecimal salary;
	
	private float lenght;
	//测试不是DbRef的时候使用
	private List<Comment> comments;
//	@DBRef
//	private Comments comments;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public Favorites getFavorites() {
		return favorites;
	}
	public void setFavorites(Favorites favorites) {
		this.favorites = favorites;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public BigDecimal getSalary() {
		return salary;
	}
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	public float getLenght() {
		return lenght;
	}
	public void setLenght(float lenght) {
		this.lenght = lenght;
	}
	/*public Comments getComments() {
		return comments;
	}
	public void setComments(Comments comments) {
		this.comments = comments;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", country=" + country + ", address=" + address
				+ ", favorites=" + favorites + ", age=" + age + ", salary=" + salary + ", lenght=" + lenght
				+ ", comments=" + comments + "]";
	}*/
	
	
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", country=" + country + ", address=" + address
				+ ", favorites=" + favorites + ", age=" + age + ", salary=" + salary + ", lenght=" + lenght
				+ ", comments=" + comments + "]";
	}
	
	
	
}
