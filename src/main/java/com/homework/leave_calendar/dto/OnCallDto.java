package com.homework.leave_calendar.dto;

import java.time.LocalDate;

public record OnCallDto(
        TeamMemberDto teamMember,
        LocalDate weekStart,
        LocalDate weekEnd,
        boolean hasConflict
) {}