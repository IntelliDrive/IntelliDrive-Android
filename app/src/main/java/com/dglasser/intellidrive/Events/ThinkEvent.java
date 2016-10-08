package com.dglasser.intellidrive.Events;

import org.greenrobot.eventbus.EventBus;

/**
 * Event to broadcast thoughts from chatbot.
 */
public class ThinkEvent extends EventBus {
    /**
     * Chatbot thought.
     */
    private String thought;

    /**
     * Used to determine if there was an error when getting thought.
     */
    private boolean error;

    /**
     * Basic constructor. Takes a thought, and whether or not there was an error when processing
     * request.
     * @param thought Chatbot thought.
     * @param error Whether tor not there was an error.
     */
    public ThinkEvent(String thought, boolean error) {
        this.thought = thought;
        this.error = error;
    }

    /**
     * Gets chatbot thought.
     * @return Chatbot thought.
     */
    public String getThought() {
        return thought;
    }

    /**
     * Determines whether or not there was an error.
     * @return Whether or not there was an error.
     */
    public boolean isError() {
        return error;
    }
}
