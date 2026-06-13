package com.homework.leave_calendar.service;

import com.homework.leave_calendar.dto.OnCallDto;
import com.homework.leave_calendar.mapper.LeaveRequestMapper;
import com.homework.leave_calendar.model.LeaveRequest;
import com.homework.leave_calendar.model.LeaveStatus;
import com.homework.leave_calendar.model.TeamMember;
import com.homework.leave_calendar.repository.LeaveRequestRepository;
import com.homework.leave_calendar.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OnCallService {

    private static final LocalDate ROTATION_ANCHOR = LocalDate.of(2025, 1, 6);

    private final TeamMemberRepository teamMemberRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper mapper;

    public List<OnCallDto> getSchedule(int weeks) {
        LocalDate currentWeekStart = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return IntStream.range(0, weeks)
                .mapToObj(i -> buildOnCallDto(currentWeekStart.plusWeeks(i)))
                .toList();
    }

    private OnCallDto buildOnCallDto(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        TeamMember member = resolveOnCallMember(weekStart);
        boolean hasConflict = hasApprovedLeave(member, weekStart, weekEnd);

        return new OnCallDto(mapper.toDto(member), weekStart, weekEnd, hasConflict);
    }

    private TeamMember resolveOnCallMember(LocalDate weekStart) {
        List<TeamMember> members = teamMemberRepository.findAllByOrderByIdAsc();
        if (members.isEmpty()) {
            throw new EntityNotFoundException("No team members found");
        }
        long weeksDiff = ChronoUnit.WEEKS.between(ROTATION_ANCHOR, weekStart);
        int index = (int) ((weeksDiff % members.size() + members.size()) % members.size());
        return members.get(index);
    }

    private boolean hasApprovedLeave(TeamMember member, LocalDate weekStart, LocalDate weekEnd) {
        return leaveRequestRepository
                .findOverlapping(member.getId(), weekStart, weekEnd, LeaveStatus.REJECTED)
                .stream()
                .anyMatch(l -> l.getStatus() == LeaveStatus.APPROVED);
    }
}