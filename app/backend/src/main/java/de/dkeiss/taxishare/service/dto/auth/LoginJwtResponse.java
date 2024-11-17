package de.dkeiss.taxishare.service.dto.auth;

import java.util.List;

public record LoginJwtResponse(
		String accessToken,
		String type,
		Long id,
		String username,
		String email,
		List<String> roles
) {}