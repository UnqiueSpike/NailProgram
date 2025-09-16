package com.mrui.nail_first.dto;

import com.mrui.nail_first.model.ServiceType;

import java.time.LocalDateTime;

public record AppointmentDTO(
        Long id,
        ServiceType serviceType,
        String customerName,
        String phone,
        LocalDateTime startTime,
        Integer durationMinutes,
        String remark
) {}