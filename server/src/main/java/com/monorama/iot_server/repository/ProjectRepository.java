package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.Project;
import com.monorama.iot_server.domain.type.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
    SELECT p FROM Project p
    WHERE
        (:includeBoth = true AND (p.projectType = com.monorama.iot_server.domain.type.ProjectType.HEALTH_DATA OR p.projectType = com.monorama.iot_server.domain.type.ProjectType.BOTH) OR
         :includeBoth = false AND p.projectType = com.monorama.iot_server.domain.type.ProjectType.HEALTH_DATA)
      AND p.startDate <= CURRENT_DATE
      AND p.endDate >= CURRENT_DATE
      AND p.maxParticipant > p.curParticipant
    """)
    List<Project> findActiveHealthProjects(@Param("includeBoth") boolean includeBoth);

    @Query("SELECT p FROM Project p WHERE p.id = :projectId AND p.projectType != :excludedType")
    Optional<Project> findByIdAndProjectTypeNot(@Param("projectId") Long projectId, @Param("excludedType") ProjectType excludedType);

    @Query("""
    SELECT p FROM Project p
    WHERE p.id = :projectId
      AND p.startDate <= CURRENT_DATE
      AND p.endDate >= CURRENT_DATE
      AND (p.projectType = com.monorama.iot_server.domain.type.ProjectType.HEALTH_DATA
           OR p.projectType = com.monorama.iot_server.domain.type.ProjectType.BOTH)
    """)
    Optional<Project> findActiveHealthProjectById(@Param("projectId") Long projectId);

    @Query("""
    SELECT p FROM Project p
    WHERE p.user.id = :pmId
    """)
    List<Project> findAllByPMId(Long pmId);

    @Query("""
    SELECT p FROM Project p
    WHERE
        (:includeBoth = true AND (p.projectType = com.monorama.iot_server.domain.type.ProjectType.AIR_QUALITY OR p.projectType = com.monorama.iot_server.domain.type.ProjectType.BOTH) OR
         :includeBoth = false AND p.projectType = com.monorama.iot_server.domain.type.ProjectType.AIR_QUALITY)
      AND p.startDate <= CURRENT_DATE
      AND p.endDate >= CURRENT_DATE
      AND p.maxParticipant > p.curParticipant
    """)
    List<Project> findActiveAirQualityProjects(@Param("includeBoth") boolean includeBoth);

    @Query("""
    SELECT p FROM Project p
    WHERE p.id = :projectId
      AND p.startDate <= CURRENT_DATE
      AND p.endDate >= CURRENT_DATE
      AND (p.projectType = com.monorama.iot_server.domain.type.ProjectType.AIR_QUALITY
           OR p.projectType = com.monorama.iot_server.domain.type.ProjectType.BOTH)
    """)
    Optional<Project> findActiveAirQualityProjectById(@Param("projectId") Long projectId);
    // TODO: 인원 관련 로직 확인해보기
}
