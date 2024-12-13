package com.travelbooking.service;

import com.travelbooking.model.Booking;
import com.travelbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Retrieve all bookings for a user
    public Flux<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Create a new booking
    public Mono<Booking> createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    // Update an existing booking
    public Mono<Booking> updateBooking(String bookingId, Booking updatedBooking) {
        return bookingRepository.findById(bookingId)
                .flatMap(existingBooking -> {
                    existingBooking.setFlightId(updatedBooking.getFlightId());
                    existingBooking.setSeatsBooked(updatedBooking.getSeatsBooked());
                    existingBooking.setTotalPrice(updatedBooking.getTotalPrice());
                    return bookingRepository.save(existingBooking);
                });
    }

    // Delete a booking
    public Mono<Void> deleteBooking(String bookingId) {
        return bookingRepository.deleteById(bookingId);
    }
}
