package com.monorama.iot_server.domain;

import com.monorama.iot_server.domain.type.DataType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "air_meta_data_item_tb")
@NoArgsConstructor
public class AirMetaDataItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "air_meta_data_item_id")
    private Long id;

    /*** basic information ***/
    @Column(name = "data_name")
    private String dataName;

    @Column(name = "data_type")
    @Enumerated(EnumType.STRING)
    private DataType dataType;

    /*** mapping information ***/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "airMetaDataItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AirMetaData> airMetaDataList = new ArrayList<>();

    @Builder
    public AirMetaDataItem(String dataName, DataType dataType) {
        this.dataName = dataName;
        this.dataType = dataType;
    }

    /*** business logic ***/
    public void setProject(Project project) {
        this.project = project;
        project.getAirMetaDataItemList().add(this);
    }
}
