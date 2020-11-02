package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.repositories.FacilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facilities")
@AllArgsConstructor
public class FacilityController {

    private final FacilityRepository facilityRepository;


}
