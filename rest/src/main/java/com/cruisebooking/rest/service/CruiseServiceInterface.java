package com.cruisebooking.rest.service;

import com.cruisebooking.rest.model.BookingModel;
import com.cruisebooking.rest.model.CruiseModel;
import com.cruisebooking.rest.model.UserModel;

import java.util.Date;
import java.util.List;

public interface CruiseServiceInterface {
    //Cruise Implementations
    public List<CruiseModel> getCruiseList();
    //public CruiseModel getCruiseInfo(String id);
    public CruiseModel createCruiseInfo(CruiseModel cd);
    public CruiseModel findCruiseById(String cruiseId);
    public void updateCruieData(CruiseModel cd);


    //User Implementations
    public UserModel createUserInfo(UserModel userInfo);
    public UserModel findUserFromDb(String userPhone);
    List<CruiseModel> searchCruises(Date date, String source, String destination, double startPrice, double endPrice);
    //List<CruiseModel> searchCruisesByPriceRange(double startPrice, double endPrice);



    public BookingModel createBookInfo(BookingModel bookingModel);
    public List<BookingModel> getBookingInfo(String bookingUser);
    public List<BookingModel> getAllBookings();
    public BookingModel findBookingById(String bookingId);
    public Boolean cancelBookingById(String bookingId);

}
