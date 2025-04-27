package com.coindesk.repository;

import com.coindesk.entity.CoindeskDataEntity;
import com.coindesk.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {
    
    /**
     * 根據 CoindeskData 查詢匯率資料
     */
    List<ExchangeRateEntity> findByCoindeskData(CoindeskDataEntity coindeskData);
    
    /**
     * 根據 CoindeskData ID 查詢匯率資料
     */
    @Query("SELECT e FROM ExchangeRateEntity e WHERE e.coindeskData.id = :coindeskId")
    List<ExchangeRateEntity> findByCoindeskDataId(@Param("coindeskId") Long coindeskId);
    
    /**
     * 根據幣別代碼和 CoindeskData 查詢匯率資料
     */
    @Query("SELECT e FROM ExchangeRateEntity e WHERE e.currency.code = :currencyCode AND e.coindeskData.id = :coindeskId")
    ExchangeRateEntity findByCurrencyCodeAndCoindeskDataId(
            @Param("currencyCode") String currencyCode,
            @Param("coindeskId") Long coindeskId);
} 