package com.code.jamie.droid_ussd.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.AUTO;

@Entity(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id",nullable = false)
    private Long id;
    @Column(name = "phone",nullable = false)
    private String phoneNumber;
    @Column(name = "pin",nullable = false)
    private String pin;
}
