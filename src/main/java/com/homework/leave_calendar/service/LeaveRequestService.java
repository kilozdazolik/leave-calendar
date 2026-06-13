package com.homework.leave_calendar.service;

import com.homework.leave_calendar.dto.LeaveRequestCreateDto;
import com.homework.leave_calendar.dto.LeaveRequestDto;
import com.homework.leave_calendar.exception.OverlappingLeaveException;
import com.homework.leave_calendar.mapper.LeaveRequestMapper;
import com.homework.leave_calendar.model.LeaveRequest;
import com.homework.leave_calendar.model.LeaveStatus;
import com.homework.leave_calendar.model.TeamMember;
import com.homework.leave_calendar.repository.LeaveRequestRepository;
import com.homework.leave_calendar.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final LeaveRequestMapper mapper;

    public List<LeaveRequestDto> getAll(Long memberId, LeaveStatus status) {
        return leaveRequestRepository.findWithFilters(memberId, status)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public LeaveRequestDto getById(Long id) {
        return leaveRequestRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found: " + id));
    }

    public LeaveRequestDto create(LeaveRequestCreateDto dto) {
        TeamMember member = findMemberOrThrow(dto.teamMemberId());
        checkForOverlaps(dto, member);
        LeaveRequest saved = leaveRequestRepository.save(toEntity(dto, member));
        return mapper.toDto(saved);
    }

    public LeaveRequestDto updateStatus(Long id, LeaveStatus newStatus) {
        LeaveRequest entity = findLeaveOrThrow(id);
        entity.setStatus(newStatus);
        return mapper.toDto(leaveRequestRepository.save(entity));
    }

    public void delete(Long id) {
        findLeaveOrThrow(id);
        leaveRequestRepository.deleteById(id);
    }

    private void checkForOverlaps(LeaveRequestCreateDto dto, TeamMember member) {
        List<LeaveRequest> conflicts = leaveRequestRepository.findOverlapping(
                dto.teamMemberId(),
                dto.startDate(),
                dto.endDate(),
                LeaveStatus.REJECTED
        );
        if (!conflicts.isEmpty()) {
            throw new OverlappingLeaveException(
                    "Leave request overlaps with an existing one for " + member.getName()
            );
        }
    }

    private LeaveRequest toEntity(LeaveRequestCreateDto dto, TeamMember member) {
        LeaveRequest entity = new LeaveRequest();
        entity.setTeamMember(member);
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
        entity.setReason(dto.reason());
        return entity;
    }

    private TeamMember findMemberOrThrow(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team member not found: " + id));
    }

    private LeaveRequest findLeaveOrThrow(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leave request not found: " + id));
    }
}