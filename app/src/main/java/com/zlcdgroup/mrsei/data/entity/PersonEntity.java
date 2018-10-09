package com.zlcdgroup.mrsei.data.entity;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/10/8 12:21
 */
@Entity(nameInDb = "tb_person")
public class PersonEntity implements Serializable {
  private static final long serialVersionUID = -6108705197988995085L;

  @Id(autoincrement = true)
  private    Long   id;

  @Property
  private String personId;
  @Property
  private String personAccount;
  @Property
  private String personPassword;
  @Property
  private String personName;
  @Property
  private String personIdCard;
  @Property
  private String personTel;
  @Property
  private Integer personUsestatus;
  @Property
  private String personMemo;
  @Property
  private String personWorknumber;
  @Property
  private String personRfid;
  @Property
  private Integer personType;
  /**
   * 在person实体中有个 字段personBindAuth，标定权限控制1=可标2=不可标， <br>
   * 终端 先抄表默认1 稽核默认2，如果接收到返回的值不一样，就用返回的值
   */
  @Property
  private Integer personBindAuth;

  @Property
  private   Long    loginTime;
  @Generated(hash = 1672043898)
public PersonEntity(Long id, String personId, String personAccount,
        String personPassword, String personName, String personIdCard,
        String personTel, Integer personUsestatus, String personMemo,
        String personWorknumber, String personRfid, Integer personType,
        Integer personBindAuth, Long loginTime) {
    this.id = id;
    this.personId = personId;
    this.personAccount = personAccount;
    this.personPassword = personPassword;
    this.personName = personName;
    this.personIdCard = personIdCard;
    this.personTel = personTel;
    this.personUsestatus = personUsestatus;
    this.personMemo = personMemo;
    this.personWorknumber = personWorknumber;
    this.personRfid = personRfid;
    this.personType = personType;
    this.personBindAuth = personBindAuth;
    this.loginTime = loginTime;
}
@Generated(hash = 69356185)
  public PersonEntity() {
  }
  public String getPersonId() {
      return this.personId;
  }
  public void setPersonId(String personId) {
      this.personId = personId;
  }
  public String getPersonAccount() {
      return this.personAccount;
  }
  public void setPersonAccount(String personAccount) {
      this.personAccount = personAccount;
  }
  public String getPersonPassword() {
      return this.personPassword;
  }
  public void setPersonPassword(String personPassword) {
      this.personPassword = personPassword;
  }
  public String getPersonName() {
      return this.personName;
  }
  public void setPersonName(String personName) {
      this.personName = personName;
  }
  public String getPersonIdCard() {
      return this.personIdCard;
  }
  public void setPersonIdCard(String personIdCard) {
      this.personIdCard = personIdCard;
  }
  public String getPersonTel() {
      return this.personTel;
  }
  public void setPersonTel(String personTel) {
      this.personTel = personTel;
  }
  public Integer getPersonUsestatus() {
      return this.personUsestatus;
  }
  public void setPersonUsestatus(Integer personUsestatus) {
      this.personUsestatus = personUsestatus;
  }
  public String getPersonMemo() {
      return this.personMemo;
  }
  public void setPersonMemo(String personMemo) {
      this.personMemo = personMemo;
  }
  public String getPersonWorknumber() {
      return this.personWorknumber;
  }
  public void setPersonWorknumber(String personWorknumber) {
      this.personWorknumber = personWorknumber;
  }
  public String getPersonRfid() {
      return this.personRfid;
  }
  public void setPersonRfid(String personRfid) {
      this.personRfid = personRfid;
  }
  public Integer getPersonType() {
      return this.personType;
  }
  public void setPersonType(Integer personType) {
      this.personType = personType;
  }
  public Integer getPersonBindAuth() {
      return this.personBindAuth;
  }
  public void setPersonBindAuth(Integer personBindAuth) {
      this.personBindAuth = personBindAuth;
  }
public Long getLoginTime() {
    return this.loginTime;
}
public void setLoginTime(Long loginTime) {
    this.loginTime = loginTime;
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
}
