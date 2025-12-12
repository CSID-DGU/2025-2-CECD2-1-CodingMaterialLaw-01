package com.monorama.iot_server.domain;

import com.monorama.iot_server.domain.embedded.PersonalInfoItem;
import com.monorama.iot_server.type.EProvider;
import com.monorama.iot_server.type.ERole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate
@Table(name = "user_tb")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /*** social login information ***/
    @Column(name = "social_id")
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 10)
    private EProvider provider;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private ERole role;

    @Column(name = "is_login")
    private Boolean isLogin;

    /*** basic information ***/
    @Embedded
    private PersonalInfoItem personalInfo;

    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDate createdAt;

    /*** mapping information ***/
    @OneToMany(mappedBy = "user")
    private List<HealthData> healthDataList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<AirQualityData> airQualityDataList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Project> projectList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<AirMetaData> airMetaDataList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserProject> userProjectList = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private UserDataPermission userDataPermission;

    /*** constructor ***/
    @Builder
    public User(ERole role, String socialId, EProvider provider) {
        this.role = role;
        this.socialId = socialId;
        this.provider = provider;
    }

    /*** business logic ***/
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setIsLogin(Boolean isLogin) {
        this.isLogin = isLogin;
    }

    public void register(PersonalInfoItem personalInfo, ERole role) {
        this.personalInfo = personalInfo;
        this.role = role;
    }

    public void updateRoleToBoth() {
        this.role = ERole.BOTH_USER;
    }

    public String getEmail() {
        return personalInfo.getEmail();
    }

    private void setUserDataPermission(UserDataPermission userDataPermission) {
        this.userDataPermission = userDataPermission;
    }

    public void withdrawUser() {
        this.isLogin = false;
        this.socialId = null;
        this.refreshToken = null;
        this.personalInfo.withdraw();
        this.role = ERole.WITHDRAWN;
    }
}
