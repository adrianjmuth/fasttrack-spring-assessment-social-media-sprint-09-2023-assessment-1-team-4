package com.cooksys.socialmedia.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {

	private final ValidateService validateService;

	@GetMapping("/tag/exists/{label}")
	public boolean validateTagExists(@PathVariable String label) {
		return validateService.validateTagExists(label);
	}

	@GetMapping("/username/available/@{username}")
	public boolean validateUsernameAvailable(@PathVariable String username) {
		return validateService.validateUsernameAvailable(username);
	}

	@GetMapping("/username/exists/@{username}")
	public boolean validateUsernameExists(@PathVariable String username) {
		return validateService.validateUsernameExists(username);
	}

}
