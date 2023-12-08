package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;

public interface UserService {

	List<UserResponseDto> getUsers();

	UserResponseDto getUserByUsername(String username);

	List<TweetResponseDto> getUserFeed(String username);

	List<TweetResponseDto> getAllTweetsByUser(String username);

	void followUser(String username, CredentialsDto credentials);

	void unfollowUser(String username, CredentialsDto credentials);

	UserResponseDto createUser(UserRequestDto userRequestDto);

	List<UserResponseDto> getFollowers(String username);

	List<UserResponseDto> getFollowing(String username);

	List<TweetResponseDto> getMentions(String username);

	UserResponseDto updateUsername(String username, UserRequestDto credentials);

	UserResponseDto deleteUser(String username, CredentialsDto credentials);

}
