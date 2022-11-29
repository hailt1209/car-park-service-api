package com.hailt.carpark.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Builder
@Entity
@Table(name = "car_park_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarParkAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_park_id", referencedColumnName = "id")
    private CarPark carPark;

    @Column(name = "total_lots")
    private Integer totalLots;

    @Column(name = "lots_available")
    private Integer lotsAvailable;

    @Column(name = "last_updated_on")
    private Date lastUpdatedOn;
}
