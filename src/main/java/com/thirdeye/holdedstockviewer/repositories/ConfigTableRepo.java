package com.thirdeye.holdedstockviewer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.holdedstockviewer.entity.ConfigTable;


@Repository
public interface ConfigTableRepo extends JpaRepository<ConfigTable, Long> {
}
