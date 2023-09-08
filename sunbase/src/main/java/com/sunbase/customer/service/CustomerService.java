package com.sunbase.customer.service;

import com.sunbase.customer.model.Customer;
import com.sunbase.customer.requestdto.AuthenticationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CustomerService {

    public String authenticateUser(AuthenticationRequest authRequest);
    public String createCustomer(Customer customer);

    public Customer[] getCustomerList();

    public String deleteCustomer(String uuid);
    public String updateCustomer(String uuid,Customer customer);
    }
