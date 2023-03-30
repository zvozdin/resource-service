package com.example.resourceservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
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

    public FileTrackingEntity() {

    }

    public FileTrackingEntity(String name, String trackingId) {
        this.name = name;
        this.trackingId = trackingId;
    }

}
