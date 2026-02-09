package com.spring.ai.firstproject.dao;



public class Users {

	   public String username;
	   public String password;
	   public  String name;
	   public String role;
       
       
	   public Users(String username, String password, String name, String role) {
		
		this.username = username;
		this.password = password;
		this.name = name;
		this.role = role;
	}
	   public String getUsername() {
		   return username;
	   }
	   public void setUsername(String username) {
		   this.username = username;
	   }
	   public String getPassword() {
		   return password;
	   }
	   public void setPassword(String password) {
		   this.password = password;
	   }
	   public String getName() {
		   return name;
	   }
	   public void setName(String name) {
		   this.name = name;
	   }
	   public String getRole() {
		   return role;
	   }
	   public void setRole(String role) {
		   this.role = role;
	   }
       
       
}
