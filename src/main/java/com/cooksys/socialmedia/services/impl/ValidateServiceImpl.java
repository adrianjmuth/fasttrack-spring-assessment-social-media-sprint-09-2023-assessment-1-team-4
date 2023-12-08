package com.cooksys.socialmedia.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.ProfileMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;
	private final HashtagRepository hashtagRepository;

	private final UserMapper userMapper;
	private final TweetMapper tweetMapper;
	private final HashtagMapper hashtagMapper;
	private final ProfileMapper profileMapper;
	private final CredentialsMapper credentialsMapper;

	@Override
	public boolean validateTagExists(String label) {
		Optional<Hashtag> optionalTags = hashtagRepository.findByLabel("#" + label);
		Optional<Hashtag> optionalTagNoHash = hashtagRepository.findByLabel(label);
		if (optionalTags.isEmpty() && optionalTagNoHash.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public boolean validateUsernameAvailable(String username) {
		User user = userRepository.findByCredentialsUsername(username);
		if (user == null) {
			return true;
		}
		return false;
	}

	public boolean validateUsernameExists(String username) {
		List<User> users = userRepository.findAllByDeletedFalse();
		for (User user : users) {
			if (user.getCredentials().getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

}
