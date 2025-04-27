package com.coindesk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "CURRENCY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyEntity {

    @Id
    @Column(name = "CODE", length = 10)
    private String code;

    @Column(name = "CHINESE_NAME", nullable = false, length = 50)
    private String chineseName;
    
    @Column(name = "ENGLISH_NAME", length = 100)
    private String englishName;
} 