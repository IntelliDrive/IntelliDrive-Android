package com.dglasser.intellidrive.POJO;

/**
 * Basic POJO used for sending POST requests to server for login.
 */
public class LoginObject {
    private String username;
    private String password;

    /**
     * Basic constructor. Takes a username and password.
     * @param username Username.
     * @param password Password.
     */
    public LoginObject(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Get username.
     * @return username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get password.
     * @return password.
     */
    public String getPassword() {
        return password;
    }
}
