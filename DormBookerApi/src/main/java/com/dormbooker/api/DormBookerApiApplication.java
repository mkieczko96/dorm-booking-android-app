package com.dormbooker.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication
public class DormBookerApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(DormBookerApiApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }

}
