package com.monorama.iot_server.domain;

import com.monorama.iot_server.domain.embedded.AirQualityDataFlag;
import com.monorama.iot_server.domain.embedded.HealthDataFlag;
import com.monorama.iot_server.domain.embedded.PersonalInfoFlag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_data_permission_tb")
public class UserDataPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_data_permission_id", updatable = false)
    private Long id;

    /*** basic information ***/
    @Embedded
    private PersonalInfoFlag personalInfoFlag;

    @Embedded
    private HealthDataFlag healthDataFlag;

    @Embedded
    private AirQualityDataFlag airQualityDataFlag;

    /*** mapping information ***/
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public UserDataPermission(User user) {
        this.personalInfoFlag = new PersonalInfoFlag();
        this.healthDataFlag = new HealthDataFlag();
        this.airQualityDataFlag = new AirQualityDataFlag();
        this.user = user;
    }
}
