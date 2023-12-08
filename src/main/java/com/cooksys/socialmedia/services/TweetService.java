package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;

public interface TweetService {
	ContextDto getContextOfTweet(Long id);

	TweetResponseDto getTweet(Long id);

	List<TweetResponseDto> getTweets();

	TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto);

	void likeTweet(Long id, CredentialsDto credentialsDto);

	TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

	List<UserResponseDto> usersWhoLikedTweet(Long id);

	List<TweetResponseDto> userRepliesToTweet(Long id);

	List<HashtagResponseDto> tagsAssociatedWithTweet(Long id);

	TweetResponseDto createRepostOfTweet(Long id, CredentialsDto creds);

	List<TweetResponseDto> getTweetReposts(Long id);

	List<UserResponseDto> getTweetMentions(Long id);

	TweetResponseDto createTweetReply(Long id, TweetRequestDto tweetRequestDto);
}
