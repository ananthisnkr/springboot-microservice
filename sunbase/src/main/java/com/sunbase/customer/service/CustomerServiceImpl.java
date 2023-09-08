package com.sunbase.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbase.customer.model.Customer;
import com.sunbase.customer.requestdto.AuthenticationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class CustomerServiceImpl implements CustomerService {


    @Value("${authentication.api.url}")
    private String baseUrl;

    private String bearerToken;

    @Override
    public String authenticateUser(AuthenticationRequest authRequest) {

        String authenticationApiUrl = baseUrl + "assignment_auth.jsp";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String reqJson = null;
        try {
            reqJson = objectMapper.writeValueAsString(authRequest);


            HttpEntity<String> entity = new HttpEntity<>(reqJson, headers);


            ResponseEntity<String> response = new RestTemplate().exchange(
                    authenticationApiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();


                ObjectMapper responseMapper = new ObjectMapper();
                JsonNode jsonNode = responseMapper.readTree(responseBody);
                bearerToken = jsonNode.get("access_token").asText();
                return bearerToken;

            } else {
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    @Override
    public String createCustomer(Customer customer) {
        try {
            String urlpath = baseUrl + "assignment.jsp";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlpath)
                    .queryParam("cmd", "create");

            String createApiUrl = builder.toUriString();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + bearerToken);

            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectMapper objectMapper = new ObjectMapper();
            String reqbody = objectMapper.writeValueAsString(customer);

            HttpEntity<String> entity = new HttpEntity<>(reqbody, headers);

            ResponseEntity<String> response = new RestTemplate().exchange(
                    createApiUrl, HttpMethod.POST, entity, String.class);

            System.out.println(response.getStatusCode() + "in service impl ");
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return "Successfully Created";
            } else {
                return "Failed to create customer. Status code: " + response.getStatusCodeValue();

            }
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                if (ex.getResponseBodyAsString().contains("First Name or Last Name is missing")) {
                    return "Failed to create customer. First Name or Last Name is missing.";
                } else {
                    return "Failed to create customer. Unknown error.";
                }
            } else {
                return "Failed to create customer. Status code: " + ex.getStatusCode().value();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

    }

    public Customer[] getCustomerList() {

        try {
            String urlpath = baseUrl + "assignment.jsp";
            UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(urlpath)
                    .queryParam("cmd", "get_customer_list");

            String apiUrl = uri.toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + bearerToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = new RestTemplate().exchange(apiUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                // Process the JSON response data
                System.out.println("Response: " + responseBody);

                ObjectMapper objectMapper = new ObjectMapper();
                Customer[] customers = objectMapper.readValue(responseBody, Customer[].class);
                for (Customer customer : customers) {
                    System.out.println("Customer: " + customer.getFirstName() + " " + customer.getLastName());
                }
                return customers;
            } else {
                System.err.println("Failure: " + response.getStatusCodeValue() + ", " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to fetch customer list. Exception: " + e.getMessage());
            return null;
        }


    }

    public String deleteCustomer(String uuid) {
        try {
            String urlPath = baseUrl + "assignment.jsp";
            UriComponentsBuilder uribuilder = UriComponentsBuilder.fromHttpUrl(urlPath)
                    .queryParam("cmd", "delete").queryParam("uuid", uuid);
            String apiUrl = uribuilder.toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + bearerToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = new RestTemplate().exchange(
                    apiUrl, HttpMethod.POST, entity, String.class);

            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                return "Successfully deleted";
            } else if (statusCode == HttpStatus.BAD_REQUEST) {
                return "UUID not found";
            } else {
                return "Error Not deleted";
            }

        } catch (Exception e) {
            return "Error Not deleted";
        }
    }


    public String updateCustomer(String uuid, Customer customer) {
        try {
            String urlPath = baseUrl + "assignment.jsp";
            UriComponentsBuilder uribuilder = UriComponentsBuilder.fromHttpUrl(urlPath)
                    .queryParam("cmd", "update").queryParam("uuid", uuid);
            String apiUrl = uribuilder.toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + bearerToken);

            ObjectMapper objectMapper = new ObjectMapper();
            String reqbody = objectMapper.writeValueAsString(customer);

            HttpEntity<String> entity = new HttpEntity<>(reqbody, headers);

            ResponseEntity<String> responseEntity = new RestTemplate().exchange(
                    apiUrl, HttpMethod.POST, entity, String.class);

            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                return "Successfully updated";
            } else if (statusCode == HttpStatus.BAD_REQUEST) {
                return "UUID not found";
            } else {
                return "Error Not Updated";
            }

        } catch (Exception e) {
            return "Error Not updated";
        }
    }

}
