package com.example.resourceservice.repository;

import com.example.resourceservice.model.FileTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTrackingRepository extends JpaRepository<FileTrackingEntity, Long> {
}
