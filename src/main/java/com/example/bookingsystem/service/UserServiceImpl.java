package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UserDto;
import com.example.bookingsystem.mapper.UserMapper;
import com.example.bookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        log.info("Fetching all users from the database");
        List<UserDto> users = userRepository.findAll().stream()
                                            .map(userMapper::toDto)
                                            .toList();
        log.debug("Found {} users", users.size());
        return users;
    }
}
