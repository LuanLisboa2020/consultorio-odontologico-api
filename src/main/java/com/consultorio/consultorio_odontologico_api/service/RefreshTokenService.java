package com.consultorio.consultorio_odontologico_api.service;

import java.util.Optional;
import java.util.UUID;

import com.consultorio.consultorio_odontologico_api.dto.response.RefreshTokenResult;
import com.consultorio.consultorio_odontologico_api.entity.RefreshToken;
import com.consultorio.consultorio_odontologico_api.entity.Usuario;

public interface RefreshTokenService {

    RefreshTokenCreationResult criarRefreshToken(
            Usuario usuario,
            UUID sessionId
    );

    RefreshTokenResult renovarToken(String token);

    Optional<RefreshToken> buscarPorToken(String token);

    void revogarToken(RefreshToken refreshToken);

    void revogarSessao(UUID usuarioId, UUID sessionId);
}
