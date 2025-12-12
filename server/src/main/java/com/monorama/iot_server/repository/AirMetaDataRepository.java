package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.AirMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirMetaDataRepository extends JpaRepository<AirMetaData, Long> {

    @Query("""
    SELECT a FROM AirMetaData a
    WHERE a.project.id = :projectId
    """)
    List<AirMetaData> findAllByProjectId(Long projectId);

    @Query("SELECT COUNT(a) > 0 FROM AirMetaData a WHERE a.user.id = :userId AND a.project.id = :projectId")
    Boolean existsByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);


}
