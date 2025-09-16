package com.mrui.nail_first.controller;

import com.mrui.nail_first.dto.AppointmentDTO;
import com.mrui.nail_first.dto.CreateAppointmentRequest;
import com.mrui.nail_first.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:5173"}) // React 本地联调

public class AppointmentController {

    private final AppointmentService service;

    /** 新建预约 */
    @PostMapping
    public AppointmentDTO create(@RequestBody CreateAppointmentRequest req) {
        return service.create(req);
    }

    /** 按天查询，如 /api/appointments?date=2025-09-05 */
    @GetMapping
    public List<AppointmentDTO> listByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.listByDate(date);
    }
}