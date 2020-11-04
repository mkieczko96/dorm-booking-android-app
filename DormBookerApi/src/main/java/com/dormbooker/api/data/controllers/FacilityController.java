package com.dormbooker.api.data.controllers;

import com.dormbooker.api.data.exceptions.ResourceNotExistsException;
import com.dormbooker.api.data.models.Facility;
import com.dormbooker.api.data.repositories.FacilityRepository;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@AllArgsConstructor
public class FacilityController {

    private final FacilityRepository facilityRepository;

    @GetMapping
    public List<Facility> findAllFacilities(@And({
            @Spec(path = "name", params = "name", spec = Like.class)
    })Specification<Facility> facilitySpecification) {
        return facilityRepository.findAll(facilitySpecification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Facility> findFacilityById(@PathVariable("id") long id) throws ResourceNotExistsException{
        Facility facility = facilityRepository.findById(id).orElseThrow(() -> new ResourceNotExistsException("Facility: " + id + " not found."));
        return ResponseEntity.ok().body(facility);
    }
}
