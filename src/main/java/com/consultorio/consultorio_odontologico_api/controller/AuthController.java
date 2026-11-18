package com.consultorio.consultorio_odontologico_api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.consultorio.consultorio_odontologico_api.dto.request.LoginRequest;
import com.consultorio.consultorio_odontologico_api.dto.response.AuthResult;
import com.consultorio.consultorio_odontologico_api.dto.response.LoginResponse;
import com.consultorio.consultorio_odontologico_api.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        AuthResult authResult = authService.login(request);
        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", authResult.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(86400)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authResult.getLoginResponse());
    }
}
