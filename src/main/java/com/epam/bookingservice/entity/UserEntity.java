package com.epam.bookingservice.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "\"user\"")
public class UserEntity {

    @Id
    @GeneratedValue
    private final Integer id;

    @Column
    private final String name;

    @Column
    private final String email;

    @Column
    private final String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private final RoleEntity role;
}
