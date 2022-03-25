package com.rumesh.stockexchange.currency;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "currency")
@Data
@RequiredArgsConstructor
public class Currency implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cur_code", nullable = false, columnDefinition = "varchar(100) UNIQUE")
    private String curCode;

    @Column(name = "rate_usd", columnDefinition = "float default 0 NOT NULL")
    private Float rateUsd;

    @Column(name = "last_updated")
    private String lastUpdated;
}
