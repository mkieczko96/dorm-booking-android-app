package com.dorm.booker.api.data.controllers;

import com.dorm.booker.api.data.exceptions.BookingNotUpdatableException;
import com.dorm.booker.api.data.exceptions.ResourceNotExistsException;
import com.dorm.booker.api.data.models.Booking;
import com.dorm.booker.api.data.repositories.BookingRepository;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;

    //TODO: Add sorting by beginAt date.
    @GetMapping
    public List<Booking> findAllBookings(@And({
            @Spec(path = "userId", params = "user-id", spec = Equal.class),
            @Spec(path = "facilityId", params = "facility-id", spec = Equal.class),
            @Spec(path = "beginAt", params = "begin-after", spec = GreaterThanOrEqual.class),
            @Spec(path = "beginAt", params = "begin-before", spec = LessThanOrEqual.class)
    }) Specification<Booking> bookingSpecification) {
        return bookingRepository.findAll(bookingSpecification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> findBookingById(@PathVariable("id") long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        return ResponseEntity.ok().body(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeBookingById(@PathVariable("id") long id) {
        bookingRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.GONE).body("Booking #" + id + " was deleted.");
    }

    @PostMapping
    public Booking saveNewBooking(@RequestBody Booking newBooking) {
        return bookingRepository.save(newBooking);
    }

    @PutMapping("/{id}")
    public Booking saveBooking(@PathVariable("id") long id, @RequestBody Booking updatedBooking) throws ResourceNotExistsException, BookingNotUpdatableException {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new ResourceNotExistsException("Booking with id: " + id + " does not exist."));
        if (booking.getBeginAt() > (System.currentTimeMillis() / 1000L)) {
            booking.setUserId(updatedBooking.getUserId());
            booking.setBeginAt(updatedBooking.getBeginAt());
            booking.setDurationInMinutes(updatedBooking.getDurationInMinutes());
        } else if ((booking.getBeginAt() + (booking.getDurationInMinutes() * 60)) > (System.currentTimeMillis() / 1000L)) {
            booking.setDurationInMinutes(updatedBooking.getDurationInMinutes());
        } else {
            throw new BookingNotUpdatableException("Booking: " + id + " could not be updated because it ended.");
        }
        return bookingRepository.save(booking);
    }
}
