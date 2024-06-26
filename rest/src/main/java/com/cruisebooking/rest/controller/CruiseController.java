package com.cruisebooking.rest.controller;

import ch.qos.logback.classic.net.SyslogAppender;
import com.cruisebooking.rest.model.BookingIdCounter;
import com.cruisebooking.rest.model.BookingModel;
import com.cruisebooking.rest.model.CruiseModel;
import com.cruisebooking.rest.model.UserModel;
import com.cruisebooking.rest.service.CruiseServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

@RestController
//Controller class
public class CruiseController {
    public static UserModel user;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${authentication.phoneNumber}")
    private String validPhoneNumber;

    @Value("${authentication.password}")
    private String validPassword;
    @Autowired
    CruiseServiceInterface cruiseServiceInterface;

    public CruiseController(CruiseServiceInterface cruiseServiceInterface) {
        this.cruiseServiceInterface = cruiseServiceInterface;
    }

    @GetMapping("/cruise/book-now")
    public ModelAndView bookNow() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView; // Return the name of the HTML file for the booking page
    }
    @PostMapping("/login")
    public ModelAndView login(@RequestParam String userPhone, @RequestParam String userPassword) {
        ModelAndView modelAndView = new ModelAndView();

            if (validPhoneNumber.equals(userPhone) && validPassword.equals(userPassword)) {
                modelAndView.setViewName("home");
            } else {
//                user = cruiseServiceInterface.findUserFromDb(userPhone);
////                System.out.println(user.getName());
//                if (user != null && user.getUserPassword().equals(userPassword)) {
//                    modelAndView.setViewName("userHome");
//                    modelAndView.addObject("userInfo", user);
//                }
                try {
                    user = cruiseServiceInterface.findUserFromDb(userPhone);
                    if (user != null) {
                        if (user.getUserPassword().equals(userPassword)) {
                            modelAndView.setViewName("userHome");
                            modelAndView.addObject("userInfo", user);
                        } else {
                            modelAndView.setViewName("index");
                            modelAndView.addObject("errorMessage", "Invalid password. Please try again.");
                            modelAndView.addObject("showAlert", true);
                        }
                    } else {
                        modelAndView.setViewName("index");
                        modelAndView.addObject("errorMessage", "Invalid phone number. Please try again.");
                        modelAndView.addObject("showAlert", true);
                    }
                } catch (Exception e) {
                    modelAndView.setViewName("index");
                    modelAndView.addObject("errorMessage", "Invalid phone number or password. Please try again.");
                    modelAndView.addObject("showAlert", true);
                }
            }
        return modelAndView;
        }

    @PostMapping("/userRegister")
    public ModelAndView userRegister(@ModelAttribute UserModel userModel) {
        ModelAndView modelAndView = new ModelAndView();
        // Check if the phone number already exists in the database
        try {
            UserModel user = cruiseServiceInterface.findUserFromDb(userModel.getUserPhone());
                modelAndView.setViewName("index");
                modelAndView.addObject("errorMessage", "Phone number already exists. Please enter a different phone number.");
                modelAndView.addObject("showAlert", true);
            } catch (NoSuchElementException e) {
        cruiseServiceInterface.createUserInfo(userModel);
        modelAndView.setViewName("index");
        }
        return modelAndView;
    }



