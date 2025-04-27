package com.coindesk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "EXCHANGE_RATE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "COINDESK_ID", nullable = false)
    private CoindeskDataEntity coindeskData;
    
    @ManyToOne
    @JoinColumn(name = "CURRENCY_CODE", nullable = false)
    private CurrencyEntity currency;
    
    @Column(name = "SYMBOL")
    private String symbol;
    
    @Column(name = "RATE", length = 50, nullable = false)
    private String rate;
    
    @Column(name = "RATE_FLOAT", nullable = false)
    private Double rateFloat;
} 