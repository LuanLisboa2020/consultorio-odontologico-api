package com.consultorio.consultorio_odontologico_api.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.consultorio.consultorio_odontologico_api.dto.response.RefreshTokenResult;
import com.consultorio.consultorio_odontologico_api.entity.RefreshToken;
import com.consultorio.consultorio_odontologico_api.entity.Usuario;
import com.consultorio.consultorio_odontologico_api.exception.RefreshTokenReuseException;
import com.consultorio.consultorio_odontologico_api.repository.RefreshTokenRepository;
import com.consultorio.consultorio_odontologico_api.security.JwtService;
import com.consultorio.consultorio_odontologico_api.service.RefreshTokenCreationResult;
import com.consultorio.consultorio_odontologico_api.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final long refreshTokenExpiration;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            @Value("${api.security.jwt.refresh-token-expiration}") long refreshTokenExpiration) {

        if (refreshTokenExpiration <= 0) {
            throw new IllegalArgumentException("A validade do Refresh Token deve ser maior que zero.");
        }

        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    @Transactional
    public RefreshTokenCreationResult criarRefreshToken(
            Usuario usuario,
            UUID sessionId) {

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUsuario(usuario);
        refreshToken.setTokenHash(gerarHash(token));
        refreshToken.setSessionId(sessionId);
        refreshToken.setDataExpiracao(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)));

        RefreshToken tokenSalvo = refreshTokenRepository.save(refreshToken);

        return new RefreshTokenCreationResult(
                token,
                tokenSalvo.getId()
        );
    }

    @Override
    @Transactional(noRollbackFor = RefreshTokenReuseException.class)
    public RefreshTokenResult renovarToken(String token) {

        if (token == null || token.isBlank()) {
            throw new BadCredentialsException("Refresh Token inválido.");
        }

        String tokenHash = gerarHash(token);

        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Refresh Token inválido."));

        LocalDateTime agora = LocalDateTime.now();

        // Verifica se o token já foi utilizado e revogado.
        if (refreshToken.getDataRevogacao() != null) {

            revogarSessao(
                    refreshToken.getUsuario().getId(),
                    refreshToken.getSessionId()
            );
            throw new RefreshTokenReuseException();
        }

        // Verifica se o Refresh Token expirou.
        if (!refreshToken.getDataExpiracao().isAfter(agora)) {
            throw new BadCredentialsException("Refresh Token inválido ou expirado.");
        }

        Usuario usuario = refreshToken.getUsuario();

        // Verifica se o administrador continua ativo.
        if (!usuario.isAtivo()) {
            throw new BadCredentialsException("A conta deste administrador está inativa.");
        }

        // Revoga o token anterior.
        refreshToken.setDataRevogacao(agora);

        // Cria o próximo Refresh Token.
        RefreshTokenCreationResult novoRefreshToken = criarRefreshToken(usuario, refreshToken.getSessionId());

        // Registra qual token substituiu o anterior.
        refreshToken.setReplacedByTokenId(novoRefreshToken.tokenId());

        // Gera um novo Access Token.
        String novoAccessToken = jwtService.gerarAccessToken(usuario.getId());

        return new RefreshTokenResult(
                novoAccessToken,
                novoRefreshToken.token(),
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    @Override
    @Transactional
    public Optional<RefreshToken> buscarPorToken(String token) {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String tokenHash = gerarHash(token);

        return refreshTokenRepository.findByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public void revogarToken(RefreshToken refreshToken) {

        if (refreshToken == null) {
            return;
        }

        if (refreshToken.getDataRevogacao() != null) {
            return;
        }

        refreshToken.setDataRevogacao(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public void revogarSessao(UUID usuarioId, UUID sessionId) {

        if (usuarioId == null || sessionId == null) {
            throw new IllegalArgumentException("O usuário e a sessão são obrigatórios.");
        }

        refreshTokenRepository.revogarTodosDaSessao(
                usuarioId,
                sessionId,
                LocalDateTime.now()
        );
    }

    private String gerarHash(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Não foi possível gerar o hash do Refresh Token.", e);
        }
    }
}
