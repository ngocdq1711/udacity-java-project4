package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController controllerUnderTest;
    private UserRepository userRepoMock = mock(UserRepository.class);
    private CartRepository cartRepoMock = mock(CartRepository.class);
    private PasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        controllerUnderTest = new UserController();
        FieldInjector.injectObjects(controllerUnderTest, "userRepository", userRepoMock);
        FieldInjector.injectObjects(controllerUnderTest, "cartRepository", cartRepoMock);
        FieldInjector.injectObjects(controllerUnderTest, "encoder", passwordEncoder);
    }

    @Test
    public void create_user_password_not_match_confirmPass() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("password123");
        request.setConfirmPassword("differentPassword");

        ResponseEntity<?> response = controllerUnderTest.createUser(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
        Assertions.assertEquals("Passwords do not match", response.getBody());
    }

    @Test
    public void testCreateUser_PasswordMismatch() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("userWithMismatchPassword");
        request.setPassword("password123");
        request.setConfirmPassword("password321");

        ResponseEntity<?> response = controllerUnderTest.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCreateUser_InvalidPasswordLength() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("userWithShortPassword");
        request.setPassword("short");
        request.setConfirmPassword("short");

        ResponseEntity<?> response = controllerUnderTest.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCreateUser_UsernameAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("existingUser");
        request.setPassword("validPassword123");
        request.setConfirmPassword("validPassword123");

        when(userRepoMock.findByUsername("existingUser")).thenReturn(new User());

        ResponseEntity<?> response = controllerUnderTest.createUser(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testGetUserByUsername_NotFound() {
        when(userRepoMock.findByUsername("nonexistentUser")).thenReturn(null);
        ResponseEntity<User> response = controllerUnderTest.findByUserName("nonexistentUser");
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetUserByUsername_Found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("existingUser");
        when(userRepoMock.findByUsername("existingUser")).thenReturn(user);

        ResponseEntity<User> response = controllerUnderTest.findByUserName("existingUser");
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetUserById_NotFound() {
        when(userRepoMock.findById(99L)).thenReturn(Optional.empty());
        ResponseEntity<User> response = controllerUnderTest.findById(99L);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetUserById_Found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("existingUser");
        when(userRepoMock.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = controllerUnderTest.findById(1L);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }
}
