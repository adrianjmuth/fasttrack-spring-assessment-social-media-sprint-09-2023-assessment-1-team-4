package com.cooksys.socialmedia.services.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.ProfileMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;
	private final HashtagRepository hashtagRepository;

	private final UserMapper userMapper;
	private final TweetMapper tweetMapper;
	private final HashtagMapper hashtagMapper;
	private final ProfileMapper profileMapper;
	private final CredentialsMapper credentialsMapper;

	@Override
	public List<HashtagResponseDto> getAllHashtags() {
		return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
	}

	@Override
	public List<TweetResponseDto> getTweetsByLabel(String label) {
		Optional<Hashtag> tag = hashtagRepository.findByLabel(label);
		if (tag.isEmpty()) {
			throw new NotFoundException("No hashtag with that label exists");
		}
		List<Tweet> allTweets = tag.get().getTaggedTweets();

		List<Tweet> sortedTweets = allTweets.stream().sorted(Comparator.comparing(Tweet::getPosted).reversed())
				.collect(Collectors.toList());

		return tweetMapper.entitiesToDtos(sortedTweets);
	}

}
