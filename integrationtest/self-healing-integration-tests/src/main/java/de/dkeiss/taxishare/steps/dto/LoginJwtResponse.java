package de.dkeiss.taxishare.steps.dto;

import java.util.List;

public record LoginJwtResponse(
		String accessToken,
		String type,
		Long id,
		String username,
		String email,
		List<String> roles
) {}