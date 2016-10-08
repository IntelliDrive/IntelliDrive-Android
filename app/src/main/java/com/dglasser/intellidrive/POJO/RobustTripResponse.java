package com.dglasser.intellidrive.POJO;

/**
 * More robust trip response. Used for more complicated trip requests that a simple trip request
 * will not cover.
 */
public class RobustTripResponse extends BaseTripResponse {
    /**
     * Trip number.
     */
    private String trip_number;

    /**
     * Trip name.
     */
    private String trip_name;

    /**
     * Email.
     */
    private String email;

    /**
     * Trip type.
     */
    private String type;

    /**
     * Number of miles.
     */
    private String miles;

    /**
     * Start date.
     */
    private String start;

    /**
     * End date.
     */
    private String end;

    /**
     * Gets trip number.
     * @return Trip number.
     */
    public String getTrip_number() {
        return trip_number;
    }

    /**
     * Gets trip name.
     * @return Trip name.
     */
    public String getTrip_name() {
        return trip_name;
    }

    /**
     * Gets user's email.
     * @return User's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets type of trip.
     * @return Type of trip.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets number of miles.
     * @return Number of miles.
     */
    public String getMiles() {
        return miles;
    }

    /**
     * Gets start date.
     * @return Start date.
     */
    public String getStart() {
        return start;
    }

    /**
     * Gets end date.
     * @return End date.
     */
    public String getEnd() {
        return end;
    }
}
