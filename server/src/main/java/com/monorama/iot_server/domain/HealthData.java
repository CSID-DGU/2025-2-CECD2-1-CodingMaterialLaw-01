package com.monorama.iot_server.domain;

import com.monorama.iot_server.domain.embedded.HealthDataItem;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_data_tb")
@NoArgsConstructor
public class HealthData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_data_id")
    private Long id;

    /*** basic information ***/ // TODO: 수정 요함 만들어진 날짜 받아야될듯
    @Column(name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Embedded
    private HealthDataItem healthDataItem;

    /*** mapping information ***/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /*** business logic ***/
    @Builder
    public HealthData(Long id, LocalDateTime createAt, HealthDataItem healthDataItem, User user) {
        this.id = id;
        this.createdAt = createAt;
        this.healthDataItem = healthDataItem;
        this.user = user;
    }
}
