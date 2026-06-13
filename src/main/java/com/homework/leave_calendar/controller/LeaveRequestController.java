package com.homework.leave_calendar.controller;

import com.homework.leave_calendar.dto.LeaveRequestCreateDto;
import com.homework.leave_calendar.dto.LeaveRequestDto;
import com.homework.leave_calendar.dto.LeaveStatusUpdateDto;
import com.homework.leave_calendar.model.LeaveStatus;
import com.homework.leave_calendar.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave Requests", description = "Manage team leave requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @Operation(summary = "Get all leave requests, optionally filtered by member or status")
    @GetMapping
    public ResponseEntity<List<LeaveRequestDto>> getAll(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) LeaveStatus status) {
        return ResponseEntity.ok(leaveRequestService.getAll(memberId, status));
    }

    @Operation(summary = "Get a leave request by ID")
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.getById(id));
    }

    @Operation(summary = "Create a new leave request")
    @PostMapping
    public ResponseEntity<LeaveRequestDto> create(@RequestBody @Valid LeaveRequestCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequestService.create(dto));
    }

    @Operation(summary = "Update the status of a leave request")
    @PatchMapping("/{id}/status")
    public ResponseEntity<LeaveRequestDto> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid LeaveStatusUpdateDto dto) {
        return ResponseEntity.ok(leaveRequestService.updateStatus(id, dto.status()));
    }

    @Operation(summary = "Delete a leave request")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}