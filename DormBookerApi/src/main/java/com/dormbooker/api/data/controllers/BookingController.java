package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.models.Booking;
import com.dormbooker.api.data.repositories.BookingRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequestMapping("/api/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;

    @GetMapping
    public List<Booking> findAllBookings() { return bookingRepository.findAll(); }
}
