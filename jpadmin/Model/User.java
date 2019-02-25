package com.sdmgapl1a0501.naimur.jpadmin.Model;


public class User {

    private String Name;
    private String Address;
    private String Password;
    private String Phone;
    private  String IsStaff;

    public  User() {


    }

    public User(String name, String address, String password) {
        Name = name;
        Address = address;
        Password = password;
     //   IsStaff= "false";

    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }



    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
