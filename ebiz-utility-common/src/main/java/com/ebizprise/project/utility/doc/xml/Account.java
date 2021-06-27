package com.ebizprise.project.utility.doc.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author gary.tsai 2019/5/17
 */
public class Account {
  @XmlElement(name = "userid")
  List<UserId> userIdList;

  @XmlTransient
  public List<UserId> getUserIdList() {
    return userIdList;
  }

  public void setUserIdList(List<UserId> userIdList) {
    this.userIdList = userIdList;
  }
}
