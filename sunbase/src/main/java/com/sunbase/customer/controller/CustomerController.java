package com.sunbase.customer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sunbase.customer.model.Customer;
import com.sunbase.customer.requestdto.AuthenticationRequest;
import com.sunbase.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class CustomerController {

    @Autowired
    public CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody AuthenticationRequest authRequest) {

       String bearerToken = customerService.authenticateUser(authRequest);

        if(bearerToken != null){
            return ResponseEntity.ok(bearerToken);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Authorization");
        }


    }

    @PostMapping("/create")
    public ResponseEntity<String> createCustomer(@RequestBody Customer customer){
        String responseCode = customerService.createCustomer(customer);
        System.out.println(responseCode +" from service");
        if(responseCode.equals("Successfully Created")){
            return ResponseEntity.status(HttpStatus.CREATED).body("Customer data updated successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(("First Name or Last Name is missing"));
        }
    }

    @GetMapping("/customerList")
    public ResponseEntity<Customer[]> getCustomerList(){
        Customer[] customerList = customerService.getCustomerList();
       if(customerList!=null){
            return ResponseEntity.status(HttpStatus.OK).body(customerList);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteCustomer(@RequestParam String uuid){
        String response = customerService.deleteCustomer(uuid);
       if(response != null) {
           return ResponseEntity.status(HttpStatus.OK).body("Deleted Successfully");
       }
       else{
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not deleted");
       }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateCustomer(@RequestParam String uuid ,@RequestBody Customer customer){
        String response = customerService.updateCustomer(uuid,customer);
        if(response != null) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not deleted");
        }
    }

}
