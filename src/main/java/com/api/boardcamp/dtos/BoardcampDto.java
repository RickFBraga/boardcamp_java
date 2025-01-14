package com.api.boardcamp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BoardcampDto(Long id,
    @NotBlank String name, @NotBlank String image, @NotNull Integer stockTotal, @NotNull Long pricePerDay) {

}