//    @PostMapping("/userRegister")
//    public ModelAndView userRegister(@ModelAttribute UserModel userModel) {
//        ModelAndView modelAndView = new ModelAndView();
//        // Check if the phone number already exists in the database
//        UserModel user = cruiseServiceInterface.findUserFromDb(userModel.getUserPhone());
//        if (user != null) {
//            modelAndView.setViewName("index");
//            modelAndView.addObject("errorMessage", "Phone number already exists. Please enter a different phone number.");
//            modelAndView.addObject("showAlert", true);
//        } else {
//            cruiseServiceInterface.createUserInfo(userModel);
//            modelAndView.setViewName("index");
//        }
//        return modelAndView;
//    }

    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @GetMapping("/getCruiseList") //For staff
    public ModelAndView getCruiseList() {
        List<CruiseModel> cruiseList = cruiseServiceInterface.getCruiseList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("cruiseList", cruiseList);
        return modelAndView;
    }

    @GetMapping("/getAllCruiseList") //For users
    public ModelAndView getAllCruiseList() {
        List<CruiseModel> cruiseList = cruiseServiceInterface.getCruiseList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userHome");
        modelAndView.addObject("searchResults", cruiseList);
        return modelAndView;
    }
    @GetMapping("/fetchCruiseDataById")
    public ModelAndView fetchCruise(@RequestParam String cruiseId){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        try{
        CruiseModel cd=cruiseServiceInterface.findCruiseById(cruiseId);
        modelAndView.addObject("cruiseList",cd);
    }
        catch (Exception e){
            modelAndView.addObject("message", "No Cruise with the given Id");
            modelAndView.addObject("showAlert", true);
        }
        return modelAndView;
    }


    @PostMapping("/addCruise")
    public ModelAndView addCruise(@ModelAttribute CruiseModel cruiseModel) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        try {
            CruiseModel cruiseModel1 = cruiseServiceInterface.findCruiseById(cruiseModel.getCruiseId());
            modelAndView.addObject("message", "CruiseId already exists");
            }
        catch (Exception e){
            cruiseServiceInterface.createCruiseInfo(cruiseModel);
            List<CruiseModel> cruiseList = cruiseServiceInterface.getCruiseList();
            modelAndView.addObject("message", "Added Successfully");
            modelAndView.addObject("cruiseList", cruiseList);
        }
        modelAndView.addObject("showAlert", true);
        return modelAndView;
    }

    @PutMapping("/updateCruise")
    public ModelAndView updateCruiseData(@ModelAttribute CruiseModel cruiseModel)
    {
        System.out.println(cruiseModel.getCruiseId());
        cruiseServiceInterface.updateCruieData(cruiseModel);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        return modelAndView;
   }

    @GetMapping("/searchAll")
    public ModelAndView detailsofShip(){
        List<CruiseModel> searchResults = cruiseServiceInterface.getCruiseList();
        // Create a ModelAndView and add the search results to it
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userHome");
        System.out.println(user.getName());
        modelAndView.addObject("searchResults", searchResults);
        modelAndView.addObject("userInfo",user);
        modelAndView.addObject("bookingModel", new BookingModel());
        return modelAndView;
    }

    @GetMapping("/search") //Searching based on source, destination and price range
    public ModelAndView detailsofShip(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @RequestParam String source, @RequestParam String destination, @RequestParam double startPrice, @RequestParam double endPrice){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("userHome");
        System.out.println(date);

            List<CruiseModel> searchResults = cruiseServiceInterface.searchCruises(date,source, destination, startPrice, endPrice);
            if (searchResults.isEmpty()) {
                modelAndView.addObject("message", "No results found."); // Customize error message
                modelAndView.addObject("showAlert",true);
                modelAndView.addObject("userInfo", user);

            }else {
                modelAndView.addObject("searchResults", searchResults);
                modelAndView.addObject("userInfo", user);
                modelAndView.addObject("bookingModel", new BookingModel());
        }

        return modelAndView;
    }

//    @GetMapping("/searchByPrice")
//    public ModelAndView searchByPrice(@RequestParam double startPrice,@RequestParam double endPrice) {
//        List<CruiseModel> searchResults= cruiseServiceInterface.searchCruisesByPriceRange(startPrice,endPrice);
//        System.out.println(searchResults);
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("userHome");
//        System.out.println(user.getName());
//        modelAndView.addObject("searchResults", searchResults);
//        modelAndView.addObject("userInfo",user);
//        modelAndView.addObject("bookingModel", new BookingModel());
//        return modelAndView;
//    }

    @PostMapping("/bookSeats")
    public ModelAndView bookSeats(@RequestParam String userPhone,@RequestParam String cruiseId,@RequestParam double price) {
        System.out.println(userPhone);
        System.out.println(cruiseId);
        System.out.println(price);
        // Assuming you have a method to retrieve the booking model
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("bookingModel", new BookingModel());
        modelAndView.addObject("userInfo", userPhone);
        modelAndView.addObject("cruise",cruiseId);
        modelAndView.addObject("price",price);
        modelAndView.setViewName("BookSeats"); // name of the thymleaf
        return modelAndView;
    }

