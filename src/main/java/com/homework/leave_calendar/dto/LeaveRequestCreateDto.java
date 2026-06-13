package com.homework.leave_calendar.dto;

import com.homework.leave_calendar.validation.ValidDateRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@ValidDateRange
public record LeaveRequestCreateDto(

        @NotNull(message = "Team member ID is required")
        Long teamMemberId,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotBlank(message = "Reason cannot be empty")
        String reason
) {}