package com.fintech.unit;

import com.fintech.dto.request.LoginRequest;
import com.fintech.dto.request.RegisterRequest;
import com.fintech.dto.response.AuthResponse;
import com.fintech.exception.DuplicateEmailException;
import com.fintech.exception.RoleNotFoundException;
import com.fintech.model.Customer;
import com.fintech.model.Role;
import com.fintech.model.RoleType;
import com.fintech.model.User;
import com.fintech.repository.CustomerRepository;
import com.fintech.repository.RoleRepository;
import com.fintech.repository.UserRepository;
import com.fintech.security.JwtUtil;
import com.fintech.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas Unitarias")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleType.ROLE_USER);
    }

    private User createMockUser(String id, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(userRole);
        return user;
    }

    private Customer createMockCustomer(String id, User user, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setUser(user);
        customer.setName(name);
        return customer;
    }

    @Test
    @DisplayName("Debe registrar usuario exitosamente")
    void register_ValidData_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "john@example.com",
                "password123",
                "John Doe",
                "+1234567890",
                "12345678A",
                "123 Main St",
                null,
                "USA",
                "Engineer"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        User savedUser = createMockUser("user-001", "john@example.com", "encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Customer savedCustomer = createMockCustomer("customer-001", savedUser, "John Doe");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        when(jwtUtil.generateToken("john@example.com", "John Doe", "customer-001")).thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.name()).isEqualTo("John Doe");

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(any(User.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email ya existe")
    void register_DuplicateEmail_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "john@example.com",
                "password123",
                "John Doe",
                null, null, null, null, null, null
        );
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no existe el rol USER")
    void register_RoleNotFound_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "john@example.com",
                "password123",
                "John Doe",
                null, null, null, null, null, null
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Debe hacer login exitosamente")
    void login_ValidCredentials_Success() {
        // Arrange
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        User user = createMockUser("user-001", "john@example.com", "encodedPassword");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        Customer customer = createMockCustomer("customer-001", user, "John Doe");
        when(customerRepository.findByUserId("user-001")).thenReturn(Optional.of(customer));

        when(jwtUtil.generateToken("john@example.com", "John Doe", "customer-001")).thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.name()).isEqualTo("John Doe");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
