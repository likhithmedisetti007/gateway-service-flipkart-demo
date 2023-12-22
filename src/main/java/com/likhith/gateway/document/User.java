package com.likhith.gateway.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "user")
@Data
public class User {

	@Id
	private String id;
	private String username;
	private String password;
	private String[] roles;

}