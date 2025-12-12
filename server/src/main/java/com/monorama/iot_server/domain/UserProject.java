package com.monorama.iot_server.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Table(name = "user_project_tb")
public class UserProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_project_id", updatable = false)
    private Long id;

    /*** basic information ***/
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDate createdAt;

    /*** mapping information ***/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    /*** business logic ***/
//    public static UserProject create(User user, Project project) {
//        UserProject userProject = new UserProject();
//        userProject.user = user;
//        userProject.project = project;
//
//        user.getUserProjectList().add(userProject);
//        project.getUserProjectList().add(userProject);
//
//        return userProject;
//    }

    @Builder
    public UserProject(User user, Project project) {
        this.user = user;
        this.project = project;
    }

}
