package com.consultorio.consultorio_odontologico_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "O usuário é obrigatório.")
    @Size(min = 4, max = 50, message = "O usuário deve ter entre 4 e 50 caracteres.")
    private String usuario;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    private String senha;
}
