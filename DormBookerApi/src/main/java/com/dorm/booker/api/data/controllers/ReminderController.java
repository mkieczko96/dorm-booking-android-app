package com.dorm.booker.api.data.controllers;

import com.dorm.booker.api.data.models.Reminder;
import com.dorm.booker.api.data.repositories.ReminderRepository;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@AllArgsConstructor
public class ReminderController {
    private final ReminderRepository repository;

    // GET
    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getById(@PathVariable long id) {
        Reminder reminder = repository.findById(id).orElseThrow();
        return ResponseEntity.ok().body(reminder);
    }

    // GET ALL BY BOOKING
    @GetMapping
    public ResponseEntity<List<Reminder>> getAllByBooking(@Spec(
            path = "bookingId",
            params = "booking-id",
            spec = Equal.class
    ) Specification<Reminder> filter) {
        List<Reminder> reminders = repository.findAll(filter);
        return ResponseEntity.ok().body(reminders);
    }

    // POST
    @PostMapping
    public ResponseEntity<Reminder> createReminder(@RequestBody Reminder reminder) {
        Reminder saved = repository.save(reminder);
        return ResponseEntity.created(URI.create("/api/reminders/" + saved.getId())).body(saved);
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Reminder> updateReminder(@PathVariable long id, @RequestBody Reminder updated) {
        Reminder reminder = repository.getOne(id);
        reminder.setBooking(updated.getBooking());
        reminder.setLabel(updated.getLabel());
        reminder.setMessage(updated.getMessage());
        reminder.setTriggerTime(updated.getTriggerTime());
        Reminder saved = repository.save(reminder);
        return ResponseEntity.ok().body(saved);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Reminder> removeReminder(@PathVariable long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
