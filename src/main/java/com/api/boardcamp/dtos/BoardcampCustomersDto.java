package com.api.boardcamp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BoardcampCustomersDto(Long id,
        @NotBlank String name, @NotBlank String phone, @NotNull String cpf) {

}
