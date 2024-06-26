package com.cruisebooking.rest.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "BookingDetails")
public class BookingModel {

    @Id
    @Field("bookingId")
    private String bookingId; // Use int for auto-incrementing ID
    private String bookingUser;
    private String bookingCruise;
    private int numberOfSeats;

    private double totalPrice;


    public BookingModel() {
    }

    public BookingModel(String bookingId, String bookingUser, String bookingCruise,int numberOfSeats,double totalPrice) {
        this.bookingId = bookingId;
        this.bookingUser = bookingUser;
        this.bookingCruise = bookingCruise;
        this.numberOfSeats=numberOfSeats;
        this.totalPrice=totalPrice;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }


    public String getBookingUser() {
        return bookingUser;
    }

    public void setBookingUser(String bookingUser) {
        this.bookingUser = bookingUser;
    }

    public String getBookingCruise() {
        return bookingCruise;
    }

    public void setBookingCruise(String bookingCruise) {
        this.bookingCruise = bookingCruise;
    }
    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
