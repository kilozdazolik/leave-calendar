package com.homework.leave_calendar.mapper;

import com.homework.leave_calendar.dto.LeaveRequestDto;
import com.homework.leave_calendar.dto.TeamMemberDto;
import com.homework.leave_calendar.model.LeaveRequest;
import com.homework.leave_calendar.model.TeamMember;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    public LeaveRequestDto toDto(LeaveRequest entity) {
        return new LeaveRequestDto(
                entity.getId(),
                toDto(entity.getTeamMember()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getReason(),
                entity.getStatus()
        );
    }

    public TeamMemberDto toDto(TeamMember entity) {
        return new TeamMemberDto(entity.getId(), entity.getName());
    }
}