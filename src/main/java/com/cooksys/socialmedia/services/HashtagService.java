package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;

import java.util.List;

public interface HashtagService {
	List<HashtagResponseDto> getAllHashtags();

	List<TweetResponseDto> getTweetsByLabel(String label);
}
