package com.salon.booking.entity;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "timeslot")
public class TimeslotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "from_time", nullable = false)
    private LocalTime fromTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "duration_id", nullable = false)
    private DurationEntity duration;

    @ManyToMany
    @JoinTable(name = "order_timeslot",
            joinColumns = @JoinColumn(name = "timeslot_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<OrderEntity> orders;
}
