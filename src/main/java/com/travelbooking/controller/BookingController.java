package com.travelbooking.controller;

import com.travelbooking.model.Booking;
import com.travelbooking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Retrieve all bookings for a specific user by userId.
     */
    @GetMapping("/user/{userId}")
    public Flux<Booking> getBookingsByUser(@PathVariable String userId) {
        logger.info("Fetching all bookings for user ID: {}", userId);
        return bookingService.getBookingsByUser(userId)
                .doOnComplete(() -> logger.info("All bookings fetched for user ID: {}", userId))
                .doOnError(error -> logger.error("Error fetching bookings for user ID: {}", userId, error));
    }

    /**
     * Create a new booking.
     */
    @PostMapping
    public String createBooking(@RequestParam String flightId, Model model) {
        // Retrieve flight and user information to create a booking
        Booking booking = new Booking();
        booking.setFlightId(flightId);
        // Assume you have a logged-in user ID; replace with actual logic
        booking.setUserId("loggedInUserId");
        bookingService.createBooking(booking).subscribe();

        model.addAttribute("message", "Booking successful!");
        return "redirect:/bookings"; // Redirect to the bookings page
    }

    /**
     * Update an existing booking by bookingId.
     */
    @PutMapping("/{bookingId}")
    public Mono<Booking> updateBooking(@PathVariable String bookingId, @RequestBody Booking updatedBooking) {
        logger.info("Update request received for booking ID: {}", bookingId);
        return bookingService.updateBooking(bookingId, updatedBooking)
                .doOnSuccess(updated -> logger.info("Booking updated successfully: {}", updated))
                .switchIfEmpty(Mono.error(new RuntimeException("Booking not found with ID: " + bookingId)))
                .onErrorResume(error -> {
                    logger.error("Error updating booking with ID: {}", bookingId, error);
                    return Mono.error(new RuntimeException("Failed to update booking: " + error.getMessage()));
                });
    }

    /**
     * Delete a booking by bookingId.
     */
    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBooking(@PathVariable String bookingId) {
        logger.info("Delete request received for booking ID: {}", bookingId);
        return bookingService.deleteBooking(bookingId)
                .doOnSuccess(unused -> logger.info("Booking deleted successfully with ID: {}", bookingId))
                .onErrorResume(error -> {
                    logger.error("Error deleting booking with ID: {}", bookingId, error);
                    return Mono.error(new RuntimeException("Failed to delete booking: " + error.getMessage()));
                });
    }
}
