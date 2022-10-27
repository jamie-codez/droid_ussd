package com.code.jamie.droid_ussd.service;

import com.code.jamie.droid_ussd.domain.User;
import com.code.jamie.droid_ussd.domain.Vaccination;
import com.code.jamie.droid_ussd.repo.UserRepo;
import com.code.jamie.droid_ussd.repo.VaccineRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VaccineServiceImpl implements VaccineService{
    private final UserRepo userRepo;
    private final VaccineRepo vaccineRepo;
    @Override
    public void saveUser(User user) {
        userRepo.save(user);
    }

    @Override
    public void saveVaccine(Vaccination vaccination) {
        vaccineRepo.save(vaccination);
    }

    @Override
    public List<Vaccination> search(String owner) {
       return vaccineRepo.search(owner);
    }

    @Override
    public User findUserByPhone(String phoneNumber) {
        return userRepo.findUserByPhoneNumber(phoneNumber);
    }

    @Override
    public void deleteVaccine(String owner, String term) {
        vaccineRepo.deleteVaccine(owner,term);
    }
}
