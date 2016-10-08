package com.dglasser.intellidrive.POJO;

/**
 * Base level response object that contains only the barest of essentials for requests.
 */
public class BoringResposeObject {
    /**
     * Message received from server.
     */
    String msg;

    /**
     * Simple getter function for message.
     * @return Message.
     */
    public String getMsg() {
        return msg;
    }
}
