package com.cooksys.socialmedia.services;

public interface ValidateService {

	boolean validateTagExists(String label);

	boolean validateUsernameAvailable(String username);

	boolean validateUsernameExists(String username);

}
