package com.homework.leave_calendar.config;

import com.homework.leave_calendar.model.TeamMember;
import com.homework.leave_calendar.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final TeamMemberRepository teamMemberRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (teamMemberRepository.count() == 0) {
            teamMemberRepository.saveAll(List.of(
                    new TeamMember(null, "Alice"),
                    new TeamMember(null, "Bob"),
                    new TeamMember(null, "Charlie"),
                    new TeamMember(null, "Diana")
            ));
        }
    }
}