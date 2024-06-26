package com.cruisebooking.rest.repository;

import com.cruisebooking.rest.model.BookingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class BookRepositoryUnitTest {

    @Mock
    private BookRepository bookRepository;

    private List<BookingModel> bookingModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        bookingModel = new ArrayList<>();
        bookingModel.add(new BookingModel("333", "8277719279", "linux", 20, 50000.0));
        bookingModel.add(new BookingModel("334", "8277719280", "windows", 15, 45000.0));
        bookingModel.add(new BookingModel("335", "8277719279", "mac", 10, 60000.0));
        when(bookRepository.findAllByBookingUser("8277719280")).thenReturn(List.of(bookingModel.get(1)));
        when(bookRepository.findAllByBookingUser("00000000")).thenReturn(new ArrayList<>());
    }

    @Test
    public void testFindAllByBookingUser_Found() {
        List<BookingModel> result = bookRepository.findAllByBookingUser("8277719280");
        System.out.println("Result found "+result);
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getBookingId()).isEqualTo("334");
    }

    @Test
    public void testFindAllByBookingUser_NotFound() {
        List<BookingModel> result = bookRepository.findAllByBookingUser("00000000");
        assertThat((result).isEmpty()).isTrue();
        System.out.println("Result not found "+result);
    }
}
