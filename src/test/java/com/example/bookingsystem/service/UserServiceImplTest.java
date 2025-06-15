package com.example.bookingsystem.service;

import com.example.bookingsystem.dto.UserDto;
import com.example.bookingsystem.mapper.UserMapper;
import com.example.bookingsystem.model.User;
import com.example.bookingsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {

    private static final String USER_NAME_1 = "user1";
    private static final String USER_NAME_2 = "user2";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void testGetAll_ReturnsListOfUserDto() {
        // Prepare test data
        User user1 = new User();
        user1.setName(USER_NAME_1);
        User user2 = new User();
        user2.setName(USER_NAME_2);

        UserDto dto1 = new UserDto(1L, USER_NAME_1, "user1@test.com");
        UserDto dto2 = new UserDto(2L, USER_NAME_2, "user2@test.com");

        // Mock repository
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Mock mapper
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        // Call service method
        List<UserDto> result = userService.getAll();

        // Verify interactions and results
        verify(userRepository).findAll();
        verify(userMapper).toDto(user1);
        verify(userMapper).toDto(user2);

        assertThat(result).containsExactly(dto1, dto2);
    }
}
