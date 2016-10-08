package com.dglasser.intellidrive.POJO;

/**
 * Basic POJO used for sending POST requests to server for login.
 */
public class LoginObject {
    private String email;
    private String password;

    /**
     * Basic constructor. Takes a email and password.
     * @param email Username.
     * @param password Password.
     */
    public LoginObject(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Get email.
     * @return email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get password.
     * @return password.
     */
    public String getPassword() {
        return password;
    }
}
