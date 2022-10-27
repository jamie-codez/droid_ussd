package com.code.jamie.droid_ussd.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;
@Entity(name = "vaccination")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vaccination {
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id",nullable = false)
    private Long id;
    @Column(name = "vaccine_name",nullable = false)
    private String vaccineName;
    @Column(name = "owner",nullable = false)
    private String owner;
    @Column(name = "vaccine_dates",nullable = false)
    @ElementCollection(targetClass = Long.class)
    private List<Long> vaccineDates = new ArrayList<>();
}
