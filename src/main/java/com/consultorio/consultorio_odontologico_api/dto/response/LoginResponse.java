package com.consultorio.consultorio_odontologico_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private long expiresIn;
}
