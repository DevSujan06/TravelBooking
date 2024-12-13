package com.travelbooking.controller;

import com.travelbooking.model.Flight;
import com.travelbooking.service.FlightService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final FlightService flightService;

    public AdminController(FlightService flightService) {
        this.flightService = flightService;
    }

    // Endpoint to add a new flight
    @PostMapping("/add-flight")
    public Mono<Flight> addFlight(@RequestBody Flight flight) {
        return flightService.addFlight(flight);
    }

    // Endpoint to delete a flight by ID
    @DeleteMapping("/delete-flight/{id}")
    public Mono<Void> deleteFlight(@PathVariable String id) {
        return flightService.deleteFlightById(id);
    }

    // Endpoint to update a flight by ID
    @PutMapping("/update-flight/{id}")
    public Mono<Flight> updateFlight(@PathVariable String id, @RequestBody Flight flight) {
        return flightService.updateFlight(id, flight);
    }
}
