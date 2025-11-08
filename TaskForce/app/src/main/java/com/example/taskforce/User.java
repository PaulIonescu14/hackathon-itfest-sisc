package com.example.taskforce;

public class User {
    private String mail;
    private String password;
    private String id;
    private String group;

    public User() {
    }

    public User(String mail, String password, String id, String group_id) {
        this.mail = mail;
        this.password = password;
        this.id = id;
        this.group = group_id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup_id(String group_id) {
        this.group = group_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", id='" + id + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}