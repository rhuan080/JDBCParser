package org.jdbcParser;

import java.util.List;

public class Sale {
	
	private Integer id;
	
	
	private String name;
	
	
	private List<Test> test;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Test> getTest() {
		return test;
	}

	public void setTest(List<Test> test) {
		this.test = test;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
