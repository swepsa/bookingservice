package com.example.bookingsystem.controller;

import com.example.bookingsystem.dto.UserDto;
import com.example.bookingsystem.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        UserDto user1 = new UserDto(1L, "John", "john.doe@example.com");
        UserDto user2 = new UserDto(2L, "Jane", "jane.smith@example.com");

        BDDMockito.given(userService.getAll()).willReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users")
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].name").value("John"))
               .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
               .andExpect(jsonPath("$[1].id").value(2L))
               .andExpect(jsonPath("$[1].name").value("Jane"))
               .andExpect(jsonPath("$[1].email").value("jane.smith@example.com"));
    }

    @Test
    void getAllUsers_whenNoUsers_shouldReturnEmptyList() throws Exception {
        BDDMockito.given(userService.getAll()).willReturn(List.of());

        mockMvc.perform(get("/api/users")
                       .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json("[]"));
    }

    @Test
    void getAllUsers_internalError_returnError() throws Exception {
        BDDMockito.given(userService.getAll()).willThrow(IllegalArgumentException.class);

        String response = mockMvc.perform(get("/api/users")
                                         .accept(MediaType.APPLICATION_JSON))
                                 .andExpect(status().isInternalServerError())
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = objectMapper.readValue(response, Map.class);

        assertThat(map).isNotNull()
                       .containsEntry("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
                       .containsEntry("error", "Internal Server Error");
    }
}
