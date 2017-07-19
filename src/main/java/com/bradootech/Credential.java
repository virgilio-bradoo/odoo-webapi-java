package com.bradootech;

public class Credential {
    public String url;
    public String db;
    public String username;
    public String password;

    public Credential(String url, String db, String username, String password) {
        this.url = url;
        this.db = db;
        this.username = username;
        this.password = password;
    }
}
