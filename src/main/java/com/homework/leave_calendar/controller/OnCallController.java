package com.homework.leave_calendar.controller;

import com.homework.leave_calendar.dto.OnCallDto;
import com.homework.leave_calendar.service.OnCallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oncall")
@RequiredArgsConstructor
@Tag(name = "On-Call Schedule", description = "View the weekly on-call rotation")
public class OnCallController {

    private final OnCallService onCallService;

    @Operation(summary = "Get on-call schedule", description = "Returns weekly on-call assignments. Flags conflicts where the on-call person has approved leave.")
    @GetMapping
    public ResponseEntity<List<OnCallDto>> getSchedule(
            @RequestParam(defaultValue = "8") int weeks) {
        return ResponseEntity.ok(onCallService.getSchedule(weeks));
    }
}