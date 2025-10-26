package com.fintech.service;

import com.fintech.dto.request.CustomerRequest;
import com.fintech.dto.request.UpdateKycRequest;
import com.fintech.dto.response.CustomerResponse;
import com.fintech.model.Customer;
import com.fintech.model.User;
import com.fintech.repository.CustomerRepository;
import com.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    // ==================== USER METHODS ====================

    @Transactional(readOnly = true)
    public CustomerResponse getMyProfile() {
        Customer customer = getAuthenticatedCustomer();
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse updateMyProfile(CustomerRequest request) {
        Customer customer = getAuthenticatedCustomer();

        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setDni(request.dni());
        customer.setAddress(request.address());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setNationality(request.nationality());
        customer.setOccupation(request.occupation());

        return mapToResponse(customerRepository.save(customer));
    }

    // ==================== ADMIN METHODS ====================

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomersAdmin() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByIdAdmin(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse updateKycStatusAdmin(String id, UpdateKycRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setKycStatus(request.kycStatus());
        customer.setKycDocuments(request.kycDocuments());

        return mapToResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse activateCustomerAdmin(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setActive(true);
        return mapToResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse deactivateCustomerAdmin(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setActive(false);
        return mapToResponse(customerRepository.save(customer));
    }

    // ==================== HELPER METHODS ====================

    private Customer getAuthenticatedCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));
        return customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found for user"));
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getUser().getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getDni(),
                customer.getAddress(),
                customer.getDateOfBirth(),
                customer.getNationality(),
                customer.getOccupation(),
                customer.getKycStatus(),
                customer.getKycDocuments(),
                customer.getActive(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
