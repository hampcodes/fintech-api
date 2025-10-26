package com.fintech.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Role ROLE_USER not found"));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);

        // Crear Customer asociado al User con todos los datos
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setDni(request.dni());
        customer.setAddress(request.address());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setNationality(request.nationality());
        customer.setOccupation(request.occupation());
        Customer savedCustomer = customerRepository.save(customer);

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedCustomer.getName(), savedCustomer.getId());

        return new AuthResponse(token, savedUser.getEmail(), request.name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found for user"));

        String token = jwtUtil.generateToken(user.getEmail(), customer.getName(), customer.getId());

        return new AuthResponse(token, user.getEmail(), customer.getName());
    }
}
