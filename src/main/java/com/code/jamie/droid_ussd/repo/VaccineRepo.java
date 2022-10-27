package com.code.jamie.droid_ussd.repo;

import com.code.jamie.droid_ussd.domain.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineRepo extends JpaRepository<Vaccination,Long> {
    @Query("select u from vaccination u where u.owner like lower(concat('%',:owner,'%'))")
    List<Vaccination> search(@Param(value = "owner")String owner);
    @Modifying
    @Query("delete from vaccination u where u.owner like lower(concat('%',:owner,'%')) and " +
            "u.vaccineName like lower(concat('%',:term,'%'))")
    Vaccination deleteVaccine(@Param(value = "owner")String owner,@Param(value = "term")String term);
}
