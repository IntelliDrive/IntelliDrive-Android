package com.dglasser.intellidrive.POJO;

public class RobustTripResponse extends BaseTripResponse {
    private String trip_number;
    private String trip_name;
    private String email;
    private String type;
    private String miles;
    private String start;
    private String end;

    public String getTrip_number() {
        return trip_number;
    }

    public String getTrip_name() {
        return trip_name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getMiles() {
        return miles;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
