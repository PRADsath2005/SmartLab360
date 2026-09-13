package com.smartlab360.backend.repository;

import com.smartlab360.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByBookingDate(LocalDate bookingDate);

    List<Booking> findByStatus(String status);
}