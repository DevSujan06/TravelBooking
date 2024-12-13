package com.travelbooking.controller;

import com.travelbooking.model.Flight;
import com.travelbooking.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/flights") // Thymeleaf routes for rendering views
public class FlightController {

    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    // 1. Thymeleaf Routes

    /**
     * Show the flight search page (Thymeleaf view).
     */
    @GetMapping
    public String showFlightSearchPage() {
        logger.info("Rendering flight search page.");
        return "flights"; // Points to templates/flights.html
    }

    /**
     * Search for flights and render results in Thymeleaf view.
     */
    @GetMapping("/search")
    public String searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            Model model) {
        logger.info("Searching flights for origin: {}, destination: {}", origin, destination);

        flightService.searchFlights(origin, destination)
                .collectList()
                .doOnSuccess(flightList -> {
                    model.addAttribute("flights", flightList);
                    model.addAttribute("origin", origin);
                    model.addAttribute("destination", destination);
                })
                .doOnError(error -> logger.error("Error retrieving flights: {}", error.getMessage()))
                .subscribe();

        return "flightResults"; // Render the results in Thymeleaf
    }

    /**
     * Show the "Add Flight" page (Thymeleaf view).
     */
    @GetMapping("/add")
    public String showAddFlightPage(Model model) {
        logger.info("Rendering Add Flight page.");
        model.addAttribute("flight", new Flight());
        return "flightsAdd"; // Points to templates/flightsAdd.html
    }

    /**
     * Add a new flight (Thymeleaf submission).
     */
    @PostMapping("/add")
    public String addFlight(@ModelAttribute Flight flight, Model model) {
        logger.info("Adding flight: {}", flight);

        flightService.addFlight(flight)
                .doOnSuccess(addedFlight -> logger.info("Flight added successfully: {}", addedFlight))
                .doOnError(error -> logger.error("Error adding flight", error))
                .subscribe();

        model.addAttribute("successMessage", "Flight added successfully!");
        return "redirect:/flights"; // Redirect to the list of flights
    }

    // 2. API Routes

    @RestController
    @RequestMapping("/api/flights") // API Routes for JSON requests
    public static class FlightApiController {

        private static final Logger apiLogger = LoggerFactory.getLogger(FlightApiController.class);
        private final FlightService flightService;

        public FlightApiController(FlightService flightService) {
            this.flightService = flightService;
        }

        /**
         * Add a new flight via API (JSON request).
         */
        @PostMapping(value = "/add", consumes = "application/json")
        @ResponseStatus(HttpStatus.CREATED)
        public Mono<Flight> addFlightApi(@RequestBody Flight flight) {
            apiLogger.info("API request to add flight: {}", flight);

            return flightService.addFlight(flight)
                    .doOnSuccess(addedFlight -> apiLogger.info("Flight added successfully via API: {}", addedFlight))
                    .doOnError(error -> apiLogger.error("Error adding flight via API", error));
        }

        /**
         * Get all flights via API (JSON response).
         */
        @GetMapping
        public Flux<Flight> getAllFlightsApi() {
            apiLogger.info("API request to get all flights");
            return flightService.getAllFlights()
                    .doOnComplete(() -> apiLogger.info("All flights fetched successfully via API"))
                    .doOnError(error -> apiLogger.error("Error fetching flights via API", error));
        }

        /**
         * Search flights by origin and destination via API (JSON response).
         */
        @GetMapping("/search")
        public Flux<Flight> searchFlightsApi(@RequestParam String origin, @RequestParam String destination) {
            apiLogger.info("API request to search flights from {} to {}", origin, destination);

            return flightService.searchFlights(origin, destination)
                    .doOnComplete(() -> apiLogger.info("Flights search completed successfully via API"))
                    .doOnError(error -> apiLogger.error("Error searching flights via API", error));
        }

        /**
         * Update a flight via API.
         */
        @PutMapping("/update/{id}")
        public Mono<Flight> updateFlightApi(@PathVariable String id, @RequestBody Flight flight) {
            apiLogger.info("API request to update flight with ID {}: {}", id, flight);

            return flightService.updateFlight(id, flight)
                    .doOnSuccess(updatedFlight -> apiLogger.info("Flight updated successfully via API: {}", updatedFlight))
                    .doOnError(error -> apiLogger.error("Error updating flight via API", error));
        }

        /**
         * Delete a flight via API.
         */
        @DeleteMapping("/delete/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public Mono<Void> deleteFlightApi(@PathVariable String id) {
            apiLogger.info("API request to delete flight with ID {}", id);

            return flightService.deleteFlightById(id)
                    .doOnSuccess(unused -> apiLogger.info("Flight deleted successfully via API"))
                    .doOnError(error -> apiLogger.error("Error deleting flight via API", error));
        }
    }
}
