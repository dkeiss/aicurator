package de.dkeiss.taxishare.service.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank String username,
		@NotBlank String password
) {}