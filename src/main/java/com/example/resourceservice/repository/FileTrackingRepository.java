package com.example.resourceservice.repository;

import com.example.resourceservice.repository.entity.FileTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileTrackingRepository extends JpaRepository<FileTrackingEntity, Long> {
}
