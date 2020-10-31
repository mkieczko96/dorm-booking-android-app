package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.models.User;
import com.dormbooker.api.data.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@RestController @RequestMapping("/api/v1")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("users/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id")long id) throws ResourceAccessException {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceAccessException("User not found."));
        return ResponseEntity.ok().body(user);
    }

}
