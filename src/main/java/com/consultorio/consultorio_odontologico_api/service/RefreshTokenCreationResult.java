package com.consultorio.consultorio_odontologico_api.service;

import java.util.UUID;

public record RefreshTokenCreationResult(
        String token,
        UUID tokenId
) {
}
