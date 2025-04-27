package com.coindesk.repository;

import com.coindesk.entity.CoindeskDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoindeskDataRepository extends JpaRepository<CoindeskDataEntity, Long> {
    
    /**
     * 查詢最新的 Coindesk 數據
     * @return 最新的 CoindeskDataEntity
     */
    @Query("SELECT c FROM CoindeskDataEntity c ORDER BY c.createTime DESC")
    Optional<CoindeskDataEntity> findLatest();
} 