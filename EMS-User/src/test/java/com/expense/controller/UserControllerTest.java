package com.expense.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.expense.entity.User;
import com.expense.entity.User.Role;
import com.expense.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder; 

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testAuthenticateUser() throws Exception {
        User user = new User();
        user.setEmail("thanuja@gmail.com");
        user.setPassword("thanu123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        mockMvc.perform(post("/user/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUser() throws Exception {
        User user = new User();
        user.setEmail("thanuja@gmail.com");
        user.setPassword("thanu123");
        user.setName("Test User");

        Role role = Role.USER; 
        user.setRole(role);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword"); 
        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        User user = new User();
        user.setEmail("thanuja@gmail.com");
        user.setPassword("thanu123");
        user.setName("Test User");

        Role role = Role.USER; 
        user.setRole(role); 

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        mockMvc.perform(post("/user/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}
