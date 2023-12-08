package com.cooksys.socialmedia.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserRequestDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.ProfileMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;
	private final HashtagRepository hashtagRepository;

	private final UserMapper userMapper;
	private final TweetMapper tweetMapper;
	private final HashtagMapper hashtagMapper;
	private final ProfileMapper profileMapper;
	private final CredentialsMapper credentialsMapper;

	@Override
	public List<UserResponseDto> getUsers() {
		return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
	}

	@Override
	public List<TweetResponseDto> getUserFeed(String username) {
		Optional<User> user = userRepository.findByCredentials_UsernameAndDeletedFalse(username);
		if (user.isEmpty()) {
			throw new NotFoundException("User not found");
		}
		List<Tweet> feed = tweetRepository.findByAuthorAndDeletedFalse(user.get());
		
		for (User following : user.get().getFollowers()) {
			for (Tweet tweet : following.getTweets()) {
				if (tweet.isDeleted() == false) {
					feed.add(tweet);					
				}
			}
		}
		List<Tweet> sortedFeed = feed.stream().sorted(Comparator.comparing(Tweet::getPosted).reversed())
				.collect(Collectors.toList());
		
		return tweetMapper.entitiesToDtos(sortedFeed);
	}

	@Override
	public void followUser(String username, CredentialsDto credentials) {
		if (credentials.getPassword() == null) {
			throw new BadRequestException("Invalid credentials");
		}
		User userToFollow = userRepository.findByCredentialsUsername(username);
		User userFollowing = userRepository.findByCredentialsUsername(credentials.getUsername());
		if (userToFollow == null || userFollowing == null) {
			throw new NotFoundException("User not found");
		}
		List<User> followers = userToFollow.getFollowers(); // followers of user to follow
		List<User> following = userFollowing.getFollowing(); // list of people the follower is following
		for (User user : followers) {
			if (user.equals(userFollowing)) {
				throw new BadRequestException("Already following user");
			}
		}

		followers.add(userFollowing);
		userToFollow.setFollowers(followers);
		following.add(userToFollow);
		userFollowing.setFollowing(following);
		userRepository.saveAndFlush(userToFollow);
		userRepository.saveAndFlush(userFollowing);
		
	}

	@Override
	public void unfollowUser(String username, CredentialsDto credentials) {
		if (credentials.getPassword() == null) {
			throw new BadRequestException("Invalid credentials");
		}
		User userToUnfollow = userRepository.findByCredentialsUsername(username);
		User userUnfollowing = userRepository.findByCredentialsUsername(credentials.getUsername());
		if (userToUnfollow == null || userUnfollowing == null) {
			throw new NotFoundException("User not found");
		}
		List<User> followers = userToUnfollow.getFollowers(); // followers of user to unfollow
		List<User> following = userUnfollowing.getFollowing(); // list of people the unfollower is following
		for (User user : followers) {
			if (user.equals(userUnfollowing)) {
				followers.remove(userUnfollowing);
				userToUnfollow.setFollowers(followers);
				following.remove(userToUnfollow);
				userUnfollowing.setFollowing(following);
				userRepository.saveAndFlush(userToUnfollow);
				userRepository.saveAndFlush(userUnfollowing);
				return;
			}
		}
		throw new BadRequestException("Not following user");
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		if (userRequestDto.getProfile() == null || userRequestDto.getCredentials() == null || userRequestDto.getCredentials().getPassword() == null
				|| userRequestDto.getCredentials().getUsername() == null
				|| userRequestDto.getProfile().getEmail() == null) {
			throw new BadRequestException("Invalid credentials or profile");
		}
		Optional<User> optionalUser = userRepository
				.findByCredentials(credentialsMapper.DtoToEntity(userRequestDto.getCredentials()));
		if (optionalUser.isEmpty()) {
			User newUser = userMapper.DtoToEntity(userRequestDto);
			newUser.setDeleted(false);
			return userMapper.entityToDto(userRepository.saveAndFlush(newUser));
		}
		if (optionalUser.get().isDeleted() == false) {
			throw new BadRequestException("That user already exists");
		} else {
			User reactivatedUser = optionalUser.get();
			reactivatedUser.setDeleted(false);
			return userMapper.entityToDto(userRepository.saveAndFlush(reactivatedUser));
		}
	}

	@Override
	public List<UserResponseDto> getFollowers(String username) {
		Optional<User> optionalUser = userRepository.findByCredentials_UsernameAndDeletedFalse(username);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("No active user with username: " + username);
		}
		List<User> followersToCheck = optionalUser.get().getFollowers();
		List<User> followers = new ArrayList<>();
		for (User f : followersToCheck) {
			if (f.isDeleted() == false) {
				followers.add(f);
			}
		}
		return userMapper.entitiesToDtos(followers);
	}

	@Override
	public List<UserResponseDto> getFollowing(String username) {
		Optional<User> optionalUser = userRepository.findByCredentials_UsernameAndDeletedFalse(username);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("No active user with username: " + username);
		}
		List<User> followingToCheck = optionalUser.get().getFollowing();
		List<User> following = new ArrayList<>();
		for (User f : followingToCheck) {
			if (f.isDeleted() == false) {
				following.add(f);
			}
		}
		return userMapper.entitiesToDtos(following);
	}

	@Override
	public List<TweetResponseDto> getMentions(String username) {
		Optional<User> optionalUser = userRepository.findByCredentials_UsernameAndDeletedFalse(username);
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("No active user with username: " + username);
		}
		List<Tweet> mentionsToCheck = optionalUser.get().getMentionedTweets();
		List<Tweet> mentions = new ArrayList<>();
		for (Tweet m : mentionsToCheck) {
			if (m.isDeleted() == false) {
				mentions.add(m);
			}
		}
		return tweetMapper.entitiesToDtos(mentions);
	}

	@Override
	public UserResponseDto getUserByUsername(String username) {
		Optional<User> getUser = userRepository.findByCredentials_UsernameAndDeletedFalse(username);
		if (getUser.isEmpty()) {
			throw new NotFoundException("User not found");
		}
		User userReturn = getUser.get();
		return userMapper.entityToDto(userReturn);
	}

	@Override
	public UserResponseDto updateUsername(String username, UserRequestDto credentials) {
		if (credentials.getCredentials() == null) {
			throw new NotFoundException("User not found");
		}
		User toUpdate = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(
				credentials.getCredentials().getUsername(), credentials.getCredentials().getPassword());
		if (toUpdate == null) {
			throw new NotFoundException("User not found");
		}
		if (credentials.getProfile() == null) {
			throw new NotFoundException("no profile provided");
		}
		if (credentials.getProfile().getEmail() == null) {
			userRepository.save(toUpdate);
			return userMapper.entityToDto(toUpdate);
		} else {
			toUpdate.setProfile(profileMapper.DtoToEntity(credentials.getProfile()));
		}
		userRepository.save(toUpdate);
		return userMapper.entityToDto(toUpdate);
	}

	@Override
	public UserResponseDto deleteUser(String username, CredentialsDto credentials) {
		User userToDelete = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(
				credentials.getUsername(), credentials.getPassword());
		if (userToDelete == null) {
			throw new NotFoundException("User not found");
		}
		userToDelete.setDeleted(true);
		userRepository.saveAndFlush(userToDelete);
		return userMapper.entityToDto(userToDelete);

	}

	@Override
	public List<TweetResponseDto> getAllTweetsByUser(String username) {
		User user = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (user == null) {
			throw new NotFoundException("User not found");
		}
		List<Tweet> userTweets = tweetRepository.findByAuthorAndDeletedFalseOrderByPostedDesc(user);
		userTweets.removeIf(Tweet::isDeleted);
		return tweetMapper.entitiesToDtos(userTweets);
	}
}
