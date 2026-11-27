package com.consultorio.consultorio_odontologico_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenResult {

    private final String accessToken;

    private final String refreshToken;

    private final long expiresIn;
}
