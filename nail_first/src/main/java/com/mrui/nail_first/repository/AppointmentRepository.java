package com.mrui.nail_first.repository;

import com.mrui.nail_first.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime start, LocalDateTime end);
    boolean existsByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
