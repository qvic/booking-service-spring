package com.epam.bookingservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timeslot")
public class TimeslotEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private LocalDate date;

    @Column
    private LocalTime fromTime;

    @Column
    private LocalTime toTime;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
}
