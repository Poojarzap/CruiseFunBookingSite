package com.cruisebooking.rest.repository;

import com.cruisebooking.rest.model.CruiseModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

public class CruiseRepositoryUnitTest {

    @Mock
    private CruiseRepository cruiseRepository;

    List<CruiseModel> cruiseModelList;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        cruiseModelList = new ArrayList<>();
        cruiseModelList.add(new CruiseModel("101","Cruise1","Ship1","Source1","Destination1",25000.0,60,60,new Date(2000-3-3)));
        cruiseModelList.add(new CruiseModel("102","Cruise2","Ship2","Source2","Destination2",15000.0,60,60,new Date(2000-3-3)));
        when(cruiseRepository.findByDateAndSourceAndDestinationAndPriceBetween(new Date(2000-3-3),"Source1","Destination1",200,22300)).thenReturn(List.of(cruiseModelList.get(0)));
        when(cruiseRepository.findByDateAndSourceAndDestinationAndPriceBetween(new Date(2000-3-3),"Source2","Destination2",13000,22300)).thenReturn(List.of(cruiseModelList.get(1)));
//        when(cruiseRepository.findAllByBookingUser("00000000")).thenReturn(new ArrayList<>());
    }

    @Test
    @Order(1)
    public void testFindByDateAndSourceAndDestinationAndPriceBetween_Found() {
        List<CruiseModel> result = cruiseRepository.findByDateAndSourceAndDestinationAndPriceBetween(new Date(2000-3-3),"Source2","Destination2",13000,22300);
        System.out.println("Result found "+result.get(0).getCruiseId());
        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get(0).getCruiseId()).isEqualTo("102");
    }

    @Test
    @Order(2)
    public void testFindByDateAndSourceAndDestinationAndPriceBetween_NotFound() {
        List<CruiseModel> result = cruiseRepository.findByDateAndSourceAndDestinationAndPriceBetween(new Date(2000-3-3),"Source2","Destination2",15000,35);
        Assertions.assertThat(result).isEmpty();
        System.out.println("Result not found "+result);
    }
}

