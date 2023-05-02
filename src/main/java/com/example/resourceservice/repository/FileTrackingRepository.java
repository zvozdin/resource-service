package com.example.resourceservice.repository;

import com.example.resourceservice.repository.entity.FileTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileTrackingRepository extends JpaRepository<FileTrackingEntity, Long> {

    Optional<FileTrackingEntity> findByTrackingId(String id);

}
