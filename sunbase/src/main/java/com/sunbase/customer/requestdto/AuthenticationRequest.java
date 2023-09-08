package com.sunbase.customer.requestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthenticationRequest {
    @JsonProperty("login_id")
    private String loginId;
    private String password;
}
