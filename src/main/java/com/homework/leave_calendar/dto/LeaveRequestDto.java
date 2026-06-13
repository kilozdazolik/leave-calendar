package com.homework.leave_calendar.dto;

import com.homework.leave_calendar.model.LeaveStatus;

import java.time.LocalDate;

public record LeaveRequestDto(
        Long id,
        TeamMemberDto teamMember,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        LeaveStatus status
) {}