package com.code.jamie.droid_ussd.service;

import com.code.jamie.droid_ussd.domain.User;
import com.code.jamie.droid_ussd.domain.Vaccination;

import java.util.List;

public interface VaccineService {
    void saveUser(User user);
    void saveVaccine(Vaccination vaccination);
    List<Vaccination> search(String owner);
    User findUserByPhone(String phoneNumber);
    void deleteVaccine(String owner,String term);
}
