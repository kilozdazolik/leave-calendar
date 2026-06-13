package com.homework.leave_calendar.dto;

import com.homework.leave_calendar.model.LeaveStatus;
import jakarta.validation.constraints.NotNull;

public record LeaveStatusUpdateDto(

        @NotNull(message = "Status is required")
        LeaveStatus status
) {}