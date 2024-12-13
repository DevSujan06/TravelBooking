package com.travelbooking.repository;

import com.travelbooking.model.Flight;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FlightRepository extends ReactiveCrudRepository<Flight, String> {

    /**
     * Finds flights by their origin and destination.
     * 
     * @param origin      The origin city of the flight.
     * @param destination The destination city of the flight.
     * @return A Flux containing all flights matching the origin and destination.
     */
    Flux<Flight> findByOriginAndDestination(String origin, String destination);

    /**
     * Retrieves all flights.
     * 
     * This is implicitly supported by ReactiveCrudRepository, but explicitly
     * mentioned here for clarity.
     * 
     * @return A Flux containing all flights in the repository.
     */
    Flux<Flight> findAll();
}
