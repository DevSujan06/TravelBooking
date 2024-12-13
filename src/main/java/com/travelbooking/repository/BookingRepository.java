package com.travelbooking.repository;

import com.travelbooking.model.Booking;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {
    Flux<Booking> findByUserId(String userId);
}
