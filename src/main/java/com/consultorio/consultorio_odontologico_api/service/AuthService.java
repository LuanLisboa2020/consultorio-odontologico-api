package com.consultorio.consultorio_odontologico_api.service;

import com.consultorio.consultorio_odontologico_api.dto.request.LoginRequest;
import com.consultorio.consultorio_odontologico_api.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}
