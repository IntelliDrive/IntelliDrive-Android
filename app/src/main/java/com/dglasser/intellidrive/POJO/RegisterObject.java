package com.dglasser.intellidrive.POJO;

/**
 * Basic POJO used to deserialize registration objects.
 */
public class RegisterObject {
    private String name;
    private String email;
    private String password;

    /**
     * Public constructor. Takes user's name, email, and password.
     * @param name Name.
     * @param email Email.
     * @param password Password.
     */
    public RegisterObject(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets user's name.
     * @return Name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets user's email.
     * @return User's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets user's password.
     * @return User's password.
     */
    public String getPassword() {
        return password;
    }
}
