package com.example.demo.Services;

import com.example.demo.model.DiningTable;
import com.example.demo.model.Reservation;
import com.example.demo.repository.DiningTableRepository;
import com.example.demo.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DiningTableRepository diningTableRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(Reservation reservation) {
        if (reservation.getReservationTime() == null) {
            throw new IllegalArgumentException("ReservationTime is required.");
        }
        if (reservation.getGuestCount() == null || reservation.getGuestCount() <= 0) {
            throw new IllegalArgumentException("GuestCount must be greater than zero.");
        }
        if (reservation.getTableId() == null) {
            throw new IllegalArgumentException("TableId is required.");
        }

        DiningTable table = diningTableRepository.findById(reservation.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Table not found: " + reservation.getTableId()));

        if (table.getStatus() != null && !table.getStatus().equalsIgnoreCase("Trống")) {
            throw new IllegalArgumentException("Bàn hiện không khả dụng.");
        }

        LocalDateTime startWindow = reservation.getReservationTime().minusHours(2);
        LocalDateTime endWindow = reservation.getReservationTime().plusHours(2);
        List<Integer> reservedTables = reservationRepository.findReservedTableIds(startWindow, endWindow);
        if (reservedTables.contains(reservation.getTableId())) {
            throw new IllegalArgumentException("Bàn đã được đặt vào khoảng thời gian này.");
        }

        reservation.setStatus("Chờ xác nhận");
        return reservationRepository.save(reservation);
    }

    public List<DiningTable> findAvailableTables(LocalDateTime reservationTime, Integer guestCount) {
        List<Integer> reservedTableIds = reservationRepository.findReservedTableIds(
                reservationTime.minusHours(2), reservationTime.plusHours(2));

        return diningTableRepository.findAll().stream()
                .filter(table -> table.getStatus() != null && table.getStatus().equalsIgnoreCase("Trống"))
                .filter(table -> table.getCapacity() != null && table.getCapacity() >= (guestCount == null ? 1 : guestCount))
                .filter(table -> !reservedTableIds.contains(table.getTableId()))
                .collect(Collectors.toList());
    }
}
