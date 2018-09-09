package com.zlcdgroup.mrsei.data.entity;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/9/4 11:02
 */

@Entity(nameInDb = "tb_user")
public class UserEntity  implements Serializable{
  private static final long serialVersionUID = 196217012358394317L;

  @Id(autoincrement = true)
  private   Long    id;


  @Property
  private    String    name;

  @Property
  private    int    age;

  @Generated(hash = 1186854390)
public UserEntity(Long id, String name, int age) {
    this.id = id;
    this.name = name;
    this.age = age;
}

@Generated(hash = 1433178141)
  public UserEntity() {
  }


  public String getName() {
      return this.name;
  }

  public void setName(String name) {
      this.name = name;
  }

  public int getAge() {
      return this.age;
  }

  public void setAge(int age) {
      this.age = age;
  }

public void setId(Long id) {
    this.id = id;
}

public Long getId() {
    return this.id;
}
}
