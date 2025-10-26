package com.fintech.service;

import com.fintech.dto.request.UpdateUserRoleRequest;
import com.fintech.dto.response.UserResponse;
import com.fintech.exception.RoleNotFoundException;
import com.fintech.model.Customer;
import com.fintech.model.Role;
import com.fintech.model.User;
import com.fintech.repository.CustomerRepository;
import com.fintech.repository.RoleRepository;
import com.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;

    // ==================== ADMIN METHODS ====================

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersAdmin() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByIdAdmin(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse activateUserAdmin(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
        user.setActive(true);
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse deactivateUserAdmin(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
        user.setActive(false);
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUserRoleAdmin(String id, UpdateUserRoleRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + request.role()));

        user.setRole(role);
        return mapToResponse(userRepository.save(user));
    }

    private UserResponse mapToResponse(User user) {
        // Obtener el nombre del Customer asociado
        Customer customer = customerRepository.findByUserId(user.getId())
                .orElse(null);

        String name = (customer != null) ? customer.getName() : "N/A";

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                name,
                user.getRole().getName(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
