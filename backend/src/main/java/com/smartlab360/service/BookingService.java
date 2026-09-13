package com.smartlab360.backend.service;

import com.smartlab360.backend.entity.Booking;
import com.smartlab360.backend.entity.Equipment;
import com.smartlab360.backend.entity.User;
import com.smartlab360.backend.repository.BookingRepository;
import com.smartlab360.backend.repository.EquipmentRepository;
import com.smartlab360.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EquipmentRepository equipmentRepository;
    private final NotificationService notificationService;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EquipmentRepository equipmentRepository,
            NotificationService notificationService) {

        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.equipmentRepository = equipmentRepository;
        this.notificationService = notificationService;
    }

    public Booking createBooking(
            Long userId,
            Long equipmentId,
            Booking booking) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Equipment not found"));

        if (!"AVAILABLE".equalsIgnoreCase(
                equipment.getStatus())) {

            throw new RuntimeException(
                    "Equipment is not available for booking");
        }

        if (booking.getStartTime() != null &&
                booking.getEndTime() != null &&
                !booking.getEndTime()
                        .isAfter(booking.getStartTime())) {

            throw new RuntimeException(
                    "End time must be after start time");
        }

        booking.setUser(user);
        booking.setEquipment(equipment);

        if (booking.getStatus() == null ||
                booking.getStatus().isBlank()) {

            booking.setStatus("PENDING");
        }

        return bookingRepository.save(booking);
    }


    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }


    public List<Booking> getBookingsByUser(
            Long userId) {

        return bookingRepository.findByUserId(
                userId);
    }


    public Booking getBookingById(
            Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found"));
    }


    public Booking updateBookingStatus(
            Long id,
            String status) {

        Booking booking =
                getBookingById(id);

        String newStatus =
                status.toUpperCase();

        booking.setStatus(newStatus);

        Booking updatedBooking =
                bookingRepository.save(booking);


        /*
         * ==============================
         * CREATE NOTIFICATION
         * ==============================
         */

        String equipmentName =
                booking.getEquipment() != null
                        ? booking.getEquipment().getName()
                        : "Equipment";


        if ("APPROVED".equals(newStatus)) {

            notificationService.createNotification(
                    booking.getUser().getId(),

                    "Booking #" +
                    booking.getId() +
                    " for " +
                    equipmentName +
                    " has been approved."
            );

        }


        else if ("REJECTED".equals(newStatus)) {

            notificationService.createNotification(
                    booking.getUser().getId(),

                    "Booking #" +
                    booking.getId() +
                    " for " +
                    equipmentName +
                    " has been rejected."
            );

        }


        return updatedBooking;
    }


    public void deleteBooking(
            Long id) {

        if (!bookingRepository.existsById(id)) {

            throw new RuntimeException(
                    "Booking not found");
        }

        bookingRepository.deleteById(id);
    }
}