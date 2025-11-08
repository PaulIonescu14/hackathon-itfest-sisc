package com.example.taskforce;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.util.UUID;

public class User extends RealmObject {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private String email = "";
    private String password = "";
    private long loginTime = System.currentTimeMillis();

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.loginTime = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public long getLoginTime() { return loginTime; }
    public void setLoginTime(long loginTime) { this.loginTime = loginTime; }
}