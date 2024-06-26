package com.cruisebooking.rest.repository;

import com.cruisebooking.rest.model.BookingModel;
import com.cruisebooking.rest.model.CruiseModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CruiseRepository extends MongoRepository<CruiseModel, String> {
//   @Query("{ 'price' : { $gte: ?0, $lte: ?1 } }")
//    List<CruiseModel> findByPriceBetween(double startPrice, double endPrice);

// List<CruiseModel> findBySourceAndDestination(String source, String destination);

    @Query("{ 'date': ?0,'source': ?1, 'destination': ?2,'price' : { $gte: ?3, $lte: ?4} }")
    List<CruiseModel> findByDateAndSourceAndDestinationAndPriceBetween(Date date, String source, String destination, double startPrice, double endPrice);

}
