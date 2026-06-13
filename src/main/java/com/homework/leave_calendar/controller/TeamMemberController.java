package com.homework.leave_calendar.controller;

import com.homework.leave_calendar.dto.TeamMemberDto;
import com.homework.leave_calendar.mapper.LeaveRequestMapper;
import com.homework.leave_calendar.repository.TeamMemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Team Members", description = "View team members")
public class TeamMemberController {

    private final TeamMemberRepository teamMemberRepository;
    private final LeaveRequestMapper mapper;

    @Operation(summary = "Get all team members")
    @GetMapping
    public ResponseEntity<List<TeamMemberDto>> getAll() {
        return ResponseEntity.ok(
                teamMemberRepository.findAll()
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }
}