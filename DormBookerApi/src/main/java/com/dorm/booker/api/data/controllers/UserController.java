package com.dorm.booker.api.data.controllers;

import com.dorm.booker.api.data.exceptions.ResourceNotExistsException;
import com.dorm.booker.api.data.models.User;
import com.dorm.booker.api.data.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") long id) throws ResourceNotExistsException {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotExistsException("User: " + id + " not found."));
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/me")
    public ResponseEntity<User> findCurrentUser(@AuthenticationPrincipal UserDetails user) {
        User u = (User) user;
        return ResponseEntity.ok(u);
    }

    @PostMapping
    public User saveNewUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }

    @PutMapping("/{id}")
    public User saveUser(@PathVariable("id") long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(u -> {
                    u.setPassword(updatedUser.getPassword());
                    u.setFirstName(updatedUser.getFirstName());
                    u.setLastName(updatedUser.getLastName());
                    u.setRoom(updatedUser.getRoom());
                    u.setUsername(updatedUser.getUsername());
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
