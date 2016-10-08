package com.dglasser.intellidrive.POJO;

/**
 * Basic POJO. Used to retrieve token from server after login.
 */
public class Token {
    /**
     * Token from server.
     */
    String token;

    public Token(String token) {
        this.token = token;
    }

    /**
     * Gets token.
     * @return Token.
     */
    public String getToken() {
        return token;
    }
}
