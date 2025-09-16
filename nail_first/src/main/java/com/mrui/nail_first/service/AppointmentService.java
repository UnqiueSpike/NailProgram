package com.mrui.nail_first.service;

import com.mrui.nail_first.dto.AppointmentDTO;
import com.mrui.nail_first.dto.CreateAppointmentRequest;
import com.mrui.nail_first.model.Appointment;
import com.mrui.nail_first.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository repo;

    // 显式构造器，避免 Lombok @RequiredArgsConstructor 失效的问题
    public AppointmentService(AppointmentRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AppointmentDTO create(CreateAppointmentRequest req) {
        LocalDateTime start = req.startTime();
        int minutes = (req.durationMinutes() == null ? 60 : req.durationMinutes());
        LocalDateTime end = start.plusMinutes(minutes);

        boolean conflict = repo.existsByStartTimeBetween(start, end.minusSeconds(1));
        if (conflict) throw new IllegalArgumentException("该时间段已有预约，请选择其他时间");

        // 不用 builder，直接 new + setter
        Appointment appt = new Appointment();
        appt.setServiceType(req.serviceType());
        appt.setCustomerName(req.customerName());
        appt.setPhone(req.phone());
        appt.setStartTime(start);
        appt.setDurationMinutes(minutes);
        appt.setRemark(req.remark());

        appt = repo.save(appt);
        return toDto(appt);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> listByDate(LocalDate date) {
        LocalDateTime s = date.atStartOfDay();
        LocalDateTime e = s.plusDays(1);
        return repo.findByStartTimeBetweenOrderByStartTimeAsc(s, e)
                .stream().map(this::toDto).toList();
    }

    private AppointmentDTO toDto(Appointment a) {
        return new AppointmentDTO(
                a.getId(), a.getServiceType(), a.getCustomerName(),
                a.getPhone(), a.getStartTime(), a.getDurationMinutes(), a.getRemark()
        );
    }
}
