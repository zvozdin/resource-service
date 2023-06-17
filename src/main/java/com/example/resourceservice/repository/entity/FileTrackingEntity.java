package com.example.resourceservice.repository.entity;

import com.example.resourceservice.client.entity.StorageType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "resource_tracking")
public class FileTrackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "resource_name")
    private String name;

    @Column(name = "resource_tracking_id", unique = true)
    private String trackingId;

    @Column(name = "resource_path")
    private String resourcePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type")
    private StorageType type;

    public FileTrackingEntity() {

    }

    public FileTrackingEntity(String name, String trackingId, String resourcePath, StorageType type) {
        this.name = name;
        this.trackingId = trackingId;
        this.resourcePath = resourcePath;
        this.type = type;
    }

}
