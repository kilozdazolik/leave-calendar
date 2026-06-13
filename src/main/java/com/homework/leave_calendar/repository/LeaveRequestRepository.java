package com.homework.leave_calendar.repository;

import com.homework.leave_calendar.model.LeaveRequest;
import com.homework.leave_calendar.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    @Query("""
        SELECT l FROM LeaveRequest l
        WHERE l.teamMember.id = :memberId
          AND l.status <> :excludedStatus
          AND l.startDate <= :endDate
          AND l.endDate >= :startDate
        """)
    List<LeaveRequest> findOverlapping(
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludedStatus") LeaveStatus excludedStatus
    );

    @Query("""
    SELECT l FROM LeaveRequest l
    WHERE (:memberId IS NULL OR l.teamMember.id = :memberId)
      AND (:status IS NULL OR l.status = :status)
    """)
    List<LeaveRequest> findWithFilters(
            @Param("memberId") Long memberId,
            @Param("status") LeaveStatus status
    );

    List<LeaveRequest> findByTeamMemberId(Long teamMemberId);
}