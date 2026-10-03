package com.gsdeveloper.bookmyslot.controller;

import com.gsdeveloper.bookmyslot.config.JwtAuthenticationFilter;
import com.gsdeveloper.bookmyslot.config.SpringSecurity;
import com.gsdeveloper.bookmyslot.controller.AuthController;
import com.gsdeveloper.bookmyslot.entity.User;
import com.gsdeveloper.bookmyslot.enums.Role;
import com.gsdeveloper.bookmyslot.service.AuthService;
import com.gsdeveloper.bookmyslot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PublicController.class, AuthController.class})
@Import(SpringSecurity.class)
class PublicControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void registerUserEndpointShouldBeAccessibleWithoutAuthentication() throws Exception {
        User savedUser = new User();
        savedUser.setFullName("Sanket");
        savedUser.setEmail("mukta@gmail.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.CLIENT);

        when(userService.saveUser(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/public/register-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Sanket\",\"email\":\"mukta@gmail.com\",\"password\":\"123456\",\"role\":\"client\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void loginEndpointShouldBeAccessibleWithoutAuthentication() throws Exception {
        com.gsdeveloper.bookmyslot.dto.AuthResponse response =
                new com.gsdeveloper.bookmyslot.dto.AuthResponse("jwt-token", "Login Successful");
        when(authService.login("gaurav@gmail.com", "123456")).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"gaurav@gmail.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());
    }
}
