package com.monorama.iot_server.repository;

import com.monorama.iot_server.domain.AirMetaDataItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirMetaDataItemRepository extends JpaRepository<AirMetaDataItem, Long> {

    @Query("""
    SELECT a FROM AirMetaDataItem a
    WHERE a.project.id = :projectId
    """)
    List<AirMetaDataItem> findAllByProjectId(Long projectId);

}
