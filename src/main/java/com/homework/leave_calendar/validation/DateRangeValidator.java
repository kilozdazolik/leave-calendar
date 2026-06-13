package com.homework.leave_calendar.validation;

import com.homework.leave_calendar.dto.LeaveRequestCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, LeaveRequestCreateDto> {

    @Override
    public boolean isValid(LeaveRequestCreateDto dto, ConstraintValidatorContext context) {
        if (dto.startDate() == null || dto.endDate() == null) {
            return true;
        }
        return !dto.endDate().isBefore(dto.startDate());
    }
}