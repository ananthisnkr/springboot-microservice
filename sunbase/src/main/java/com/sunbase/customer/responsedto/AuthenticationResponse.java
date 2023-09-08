package com.sunbase.customer.responsedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String token;
}
