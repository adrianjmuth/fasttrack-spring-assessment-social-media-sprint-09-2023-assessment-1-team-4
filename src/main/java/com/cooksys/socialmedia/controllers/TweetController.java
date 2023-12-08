package com.cooksys.socialmedia.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

	private final TweetService tweetService;

	@GetMapping("{id}/context")
	ContextDto getContextOfTweet(@PathVariable Long id) {
		return tweetService.getContextOfTweet(id);
	}

	@GetMapping("/{id}")
	public TweetResponseDto getTweet(@PathVariable Long id) {
		return tweetService.getTweet(id);
	}

	@GetMapping
	public List<TweetResponseDto> getTweets() {
		return tweetService.getTweets();
	}

	@GetMapping("/{id}/reposts")
	public List<TweetResponseDto> getTweetReposts(@PathVariable Long id) {
		return tweetService.getTweetReposts(id);
	}

	@GetMapping("/{id}/mentions")
	public List<UserResponseDto> getTweetMentions(@PathVariable Long id) {
		return tweetService.getTweetMentions(id);
	}

	@GetMapping("{id}/likes")
	public List<UserResponseDto> usersWhoLikedTweet(@PathVariable Long id) {
		return tweetService.usersWhoLikedTweet(id);
	}

	@GetMapping("{id}/replies")
	public List<TweetResponseDto> userRepliesToTweet(@PathVariable Long id) {
		return tweetService.userRepliesToTweet(id);
	}

	@GetMapping("{id}/tags")
	public List<HashtagResponseDto> tagsAssociatedWithTweet(@PathVariable Long id) {
		return tweetService.tagsAssociatedWithTweet(id);
	}

	@PostMapping("{id}/repost")
	public TweetResponseDto createRepostOfTweet(@PathVariable Long id, @RequestBody CredentialsDto creds) {
		return tweetService.createRepostOfTweet(id, creds);
	}

	@DeleteMapping("/{id}")
	public TweetResponseDto deleteTweet(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
		return tweetService.deleteTweet(id, credentialsDto);
	}

	@PostMapping("{id}/reply")
	public TweetResponseDto createTweetReply(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
		return tweetService.createTweetReply(id, tweetRequestDto);

	}

	@PostMapping("/{id}/like")
	public void likeTweet(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
		tweetService.likeTweet(id, credentialsDto);
	}

	@PostMapping
	public TweetResponseDto createTweet(@RequestBody TweetRequestDto tweetRequestDto) {
		return tweetService.createTweet(tweetRequestDto);
	}

}
