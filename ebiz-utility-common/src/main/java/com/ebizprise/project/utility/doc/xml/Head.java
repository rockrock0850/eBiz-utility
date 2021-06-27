package com.ebizprise.project.utility.doc.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "EIPAD")
public class Head {

	@XmlElement(name = "accounts")
	public Account account;

	@XmlTransient
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
}
