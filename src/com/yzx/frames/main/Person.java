package com.yzx.frames.main;

import java.io.Serializable;

import com.yzx.frames.tool.db.annotation.DBIgnore;
import com.yzx.frames.tool.db.annotation.ID;

public class Person implements Serializable {

	@DBIgnore
	private static final long serialVersionUID = 4985156148924823533L;

	private String name;
	private String age;
	@ID
	private String number;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
