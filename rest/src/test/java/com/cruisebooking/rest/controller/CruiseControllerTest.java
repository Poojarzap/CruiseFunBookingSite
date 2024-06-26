package com.cruisebooking.rest.controller;

import com.cruisebooking.rest.model.BookingIdCounter;
import com.cruisebooking.rest.model.BookingModel;
import com.cruisebooking.rest.model.CruiseModel;
import com.cruisebooking.rest.model.UserModel;
import com.cruisebooking.rest.service.CruiseServiceInterface;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class CruiseControllerTest {
    @Mock
    private CruiseServiceInterface cruiseServiceInterface;

    @InjectMocks
    private CruiseController cruiseController;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private BookingIdCounter bookingIdCounter; // Mock for BookingIdCounter


    CruiseModel c1,c2,c4;
    private UserModel user;
    List<CruiseModel> expectedCruiseList = new ArrayList<>();
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        c1 = new CruiseModel("101", "Cruise1", "Ship1", "s1", "s1", 35000.0, 7, 3, new Date(2024 - 5 - 4));
        c2 = new CruiseModel("102", "Cruise2", "Ship2", "s2", "d2", 38000.0, 7, 3, new Date(2024 - 5 - 5));
        c4 = new CruiseModel("104", "Cruise4", "Ship4", "s1", "d1", 4500, 7, 3, new Date(2024 - 5 - 4));
        expectedCruiseList.add(c1);
        expectedCruiseList.add(c2);
        user=new UserModel("Pooja","Pooja123","Female","p@gmail.com","p123","123456789");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testGetCruiseList() {


        when(cruiseServiceInterface.getCruiseList()).thenReturn(expectedCruiseList);
        ModelAndView modelAndView = cruiseController.getCruiseList();
        // Assertions
        assertEquals("home", modelAndView.getViewName());
        assertEquals(expectedCruiseList, modelAndView.getModel().get("cruiseList"));
    }

    @Test
    void testGetAllCruiseList() {
        // Prepare test data
        List<CruiseModel> expectedCruiseList = new ArrayList<>();
        expectedCruiseList.add(new CruiseModel(/* Add constructor parameters if needed */));

        // Mock behavior of cruiseServiceInterface.getCruiseList()
        when(cruiseServiceInterface.getCruiseList()).thenReturn(expectedCruiseList);

        // Call the controller method
        ModelAndView modelAndView = cruiseController.getAllCruiseList();

        // Assertions
        assertEquals("userHome", modelAndView.getViewName());
        assertEquals(expectedCruiseList, modelAndView.getModel().get("searchResults"));
    }
    @Test
    void testFetchCruise() {
        // Mock behavior of cruiseServiceInterface.findCruiseById()
        System.out.println("c2 :" +c2);
        when(cruiseServiceInterface.findCruiseById("102")).thenReturn(c2); //If given 103. gives error
        ModelAndView modelAndView = cruiseController.fetchCruise("102");
        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("home");
        assertThat(modelAndView.getModel().get("cruiseList")).isEqualTo(c2);
        assertThat(modelAndView.getModel().containsKey("message")).isFalse();
        assertThat(modelAndView.getModel().containsKey("showAlert")).isFalse();
    }

    @Test
    void testAddCruise_WhenCruiseIdExists() {

        CruiseModel c2= new CruiseModel("102", "Cruise3", "Ship3", "s3", "d3", 6700, 7, 3, new Date(2024 - 5 - 4));
        // Mock behavior of cruiseServiceInterface.findCruiseById()
        when(cruiseServiceInterface.findCruiseById(c2.getCruiseId())).thenReturn(c2);
        System.out.println("Before adding : "+expectedCruiseList);
        // Call the controller method
        ModelAndView modelAndView = cruiseController.addCruise(c2);
//        expectedCruiseList.add(c2);
        System.out.println("After adding : "+expectedCruiseList);
        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("home");
        assertThat(modelAndView.getModel().get("message")).isEqualTo("CruiseId already exists");
        assertThat(modelAndView.getModel().get("showAlert")).isEqualTo(true);
        // Verify service method calls
        verify(cruiseServiceInterface, times(0)).createCruiseInfo(any(CruiseModel.class));
        verify(cruiseServiceInterface, times(0)).getCruiseList();
    }

    @Test
    void testAddCruise_WhenCruiseIdDoesNotExist() {
        // Prepare test data
        CruiseModel c3 = new CruiseModel("103", "Cruise3", "Ship3", "s3", "d3", 6700, 7, 3, new Date(2024 - 5 - 4));
        List<CruiseModel> expectedCruiseList = new ArrayList<>();
        expectedCruiseList.add(c3);

        // Mock behavior of findCruiseById() to throw an exception
        when(cruiseServiceInterface.findCruiseById(c3.getCruiseId()))
                .thenThrow(new RuntimeException("No Cruise with the given Id"));

        // Mock behavior of createCruiseInfo() to return a CruiseModel
        when(cruiseServiceInterface.createCruiseInfo(any(CruiseModel.class))).thenReturn(c3);

        // Mock behavior of getCruiseList()
        when(cruiseServiceInterface.getCruiseList()).thenReturn(expectedCruiseList);

        // Call the controller method
        ModelAndView modelAndView = cruiseController.addCruise(c3);

        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("home");
        assertThat(modelAndView.getModel().get("message")).isEqualTo("Added Successfully");
        assertThat(modelAndView.getModel().get("showAlert")).isEqualTo(true);
        assertThat(modelAndView.getModel().get("cruiseList")).isEqualTo(expectedCruiseList);
    }



    @Test
    void testUpdateCruiseData() {

        System.out.println(c2.getCruiseName());
        // Prepare test data
        CruiseModel cruiseModel = new CruiseModel("102","c2","s2","so2","de2",9080,60,50,new Date(2012-3-5));
        System.out.println(cruiseModel.getCruiseName());
        // Call the controller method
        ModelAndView modelAndView = cruiseController.updateCruiseData(cruiseModel);

        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("home");

        // Verify service method call
//        verify(cruiseServiceInterface, times(1)).updateCruieData(any(CruiseModel.class));
    }

    @Test
    void testDetailsofShip() {
        // Prepare test data
        List<CruiseModel> expectedSearchResults = new ArrayList<>();
        expectedSearchResults.add(c1);
        expectedSearchResults.add(c4);
        // Mock behavior of cruiseServiceInterface.getCruiseList()
        when(cruiseServiceInterface.getCruiseList()).thenReturn(expectedSearchResults);

        // Set the static user in the controller
        CruiseController.user = user;

        // Call the controller method
        ModelAndView modelAndView = cruiseController.detailsofShip();

        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("userHome");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("searchResults");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("searchResults", expectedSearchResults);
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("userInfo");
        assertThat(modelAndView.getModel().get("userInfo")).isEqualTo(user);
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("bookingModel");

        // Optionally, verify if getCruiseList() was called once
        verify(cruiseServiceInterface, times(1)).getCruiseList();
    }

    @Test
    void testDetailsofShipParams() {
        // Prepare test data
        Date date = new Date(2024 - 5 - 4); // Replace with a valid date
        String source = "s1";
        String destination = "d1";
        double startPrice = 3000.0;
        double endPrice = 36000.0;
        List<CruiseModel> expectedSearchResults = new ArrayList<>();
        expectedSearchResults.add(c1);
        expectedSearchResults.add(c4);

        // Mock behavior of cruiseServiceInterface.searchCruises()
        when(cruiseServiceInterface.searchCruises(date, source, destination, startPrice, endPrice))
                .thenReturn(expectedSearchResults);
        // Set the static user in the controller
        CruiseController.user = user;
        // Call the controller method
        ModelAndView modelAndView = cruiseController.detailsofShip(date, source, destination, startPrice, endPrice);
        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("userHome");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("searchResults");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("searchResults", expectedSearchResults);
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("userInfo");
        assertThat(modelAndView.getModel().get("userInfo")).isEqualTo(user);
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("bookingModel");
        // Optionally, verify if searchCruises() was called once with the correct parameters
        verify(cruiseServiceInterface, times(1)).searchCruises(date, source, destination, startPrice, endPrice);
    }

    @Test
    void testBookSeats() {
        // Prepare test data
        String userPhone = "1234567890";
        String cruiseId = "123";
        double price = 100.0;

        // Call the controller method
        ModelAndView modelAndView = cruiseController.bookSeats(userPhone, cruiseId, price);
        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("BookSeats");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("bookingModel");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("userInfo", userPhone);
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("cruise", cruiseId);
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("price", price);

    }

    @Test
    void testBookCruise_WhenCruiseExistsAndSeatsAvailable() {
        // Prepare test data
        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingCruise("123"); // Assuming cruise ID exists
        bookingModel.setNumberOfSeats(5); // Assuming 5 seats requested
        bookingModel.setTotalPrice(500.0); // Assuming total price

        CruiseModel cruise = new CruiseModel("123", "Cruise1", "Ship1", "s1", "d1", 35000.0, 10, 5, null); // Mock cruise with available seats
        int initialAvailableSeats = cruise.getAvailableSeats();

        // Mock behavior of findCruiseById() to return the mock cruise
        when(cruiseServiceInterface.findCruiseById("123")).thenReturn(cruise);

        // Mock behavior of mongoTemplate.findAndModify() to mock BookingIdCounter
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(BookingIdCounter.class)))
                .thenReturn(new BookingIdCounter("bookingIdCounter", 1));

        // Mock behavior of void methods using doNothing()
        doNothing().when(cruiseServiceInterface).updateCruieData(any(CruiseModel.class)); //updateCruiseData returns void
        when(cruiseServiceInterface.createBookInfo(any(BookingModel.class))).thenReturn(bookingModel);

        // Call the controller method
        ModelAndView modelAndView = cruiseController.bookCruise(bookingModel);

        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("userHome");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("message", "Booking successful");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("showAlert", true);

        // Verify service method calls
        verify(cruiseServiceInterface, times(1)).findCruiseById("123");
        verify(cruiseServiceInterface, times(1)).updateCruieData(cruise);
        verify(cruiseServiceInterface, times(1)).createBookInfo(bookingModel);
        assertThat(cruise.getAvailableSeats()).isEqualTo(initialAvailableSeats - bookingModel.getNumberOfSeats());

        // Verify mongoTemplate.findAndModify() call with specific arguments
        verify(mongoTemplate, times(1)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(BookingIdCounter.class));
    }


    @Test
    void testBookCruise_WhenCruiseNotFound() {
        // Prepare test data
        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingCruise("456"); // Assuming cruise ID does not exist

        // Mock behavior of findCruiseById() to return null (cruise not found)
        when(cruiseServiceInterface.findCruiseById("456")).thenReturn(null);

        // Call the controller method
        ModelAndView modelAndView = cruiseController.bookCruise(bookingModel);

        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("userHome");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("message", "Cruise not found");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("showAlert", true);

        // Verify service method calls
        verify(cruiseServiceInterface, times(1)).findCruiseById("456");
        verify(cruiseServiceInterface, never()).updateCruieData(any(CruiseModel.class));
        verify(cruiseServiceInterface, never()).createBookInfo(any(BookingModel.class));
    }
    @Test
    void testBookCruise_WhenInsufficientSeats() {
        // Prepare test data
        BookingModel bookingModel = new BookingModel();
        bookingModel.setBookingCruise("789"); // Assuming cruise ID exists
        bookingModel.setNumberOfSeats(15); // Assuming 15 seats requested

        CruiseModel cruise = new CruiseModel("789", "Cruise2", "Ship2", "s2", "d2", 38000.0, 10, 5, null); // Mock cruise with limited available seats
        when(cruiseServiceInterface.findCruiseById("789")).thenReturn(cruise);

        // Call the controller method
        ModelAndView modelAndView = cruiseController.bookCruise(bookingModel);

        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("userHome");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("message", "Not enough seats available");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsEntry("showAlert", true);

        // Verify service method calls
        verify(cruiseServiceInterface, times(1)).findCruiseById("789");
        verify(cruiseServiceInterface, never()).updateCruieData(any(CruiseModel.class));
        verify(cruiseServiceInterface, never()).createBookInfo(any(BookingModel.class));
    }

    @Test
    void testBookingResult() {
        // Prepare test data
        String bookingUser = "123456789"; // Assuming the user's phone number
        List<BookingModel> bookingModelList = new ArrayList<>();
        BookingModel booking1 = new BookingModel();
        booking1.setBookingCruise("123");
        booking1.setBookingUser(bookingUser);
        bookingModelList.add(booking1);

        BookingModel booking2 = new BookingModel();
        booking2.setBookingCruise("456");
        booking2.setBookingUser(bookingUser);
        bookingModelList.add(booking2);

        CruiseModel cruise1 = new CruiseModel("123", "Cruise1", "Ship1", "s1", "d1", 35000.0, 10, 5, null);
        CruiseModel cruise2 = new CruiseModel("456", "Cruise2", "Ship2", "s2", "d2", 38000.0, 10, 5, null);

        UserModel userModel = new UserModel("Pooja", "Pooja123", "Female", "p@gmail.com", "p123", bookingUser);

        // Mock behavior of cruiseServiceInterface.getBookingInfo()
        when(cruiseServiceInterface.getBookingInfo(bookingUser)).thenReturn(bookingModelList);

        // Mock behavior of cruiseServiceInterface.findCruiseById()
        when(cruiseServiceInterface.findCruiseById("123")).thenReturn(cruise1);
        when(cruiseServiceInterface.findCruiseById("456")).thenReturn(cruise2);

        // Mock behavior of cruiseServiceInterface.findUserFromDb()
        when(cruiseServiceInterface.findUserFromDb(bookingUser)).thenReturn(userModel);

        // Set the static user in the controller
        CruiseController.user = userModel;

        // Call the controller method
        ModelAndView modelAndView = cruiseController.bookingResult();

        // Assertions using AssertJ
        assertThat(modelAndView.getViewName()).isEqualTo("userHome");
        AssertionsForInterfaceTypes.assertThat(modelAndView.getModel()).containsKey("combinedProject");

        List<Map<String, Object>> combinedDataList = (List<Map<String, Object>>) modelAndView.getModel().get("combinedProject");
        AssertionsForInterfaceTypes.assertThat(combinedDataList).hasSize(2);

        Map<String, Object> combinedData1 = combinedDataList.get(0);
        AssertionsForInterfaceTypes.assertThat(combinedData1).containsEntry("bookingModel", booking1);
        AssertionsForInterfaceTypes.assertThat(combinedData1).containsEntry("cruiseModel", cruise1);
        AssertionsForInterfaceTypes.assertThat(combinedData1).containsEntry("userModel", userModel);

        Map<String, Object> combinedData2 = combinedDataList.get(1);
        AssertionsForInterfaceTypes.assertThat(combinedData2).containsEntry("bookingModel", booking2);
        AssertionsForInterfaceTypes.assertThat(combinedData2).containsEntry("cruiseModel", cruise2);
        AssertionsForInterfaceTypes.assertThat(combinedData2).containsEntry("userModel", userModel);

        // Verify service method calls
        verify(cruiseServiceInterface, times(1)).getBookingInfo(bookingUser);
        verify(cruiseServiceInterface, times(2)).findCruiseById(anyString());
        verify(cruiseServiceInterface, times(2)).findUserFromDb(bookingUser);
    }

    @Test
    public void testFetchAllBookings() {
        // Arrange
        BookingModel booking1 = new BookingModel();
        booking1.setBookingCruise("1L");
        booking1.setBookingUser("12345678");

        BookingModel booking2 = new BookingModel();
        booking2.setBookingCruise("2L");
        booking2.setBookingUser("87654321");

        CruiseModel cruise1 = new CruiseModel();
        cruise1.setCruiseId("1L");

        CruiseModel cruise2 = new CruiseModel();
        cruise2.setCruiseId("2L");

        UserModel user1 = new UserModel("Pooja", "Pooja123", "Female", "p@gmail.com", "p123", "12345678");
        UserModel user2 = new UserModel("Pooja", "Pooja123", "Female", "p@gmail.com", "p123", "87654321");


        when(cruiseServiceInterface.getAllBookings()).thenReturn(Arrays.asList(booking1, booking2));
        when(cruiseServiceInterface.findCruiseById("1L")).thenReturn(cruise1);
        when(cruiseServiceInterface.findCruiseById("2L")).thenReturn(cruise2);
        when(cruiseServiceInterface.findUserFromDb("12345678")).thenReturn(user1);
        when(cruiseServiceInterface.findUserFromDb("87654321")).thenReturn(user2);

        // Act
        ModelAndView modelAndView = cruiseController.fetchAllBookings();

        // Assert
        assertEquals("home", modelAndView.getViewName());
        List<Map<String, Object>> combinedProject = (List<Map<String, Object>>) modelAndView.getModel().get("combinedProject");

        assertEquals(2, combinedProject.size());

        Map<String, Object> combinedData1 = combinedProject.get(0);
        assertEquals(booking1, combinedData1.get("bookingModel"));
        assertEquals(cruise1, combinedData1.get("cruiseModel"));
        assertEquals(user1, combinedData1.get("userModel"));

        Map<String, Object> combinedData2 = combinedProject.get(1);
        assertEquals(booking2, combinedData2.get("bookingModel"));
        assertEquals(cruise2, combinedData2.get("cruiseModel"));
        assertEquals(user2, combinedData2.get("userModel"));
    }

    @Test
    public void testCancelBooking() {
        // Arrange
        String bookingId = "1";
        String numberOfSeats = "2";

        BookingModel booking = new BookingModel();
        booking.setBookingCruise("101");

        CruiseModel cruise = new CruiseModel();
        cruise.setCruiseId("101");
        cruise.setAvailableSeats(50);

        when(cruiseServiceInterface.findBookingById(bookingId)).thenReturn(booking);
        when(cruiseServiceInterface.findCruiseById(booking.getBookingCruise())).thenReturn(cruise);

        // Act
        ModelAndView modelAndView = cruiseController.cancelBooking(bookingId, numberOfSeats);

        // Assert
        assertEquals("userHome", modelAndView.getViewName());

        verify(cruiseServiceInterface).findBookingById(bookingId);
        verify(cruiseServiceInterface).findCruiseById(booking.getBookingCruise());
        verify(cruiseServiceInterface).updateCruieData(cruise);
        verify(cruiseServiceInterface).cancelBookingById(bookingId);

        assertEquals(52, cruise.getAvailableSeats());  // 50 + 2 = 52
    }
}