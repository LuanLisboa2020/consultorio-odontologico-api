package com.consultorio.consultorio_odontologico_api.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.consultorio.consultorio_odontologico_api.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            UPDATE RefreshToken rt
            SET rt.dataRevogacao = :dataRevogacao
            WHERE rt.usuario.id = :usuarioId
            AND rt.sessionId = :sessionId
            AND rt.dataRevogacao IS NULL
            """)
    int revogarTodosDaSessao(
            @Param("usuarioId") UUID usuarioId,
            @Param("sessionId") UUID sessionId,
            @Param("dataRevogacao") LocalDateTime dataRevogacao
    );
}
