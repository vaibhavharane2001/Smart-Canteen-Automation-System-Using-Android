package com.student.canteen.ReserveTable;



public class Reservation {
    private String dateTime;
    private String guests;

    public Reservation() {
        // Default constructor required for Firebase
    }

    public Reservation(String dateTime, String guests) {
        this.dateTime = dateTime;
        this.guests = guests;
    }

    // Getters and setters
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getGuests() {
        return guests;
    }

    public void setGuests(String guests) {
        this.guests = guests;
    }
}

