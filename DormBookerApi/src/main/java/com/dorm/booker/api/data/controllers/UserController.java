package com.dorm.booker.api.data.controllers;

import com.dorm.booker.api.data.exceptions.InvalidPasswordException;
import com.dorm.booker.api.data.exceptions.ResourceNotExistsException;
import com.dorm.booker.api.data.exceptions.UnexpectedException;
import com.dorm.booker.api.data.models.User;
import com.dorm.booker.api.data.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
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

    @PatchMapping("/updatePassword")
    public ResponseEntity<User> changePassword(@AuthenticationPrincipal UserDetails requestUser,
                                               @RequestParam("old-passwd") String oldPassword,
                                               @RequestParam("new-passwd") String newPassword) throws InvalidPasswordException, UnexpectedException {
        User user = (User) requestUser;
        String currentPassword = new String(Base64.getDecoder().decode(oldPassword));
        String updatedPassword = new String(Base64.getDecoder().decode(newPassword));

        if (!user.getPassword().equals(currentPassword)) {
            throw new InvalidPasswordException();
        }

        User updatedUser = userRepository.findById(user.getId())
                .map(u -> {
                    u.setPassword(updatedPassword);
                    u.setCredentialsNonExpired(true);
                    u.setCredentialsUpdatedOn(System.currentTimeMillis() / 1000);
                    u.setLastModifiedOn(System.currentTimeMillis() / 1000);
                    return userRepository.save(u);
                })
                .orElseThrow(UnexpectedException::new);

        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}")
    public User saveUser(@PathVariable("id") long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .map(u -> {
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

    @DeleteMapping("/{id}")
    public ResponseEntity removeUser(@PathVariable("id") long id) {
        userRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
