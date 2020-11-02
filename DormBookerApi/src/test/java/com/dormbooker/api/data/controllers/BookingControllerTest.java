package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.exceptions.BookingNotUpdatableException;
import com.dormbooker.api.data.exceptions.ResourceNotExistsException;
import com.dormbooker.api.data.models.Booking;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingControllerTest {

    @Autowired
    private BookingController controller;

    @Test
    @Order(1)
    void saveNewBooking_addsNewBookingToCollection_test() {
        Booking booking = new Booking();
        booking.setUserId(1L);
        booking.setFacilityId(1L);
        booking.setBeginAt(1604318400L);
        booking.setDurationInMinutes(30L);

        Booking saved = controller.saveNewBooking(booking);

        assertNotNull(saved);
        assertEquals(800L, saved.getId());
        assertEquals(booking.getBeginAt(), saved.getBeginAt());
        assertEquals(booking.getDurationInMinutes(), saved.getDurationInMinutes());
    }

    @Test
    @Order(2)
    void saveBooking_updatesBeginAtDurationAndUserIdWhenBookingNotStartedYet_test() throws ResourceNotExistsException, BookingNotUpdatableException {
        Booking booking = controller.findBookingById(798).getBody();

        Long beginAt = (System.currentTimeMillis() / 1000L);

        assertNotNull(booking);

        booking.setDurationInMinutes(240L);
        booking.setBeginAt(beginAt);
        booking.setUserId(111L);

        Booking saved = controller.saveBooking(798, booking);

        assertEquals(saved.getDurationInMinutes(), booking.getDurationInMinutes());
        assertEquals(saved.getBeginAt(), booking.getBeginAt());
    }

    @Test
    @Order(3)
    void saveBooking_updatesDurationWhenBookingAlreadyStarted_test() throws ResourceNotExistsException, BookingNotUpdatableException {
        Booking booking = controller.findBookingById(798).getBody();

        assertNotNull(booking);

        booking.setDurationInMinutes(480L);

        Booking saved = controller.saveBooking(798, booking);

        assertEquals(booking.getDurationInMinutes(), saved.getDurationInMinutes());

        Long userId = booking.getUserId();
        booking.setUserId(222L);

        saved = controller.saveBooking(798, booking);

        assertEquals(userId, saved.getUserId());

    }

    @Test
    @Order(4)
    void saveBooking_throwsExceptionWhenBookingAlreadyFinished_test() throws ResourceNotExistsException, BookingNotUpdatableException {
        Booking booking = controller.findBookingById(800).getBody();

        assertNotNull(booking);

        booking.setDurationInMinutes(30L);

        assertThrows(BookingNotUpdatableException.class, () -> controller.saveBooking(800, booking));
    }

}