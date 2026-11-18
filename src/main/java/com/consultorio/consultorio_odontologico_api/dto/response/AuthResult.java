package com.consultorio.consultorio_odontologico_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResult {

    private LoginResponse loginResponse;

    private String refreshToken;
}
