package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User deactivateUser(User user) {
        user.setEnabled(false);
        return userRepository.save(user);
    }
}