//    @PostMapping("/bookCruise")
//    public ModelAndView bookCruise(@ModelAttribute BookingModel bookingModel) {
//        System.out.println(bookingModel.getNumberOfSeats()); //Print number of seats requested
//        CruiseModel cd=cruiseServiceInterface.findCruiseById(bookingModel.getBookingCruise());
//        int available=cd.getAvailableSeats();
//        System.out.println(available);
//        ModelAndView modelAndView = new ModelAndView();
//        if(available<bookingModel.getNumberOfSeats())
//        {
//            modelAndView.addObject("errorMessage", "No enough seats");
//            modelAndView.addObject("showAlert", true);
//            modelAndView.setViewName("userHome");
//        }
//        else {
//            int generatedBookingId = generateBookingId();
//            bookingModel.setBookingId(String.valueOf(generatedBookingId));
//            // Save the booking details to the database
//            cruiseServiceInterface.createBookInfo(bookingModel);
//            int newAvailable=available-bookingModel.getNumberOfSeats();
//            modelAndView.addObject("errorMessage", "Booking Successfull");
//            modelAndView.addObject("showAlert", true);
//            modelAndView.setViewName("userHome");
//        }
//
//        return modelAndView;
//    }

    @PostMapping("/bookCruise") //caculations to book cruise with some conditions.
    public ModelAndView bookCruise(@ModelAttribute BookingModel bookingModel) {
        int requestedSeats = bookingModel.getNumberOfSeats();
        System.out.println(bookingModel.getNumberOfSeats());
        System.out.println(bookingModel.getTotalPrice());
        CruiseModel cruise = cruiseServiceInterface.findCruiseById(bookingModel.getBookingCruise());
        if (cruise == null) {
            // Handle the case where the cruise is not found
            ModelAndView modelAndView = new ModelAndView("userHome");
            modelAndView.addObject("message", "Cruise not found");
            modelAndView.addObject("showAlert", true);
            return modelAndView;
        }
        int availableSeats = cruise.getAvailableSeats();
        if (availableSeats < requestedSeats) {
            // Not enough available seats
            ModelAndView modelAndView = new ModelAndView("userHome");
            modelAndView.addObject("message", "Not enough seats available");
            modelAndView.addObject("showAlert", true);
            return modelAndView;
        }

        // Sufficient seats available, proceed with booking
        int generatedBookingId = generateBookingId();
        bookingModel.setBookingId(String.valueOf(generatedBookingId));

        // Update available seats count
        int newAvailableSeats = availableSeats - bookingModel.getNumberOfSeats();
        cruise.setAvailableSeats(newAvailableSeats);
        // Save updated cruise details
        cruiseServiceInterface.updateCruieData(cruise);

        // Save the booking details to the database
        cruiseServiceInterface.createBookInfo(bookingModel);

        ModelAndView modelAndView = new ModelAndView("userHome");
        modelAndView.addObject("message", "Booking successful");
        modelAndView.addObject("showAlert", true);
        return modelAndView;
    }


    private int generateBookingId() {
        // Use MongoDB's findAndModify to atomically increment the booking ID
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is("bookingIdCounter"));
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        BookingIdCounter counter = mongoTemplate.findAndModify(query, update, options, BookingIdCounter.class);
        return counter.getSequence();
    }



    @GetMapping("/bookings")
    public ModelAndView bookingResult(){
//        String bookingUser= user.getUserPhone();
//        List<BookingModel> bookingModelList=cruiseServiceInterface.getBookingInfo(bookingUser);
//        List<CruiseModel> cruiseList=new ArrayList<>();
//        List<UserModel> userList=new ArrayList<>();
//
//
//        for(int i=0;i<bookingModelList.size();i++) {
//            BookingModel bd=bookingModelList.get(i);
//            cruiseList.add(cruiseServiceInterface.findCruiseById(bd.getBookingCruise()));
//            userList.add(cruiseServiceInterface.findUserFromDb(bd.getBookingUser()));
//        }
//        System.out.println(bookingModelList);
//        System.out.println(cruiseList);
//        System.out.println(userList);
//
//
//        ModelAndView modelAndView=new ModelAndView();
//        modelAndView.addObject("bookingResult",bookingModelList);
//        modelAndView.addObject("cruiseResult",cruiseList);
//        modelAndView.addObject("userResult",userList);
//        modelAndView.setViewName("userHome");
//        return modelAndView;
//    }
        String bookingUser = user.getUserPhone();
        List<BookingModel> bookingModelList = cruiseServiceInterface.getBookingInfo(bookingUser);
        List<CruiseModel> cruiseList = new ArrayList<>();
        List<UserModel> userList = new ArrayList<>();
        List<Map<String, Object>> combinedDataList = new ArrayList<>();

        for (int i = 0; i < bookingModelList.size(); i++) {
            BookingModel bookingModel = bookingModelList.get(i);
            CruiseModel cruiseModel = cruiseServiceInterface.findCruiseById(bookingModel.getBookingCruise());
            UserModel userModel = cruiseServiceInterface.findUserFromDb(bookingModel.getBookingUser());

            cruiseList.add(cruiseModel);
            userList.add(userModel);

            // Create a Map to represent combined data
            Map<String, Object> combinedData = new HashMap<>();
            combinedData.put("bookingModel", bookingModel);
            combinedData.put("cruiseModel", cruiseModel);
            combinedData.put("userModel", userModel);
            // Add the Map to the list
            combinedDataList.add(combinedData);
        }
        // Print or use the combinedDataList as needed
        System.out.println(combinedDataList);
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("combinedProject",combinedDataList);
        modelAndView.setViewName("userHome");
        return modelAndView;
    }

    @GetMapping("/fetchBookings")
    public ModelAndView fetchAllBookings(){
        List<BookingModel> bookingModelList=cruiseServiceInterface.getAllBookings();
        List<CruiseModel> cruiseList = new ArrayList<>();
        List<UserModel> userList = new ArrayList<>();
        List<Map<String, Object>> combinedDataList = new ArrayList<>();

        for (int i = 0; i < bookingModelList.size(); i++) {
            BookingModel bookingModel = bookingModelList.get(i);
            CruiseModel cruiseModel = cruiseServiceInterface.findCruiseById(bookingModel.getBookingCruise());
            UserModel userModel = cruiseServiceInterface.findUserFromDb(bookingModel.getBookingUser());

            cruiseList.add(cruiseModel);
            userList.add(userModel);

            // Create a Map to represent combined data
            Map<String, Object> combinedData = new HashMap<>();
            combinedData.put("bookingModel", bookingModel);
            combinedData.put("cruiseModel", cruiseModel);
            combinedData.put("userModel", userModel);
            // Add the Map to the list
            combinedDataList.add(combinedData);
        }
        // Print or use the combinedDataList as needed
        System.out.println(combinedDataList);
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("combinedProject",combinedDataList);
        modelAndView.setViewName("home");
//        ModelAndView modelAndView=new ModelAndView();
//        modelAndView.addObject("bookings",bookingModelList);
//        modelAndView.setViewName("home");
        return modelAndView;
    }


    @DeleteMapping("/cancel-booking/{bookingId}/{numberOfSeats}")
    public ModelAndView cancelBooking(@PathVariable("bookingId") String bookingId,@PathVariable("numberOfSeats") String numberOfSeats) {
        System.out.println(bookingId);
        System.out.println(numberOfSeats);
        BookingModel bd=cruiseServiceInterface.findBookingById(bookingId);
        CruiseModel cd=cruiseServiceInterface.findCruiseById(bd.getBookingCruise());
        int availableSeats=cd.getAvailableSeats();
        int seatsAfterCancel= availableSeats+Integer.parseInt(numberOfSeats);
        cd.setAvailableSeats(seatsAfterCancel);
        cruiseServiceInterface.updateCruieData(cd);
        cruiseServiceInterface.cancelBookingById(bookingId);
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("userHome");
        return modelAndView;

    }

}
