package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.exceptions.ResourceNotExistsException;
import com.dormbooker.api.data.models.User;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    void findAllUsers_sizeOfUsersCollectionIsCloseToInitialDataSeed_test() {
        Assertions.assertThat(userController.findAllUsers().size())
                .isCloseTo(411, Offset.offset(5));
    }

    @Test
    void findUserById_returnsOneUserWhenIdExists_test() throws ResourceNotExistsException {
        Assertions.assertThat(userController.findUserById(1)).isNotNull();
    }

    @Test
    void findUserById_throwsExceptionWhenIdDoesNotExist_test() {
        ThrowableAssert.ThrowingCallable throwingCallable = () -> userController.findUserById(1000);
        Assertions.assertThatThrownBy(throwingCallable, "User not found.");
    }

    @Test
    void findAllBookingsByUserId() {
    }

    @Test
    void saveNewUser() {
    }

    @Test
    void saveUser() {
        BCryptPasswordEncoder hash = new BCryptPasswordEncoder(16);

        User u = null;
        try {
            u = userController.findUserById(412L).getBody();
        } catch (ResourceNotExistsException e) {
            e.printStackTrace();
        }

//        for (User u: users) {
            u.setPassword(hash.encode("qwe@123"));
            userController.saveUser(u.getId(), u);
//        }

//        Assertions.assertThat(users.size()).isEqualTo(412);
    }
}