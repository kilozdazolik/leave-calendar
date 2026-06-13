package com.homework.leave_calendar.service;

import com.homework.leave_calendar.dto.LeaveRequestCreateDto;
import com.homework.leave_calendar.exception.OverlappingLeaveException;
import com.homework.leave_calendar.mapper.LeaveRequestMapper;
import com.homework.leave_calendar.model.LeaveRequest;
import com.homework.leave_calendar.model.TeamMember;
import com.homework.leave_calendar.repository.LeaveRequestRepository;
import com.homework.leave_calendar.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveRequestServiceTest {

    @Mock private LeaveRequestRepository leaveRequestRepository;
    @Mock private TeamMemberRepository teamMemberRepository;
    @Mock private LeaveRequestMapper mapper;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    private TeamMember alice;
    private LeaveRequestCreateDto dto;

    @BeforeEach
    void setUp() {
        alice = new TeamMember(1L, "Alice");

        dto = new LeaveRequestCreateDto(
                1L,
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 5),
                "Holiday"
        );
    }

    @Test
    void create_shouldSave_whenNoOverlappingRequests() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(leaveRequestRepository.findOverlapping(any(), any(), any(), any())).thenReturn(List.of());
        when(leaveRequestRepository.save(any())).thenReturn(new LeaveRequest());
        when(mapper.toDto(any(LeaveRequest.class))).thenReturn(null);

        leaveRequestService.create(dto);

        verify(leaveRequestRepository).save(any(LeaveRequest.class));
    }

    @Test
    void create_shouldThrowOverlappingLeaveException_whenConflictExists() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(leaveRequestRepository.findOverlapping(any(), any(), any(), any()))
                .thenReturn(List.of(new LeaveRequest()));

        assertThatThrownBy(() -> leaveRequestService.create(dto))
                .isInstanceOf(OverlappingLeaveException.class)
                .hasMessageContaining("Alice");

        verify(leaveRequestRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenMemberNotFound() {
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaveRequestService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("1");

        verify(leaveRequestRepository, never()).save(any());
    }
}