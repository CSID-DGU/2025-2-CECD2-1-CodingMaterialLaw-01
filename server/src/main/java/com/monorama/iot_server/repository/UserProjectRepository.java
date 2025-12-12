package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.domain.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    Boolean existsByUserAndProject(User user, Project project);

    @Query("SELECT up.project FROM UserProject up " +
            "WHERE up.user.id = :userId " +
            "AND up.project.startDate <= :today " +
            "AND up.project.endDate >= :today")
    List<Project> findProgressProjectsByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);

}

