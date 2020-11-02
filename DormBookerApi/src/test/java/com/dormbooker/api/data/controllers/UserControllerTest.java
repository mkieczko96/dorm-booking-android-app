package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.exceptions.ResourceNotExistsException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    }
}