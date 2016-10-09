package com.dglasser.intellidrive.POJO;

public class StringPair {
    private String tripType;
    private String name;

    public StringPair(String tripType, String name) {
        this.tripType = tripType;
        this.name = name;
    }

    public String getTripType() {
        return tripType;
    }

    public String getName() {
        return name;
    }
}
