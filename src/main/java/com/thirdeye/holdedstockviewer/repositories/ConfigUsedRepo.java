package com.thirdeye.holdedstockviewer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.holdedstockviewer.entity.ConfigUsed;

@Repository
public interface ConfigUsedRepo extends JpaRepository<ConfigUsed, Long> {
}
