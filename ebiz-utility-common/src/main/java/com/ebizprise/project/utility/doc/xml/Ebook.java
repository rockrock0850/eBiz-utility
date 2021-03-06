package com.ebizprise.project.utility.doc.xml;

import javax.xml.bind.annotation.XmlElement;

public class Ebook {
	String name;
	int price;
	String id;

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@XmlElement
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
