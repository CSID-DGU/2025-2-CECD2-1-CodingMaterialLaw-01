package com.monorama.iot_server.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "air_meta_data_tb")
@NoArgsConstructor
public class AirMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "air_meta_data_id")
    private Long id;

    /*** basic information ***/
    @Column(name = "meta_data_value")
    private String metaDataValue;

    /*** mapping information ***/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "air_meta_data_item_id")
    private AirMetaDataItem airMetaDataItem;

    @Builder
    public AirMetaData(String metaDataValue) {
        this.metaDataValue = metaDataValue;
    }

    /*** business logic ***/
    private void setUser(User user) {
        this.user = user;
        user.getAirMetaDataList().add(this);
    }

    private void setProject(Project project) {
        this.project = project;
        project.getAirMetaDataList().add(this);
    }

    private void setAirMetaDataItem(AirMetaDataItem airMetaDataItem) {
        this.airMetaDataItem = airMetaDataItem;
        airMetaDataItem.getAirMetaDataList().add(this);
    }

    public void setRelations(User user, Project project, AirMetaDataItem airMetaDataItem) {
        this.user = user;
        user.getAirMetaDataList().add(this);

        this.project = project;
        project.getAirMetaDataList().add(this);

        this.airMetaDataItem = airMetaDataItem;
        airMetaDataItem.getAirMetaDataList().add(this);
    }

}
