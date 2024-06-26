package com.cruisebooking.rest.repository;

import com.cruisebooking.rest.model.BookingModel;
import org.springframework.data.mongodb.core.MappedDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<BookingModel,String> {

    List<BookingModel> findAllByBookingUser(String bookingUser);
}
