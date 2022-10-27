package com.code.jamie.droid_ussd.repo;

import com.code.jamie.droid_ussd.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findUserByPhoneNumber(String phone);
}
