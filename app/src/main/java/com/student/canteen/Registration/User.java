package com.student.canteen.Registration;

public class User {
  public String email;
  public String name;
  public String phone;
  public String password;

  public String shopname;

  public String documentUrl;
  public boolean approved;


  public User(String email, String name, String phone, String password, String shopname,String documentUrl,boolean approved) {
    this.email = email;
    this.name = name;
    this.phone = phone;
    this.password = password;
    this.shopname=shopname;
    this.documentUrl = documentUrl;
    this.approved = approved;

  }
}
