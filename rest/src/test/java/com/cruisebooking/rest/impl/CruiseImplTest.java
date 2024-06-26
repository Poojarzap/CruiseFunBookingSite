package com.cruisebooking.rest.impl;

import com.cruisebooking.rest.model.BookingModel;
import com.cruisebooking.rest.model.CruiseModel;
import com.cruisebooking.rest.model.UserModel;
import com.cruisebooking.rest.repository.BookRepository;
import com.cruisebooking.rest.repository.CruiseRepository;
import com.cruisebooking.rest.repository.UserRepository;
import com.cruisebooking.rest.service.impl.CruiseImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

class CruiseImplTest {

    @Mock
    private CruiseRepository cruiseRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CruiseImpl cruiseServiceInterface;
    CruiseModel cruiseModel;
    UserModel userModel;
    BookingModel bookingModel;

    AutoCloseable autoCloseable;  //Close all unwanted resources when test cases finish their execution

    @BeforeEach
    void setUp() {
        autoCloseable= MockitoAnnotations.openMocks(this);
        cruiseModel = new CruiseModel("101","Cruise1","Ship1","Udupi","Banglore",3000,70,60,new Date(2027-6-6));
        bookingModel = new BookingModel("67","8277719279","Linux",5,20000.0);
        userModel = new UserModel("Pooja","PoojaR","Female","p123@gmail.com","p123","8277719279");
        System.out.println(cruiseModel + " - " +bookingModel+ " - " +userModel);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreateCruiseInfo() {
        mock(CruiseModel.class);

        when(cruiseRepository.save(cruiseModel)).thenReturn(cruiseModel);
        assertThat(cruiseServiceInterface.createCruiseInfo(cruiseModel)).isEqualTo(cruiseModel);
        System.out.println(cruiseServiceInterface.createCruiseInfo(cruiseModel)+ " Equals "+cruiseModel);
    }


    @Test
    void testGetCruiseList() {
        when(cruiseRepository.findAll()).thenReturn(Collections.singletonList(cruiseModel));
        List<CruiseModel> cruiseList = cruiseServiceInterface.getCruiseList();
        assertThat(cruiseList).containsExactly(cruiseModel);
        System.out.println(cruiseList + " Equals " + Collections.singletonList(cruiseModel));
    }


    @Test
    void testFindCruiseById() {
        when(cruiseRepository.findById("101")).thenReturn(Optional.ofNullable(cruiseModel)); //Optional.ofNullable for may or may not return case
        assertThat(cruiseServiceInterface.findCruiseById("101").getCruiseId()).isEqualTo(cruiseModel.getCruiseId());
    }

    @Test
    void updateCruieData() {
        mock(CruiseModel.class);
        mock(CruiseRepository.class);
        // Call the method to be tested
        cruiseServiceInterface.updateCruieData(cruiseModel);
        // Verify that the save method was called with the correct argument
        // Verify that the save method was called with the correct argument
        verify(cruiseRepository).save(cruiseModel);
        // Print a statement for visual verification
        System.out.println("Cruise data updated successfully: " + cruiseModel);
    }

    @Test
    void testCreateUserInfo() {
        mock(UserRepository.class);
        when(userRepository.save(userModel)).thenReturn(userModel);
        assertThat(cruiseServiceInterface.createUserInfo(userModel)).isEqualTo(userModel);
        System.out.println(cruiseServiceInterface.createUserInfo(userModel)+ " Equals "+userModel);
    }

    @Test
    void testFindUserFromDb() {
        when(userRepository.findById("8277719279")).thenReturn(Optional.ofNullable(userModel)); //Optional.ofNullable for may or may not return case
        assertThat(cruiseServiceInterface.findUserFromDb("8277719279").getName()).isEqualTo(userModel.getName());
        System.out.println(cruiseServiceInterface.findUserFromDb("8277719279").getName()+" equals "+userModel.getName());
    }

    @Test
    void testSearchCruises() {
        when(cruiseRepository.findByDateAndSourceAndDestinationAndPriceBetween(new Date(2027-6-6),"Udupi","Banglore",100,6000)).thenReturn(Collections.singletonList(cruiseModel));
        //If end price is given as 2000, then it gives error.
        assertThat(cruiseServiceInterface.searchCruises(new Date(2027-6-6),"Udupi","Banglore",100,6000)).isEqualTo(Collections.singletonList(cruiseModel));
        System.out.println(cruiseModel.getPrice());

    }

    @Test
    void testCreateBookInfo() {
        mock(BookingModel.class);
        mock(BookRepository.class);
        when(bookRepository.save(bookingModel)).thenReturn(bookingModel);
        assertThat(cruiseServiceInterface.createBookInfo(bookingModel)).isEqualTo(bookingModel);
        System.out.println(cruiseServiceInterface.createBookInfo(bookingModel)+" equals "+bookingModel);
    }

    @Test
    void testGetBookingInfo() {
        mock(BookingModel.class);
        mock(BookRepository.class);
        when(bookRepository.findAllByBookingUser("8277719279")).thenReturn(Collections.singletonList(bookingModel));
        assertThat(cruiseServiceInterface.getBookingInfo("8277719279")).isEqualTo(Collections.singletonList(bookingModel));
        System.out.println("Fetched successfully");
    }

    @Test
    void testGetAllBookings() {
        mock(BookingModel.class);
        mock(BookRepository.class);
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(bookingModel));
        assertThat(cruiseServiceInterface.getAllBookings()).isEqualTo(Collections.singletonList(bookingModel));
    }

    @Test
    void testFindBookingById() {
        mock(BookingModel.class);
        mock(BookRepository.class);
        when(bookRepository.findById("67")).thenReturn(Optional.ofNullable(bookingModel)); //Optional.ofNullable for may or may not return case
        assertThat(cruiseServiceInterface.findBookingById("67").getBookingUser()).isEqualTo(bookingModel.getBookingUser());
        System.out.println(cruiseServiceInterface.findBookingById("67").getBookingUser()+ " Equals "+bookingModel.getBookingUser());
    }


    //Special way of writing test case for delete
    @Test
    void testCancelBookingById() {
        mock(BookingModel.class);
        mock(BookRepository.class);
        doAnswer(Answers.CALLS_REAL_METHODS).when(bookRepository).deleteById(any());
        assertThat(cruiseServiceInterface.cancelBookingById("67")).isEqualTo(true);

    }
}