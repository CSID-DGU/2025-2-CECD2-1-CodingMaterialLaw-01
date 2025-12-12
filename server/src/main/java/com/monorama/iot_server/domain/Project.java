package com.monorama.iot_server.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monorama.iot_server.domain.embedded.AirQualityDataFlag;
import com.monorama.iot_server.domain.embedded.HealthDataFlag;
import com.monorama.iot_server.domain.embedded.PersonalInfoFlag;
import com.monorama.iot_server.domain.type.ProjectType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "project_tb")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    /*** basic information ***/
    @Enumerated(EnumType.STRING)
    @Column(name = "project_type")
    private ProjectType projectType;

    @Column(name = "title")
    private String title;

    @Column(name = "max_participant")
    private Integer maxParticipant;

    @Column(name = "cur_participant")
    private Integer curParticipant ;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(name = "description")
    private String description;

    @Column(name = "terms_of_policy")
    private String termsOfPolicy;

    @Column(name = "privacy_policy")
    private String privacyPolicy;

    @Column(name = "health_data_consent")
    private String healthDataConsent;

    @Column(name = "air_data_consent")
    private String airDataConsent;

    @Column(name = "local_data_terms_of_service")
    private String localDataTermsOfService;

    @Embedded
    private PersonalInfoFlag personalInfoFlag = new PersonalInfoFlag();

    @Embedded
    private HealthDataFlag healthDataFlag = new HealthDataFlag();

    @Embedded
    private AirQualityDataFlag airQualityDataFlag = new AirQualityDataFlag();

    /*** mapping information ***/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pm_id")
    private User user;

    @OneToMany(mappedBy = "project")
    private List<UserProject> userProjectList = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AirMetaDataItem> airMetaDataItemList = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<AirMetaData> airMetaDataList = new ArrayList<>();

    /*** constructor ***/
    @PrePersist
    public void prePersist() {
        curParticipant = 0;
    }

    @Builder
    public Project(ProjectType projectType,
                   String title,
                   Integer participant,
                   LocalDate startDate,
                   LocalDate endDate,
                   String description,
                   String termsOfPolicy,
                   String privacyPolicy,
                   String healthDataConsent,
                   String airDataConsent,
                   String localDataTermsOfService,
                   PersonalInfoFlag personalInfoFlag,
                   HealthDataFlag healthDataFlag,
                   AirQualityDataFlag airQualityDataFlag) {
        this.projectType = projectType;
        this.title = title;
        this.maxParticipant = participant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.termsOfPolicy = termsOfPolicy;
        this.privacyPolicy = privacyPolicy;
        this.healthDataConsent = healthDataConsent;
        this.airDataConsent = airDataConsent;
        this.localDataTermsOfService = localDataTermsOfService;
        this.personalInfoFlag = personalInfoFlag;
        this.healthDataFlag = healthDataFlag;
        this.airQualityDataFlag = airQualityDataFlag;
    }

    /*** business logic ***/
    public void setUser(User user) {
        this.user = user;
        user.getProjectList().add(this);
    }

    public void increaseParticipant() {
        this.curParticipant += 1;
    }
}
