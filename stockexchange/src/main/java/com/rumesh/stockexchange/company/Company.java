package com.rumesh.stockexchange.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "company")
@DynamicUpdate
@DynamicInsert
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "symbol", unique = true, nullable = false, columnDefinition = "varchar(50) UNIQUE")
    private String symbol;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "price", nullable = false, columnDefinition = "double")
    private Float price;

    @Column(name = "total_shares", nullable = false, columnDefinition = "integer")
    private Float totalShares;
    
    @Column(name= "currency", nullable = false)
    private String currency;
    
    @Column(name="last_updated")
    private String lastUpdated;

//   @Column(name = "url")
//    private String url;

}
