package com.e.evoting;

public class Person {

    private String name;
    private String phone;
    private String email;
    private String aadhar;
    private String address;
    private Boolean voted;

    public Person(String name, String phone, String email, String aadhar, String address, Boolean voted) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.aadhar = aadhar;
        this.address = address;
        this.voted = voted;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

    public Person(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Person(String name, String phone, String email, String aadhar, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.aadhar = aadhar;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    Person() {

    }


}
