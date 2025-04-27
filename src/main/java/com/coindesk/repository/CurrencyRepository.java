package com.coindesk.repository;

import com.coindesk.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, String> {
    
    /**
     * 通過貨幣代碼查詢貨幣信息
     * @param code 貨幣代碼
     * @return 貨幣實體
     */
    Optional<CurrencyEntity> findByCode(String code);
} 