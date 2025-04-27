package com.coindesk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "COINDESK_DATA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoindeskDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "UPDATED_TIME", nullable = false)
    private String updatedTime;
    
    @Column(name = "UPDATED_ISO", nullable = false)
    private String updatedIso;
    
    @Column(name = "UPDATED_UK")
    private String updatedUk;
    
    @Column(name = "DISCLAIMER")
    private String disclaimer;
    
    @Column(name = "CHART_NAME")
    private String chartName;
    
    @Column(name = "CREATE_TIME", nullable = false)
    private LocalDateTime createTime;
} 