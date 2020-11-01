package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.models.Booking;
import com.dormbooker.api.data.models.User;
import com.dormbooker.api.data.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@RestController @RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id")long id) throws ResourceAccessException {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceAccessException("User not found."));
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/{id}/bookings")
    public List<Booking> findAllBookingsByUserId(@PathVariable("id")long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceAccessException("User not found.")).getBookings();
    }

    @PostMapping
    public User saveNewUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }

    @PutMapping("/{id}")
    public User saveUser(@PathVariable("id")long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setFirstName(updatedUser.getFirstName());
                    u.setLastName(updatedUser.getLastName());
                    u.setRoom(updatedUser.getRoom());
                    u.setEmailAddress(updatedUser.getEmailAddress());
                    u.setLastModifiedOn(System.currentTimeMillis() / 1000L);
                    u.setExpiresOn(updatedUser.getExpiresOn());
                    return userRepository.save(u);
                })
                .orElseGet(() -> {
                    updatedUser.setId(id);
                    return userRepository.save(updatedUser);
                });
    }
}
