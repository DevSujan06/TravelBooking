package com.travelbooking.service;

import com.travelbooking.model.Flight;
import com.travelbooking.repository.FlightRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class FlightService {
	private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
	 private final FlightRepository flightRepository;

	    public FlightService(FlightRepository flightRepository) {
	        this.flightRepository = flightRepository;
	    }

	    public Flux<Flight> searchFlights(String origin, String destination) {
	        return flightRepository.findByOriginAndDestination(origin, destination);
	    }
    // Add a new flight
    public Mono<Flight> addFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    // Delete a flight by ID
    public Mono<Void> deleteFlightById(String id) {
        return flightRepository.deleteById(id);
    }

    // Update a flight by ID
    public Mono<Flight> updateFlight(String id, Flight updatedFlight) {
        return flightRepository.findById(id)
                .flatMap(existingFlight -> {
                    existingFlight.setOrigin(updatedFlight.getOrigin());
                    existingFlight.setDestination(updatedFlight.getDestination());
                    existingFlight.setPrice(updatedFlight.getPrice());
                    existingFlight.setAvailableSeats(updatedFlight.getAvailableSeats());
                    return flightRepository.save(existingFlight);
                });
    }
    public Flux<Flight> getAllFlights() {
        logger.info("Fetching all flights");
        return flightRepository.findAll();
    }
    
}
