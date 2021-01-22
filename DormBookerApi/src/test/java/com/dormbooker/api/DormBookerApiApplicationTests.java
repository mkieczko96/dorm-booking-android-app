package com.dormbooker.api;

import com.dorm.booker.api.data.controllers.BookingController;
import com.dorm.booker.api.data.controllers.UserController;
import com.dorm.booker.api.data.controllers.FacilityController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DormBookerApiApplicationTests {

    @Autowired
    private UserController userController;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private FacilityController facilityController;

    @Test
    void contextLoads() throws Exception{
        Assertions.assertNotNull(userController);
        Assertions.assertNotNull(bookingController);
        Assertions.assertNotNull(facilityController);
    }

}
