package com.fintech.config;

import com.fintech.model.Customer;
import com.fintech.model.Role;
import com.fintech.model.RoleType;
import com.fintech.model.User;
import com.fintech.repository.CustomerRepository;
import com.fintech.repository.RoleRepository;
import com.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with default data...");

        // Crear roles
        Role userRole = createRoleIfNotExists(RoleType.ROLE_USER);
        Role adminRole = createRoleIfNotExists(RoleType.ROLE_ADMIN);

        // Crear usuario admin por defecto
        createAdminUserIfNotExists(adminRole);

        log.info("Database initialization completed.");
    }

    private Role createRoleIfNotExists(RoleType roleType) {
        return roleRepository.findByName(roleType)
                .orElseGet(() -> {
                    Role role = new Role(roleType);
                    roleRepository.save(role);
                    log.info("Created role: {}", roleType);
                    return role;
                });
    }

    private void createAdminUserIfNotExists(Role adminRole) {
        String adminEmail = "admin@fintech.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists: {}", adminEmail);
            return;
        }

        // Crear User admin
        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRole(adminRole);
        adminUser.setActive(true);
        User savedAdmin = userRepository.save(adminUser);

        // Crear Customer asociado al admin
        Customer adminCustomer = new Customer();
        adminCustomer.setUser(savedAdmin);
        adminCustomer.setName("System Administrator");
        adminCustomer.setPhone("+1-800-FINTECH");
        adminCustomer.setNationality("System");
        adminCustomer.setOccupation("Administrator");
        customerRepository.save(adminCustomer);

        log.info("========================================");
        log.info("DEFAULT ADMIN USER CREATED:");
        log.info("Email: {}", adminEmail);
        log.info("Password: admin123");
        log.info("⚠️  CHANGE THIS PASSWORD IN PRODUCTION!");
        log.info("========================================");
    }
}
