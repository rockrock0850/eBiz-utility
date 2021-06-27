package com.ebizprise.project.utility.doc.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author gary.tsai 2019/5/17
 */
public class UserId {

  String uId;
  String description;
  String mail;
  String title;
  String memberOf;
  String department;
  String departmentNumber;
  String info;
  String ou;

  @XmlElement
  public String getuId() {
    return uId;
  }

  public void setuId(String uId) {
    this.uId = uId;
  }

  @XmlElement
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @XmlElement
  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  @XmlElement
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @XmlElement
  public String getMemberOf() {
    return memberOf;
  }

  public void setMemberOf(String memberOf) {
    this.memberOf = memberOf;
  }

  @XmlElement
  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  @XmlElement
  public String getDepartmentNumber() {
    return departmentNumber;
  }

  public void setDepartmentNumber(String departmentNumber) {
    this.departmentNumber = departmentNumber;
  }

  @XmlElement
  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  @XmlElement
  public String getOu() {
    return ou;
  }

  public void setOu(String ou) {
    this.ou = ou;
  }
}
