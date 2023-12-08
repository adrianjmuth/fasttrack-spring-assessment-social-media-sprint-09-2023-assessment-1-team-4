package com.cooksys.socialmedia.services.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.dtos.TweetRequestDto;
import com.cooksys.socialmedia.dtos.TweetResponseDto;
import com.cooksys.socialmedia.dtos.UserResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.CredentialsMapper;
import com.cooksys.socialmedia.mappers.HashtagMapper;
import com.cooksys.socialmedia.mappers.ProfileMapper;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final UserRepository userRepository;
	private final TweetRepository tweetRepository;
	private final HashtagRepository hashtagRepository;

	private final UserMapper userMapper;
	private final TweetMapper tweetMapper;
	private final HashtagMapper hashtagMapper;
	private final ProfileMapper profileMapper;
	private final CredentialsMapper credentialsMapper;

	@Override
	public ContextDto getContextOfTweet(Long id) {
		Optional<Tweet> checker = tweetRepository.findByIdAndDeletedFalse(id);
		if (checker.isEmpty()) {
			throw new BadRequestException("no tweet with this id!");
		}
		ContextDto context = new ContextDto();
		Tweet targetTweet = tweetRepository.getReferenceById(id);
		List<Tweet> before = new ArrayList<>();
		List<Tweet> after = tweetRepository.getReferenceById(id).getReplies();
		Long currentId = id;
		while (true) { // before
			Tweet thisTweet = tweetRepository.getReferenceById(currentId);
			if (thisTweet.getInReplyTo() != null) {
				before.add(thisTweet.getInReplyTo());
				currentId = thisTweet.getInReplyTo().getId();
			} else {
				break;
			}
		}
		List<Tweet> replies = after;
		while (true) {
			List<Tweet> repliesToReplies = new ArrayList<>();
			for (Tweet reply : replies) {
				repliesToReplies.addAll(reply.getReplies());
			}
			after.addAll(repliesToReplies);
			replies = repliesToReplies;
			if (replies.isEmpty()) {
				break;
			}
		}
		List<Tweet> sortedBefore = before.stream().sorted(Comparator.comparing(Tweet::getPosted).reversed()).toList();
		List<Tweet> sortedAfter = after.stream().sorted(Comparator.comparing(Tweet::getPosted).reversed()).toList();
		context.setTarget(tweetMapper.entityToDto(targetTweet));
		context.setBefore(tweetMapper.entitiesToDtos(sortedBefore));
		context.setAfter(tweetMapper.entitiesToDtos(sortedAfter));
		return context;
	}

	@Override
	public TweetResponseDto getTweet(Long id) {
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("No tweet found with id: " + id);
		}
		return tweetMapper.entityToDto(optionalTweet.get());
	}

	@Override
	public List<TweetResponseDto> getTweets() {
		return tweetMapper.entitiesToDtos(tweetRepository.findAllByDeletedFalse());
	}

	@Override
	public List<UserResponseDto> usersWhoLikedTweet(Long id) {
		Tweet thisTweet = tweetRepository.getReferenceById(id);
		List<User> notDeleted = tweetRepository.getReferenceById(id).getUsersWhoLiked();
		if (thisTweet.isDeleted()) {
			throw new NotFoundException("Tweet not found");
		}
		notDeleted.removeIf(User::isDeleted);
		return userMapper.entitiesToDtos(notDeleted);
	}

	@Override
	public List<TweetResponseDto> userRepliesToTweet(Long id) {
		Tweet thisTweet = tweetRepository.getReferenceById(id);
		if (thisTweet.isDeleted()) {
			throw new NotFoundException("Tweet has been deleted");
		}
		List<Tweet> notDeleted = tweetRepository.getReferenceById(id).getReplies();
		notDeleted.removeIf(Tweet::isDeleted);
		return tweetMapper.entitiesToDtos(notDeleted);
	}

	@Override
	public List<HashtagResponseDto> tagsAssociatedWithTweet(Long id) {
		if (tweetRepository.getReferenceById(id).isDeleted()) {
			throw new NotFoundException("Tweet not found");
		}
		List<Hashtag> tags = tweetRepository.getReferenceById(id).getHashtags();
		List<Hashtag> fixedTags = new ArrayList<>();
		for (Hashtag t : tags) {
			String label = t.getLabel();
			String newLabel = label.replace("#", "");
			t.setLabel(newLabel);
			hashtagRepository.saveAndFlush(t);
			fixedTags.add(t);
		}
		return hashtagMapper.entitiesToDtos(fixedTags);
	}

	@Override
	public TweetResponseDto createRepostOfTweet(Long id, CredentialsDto creds) {
		Optional<Tweet> tweetToReturnOptional = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToReturnOptional.isEmpty()) {
			throw new NotFoundException("Tweet doesn't exist");
		}
		Tweet tweetToRepost = new Tweet();
		User newAuthor = userRepository.findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(
				creds.getUsername(), creds.getPassword());
		tweetToRepost.setAuthor(newAuthor);
		tweetToRepost.setContent(tweetRepository.getReferenceById(id).getContent());
		tweetToRepost.setRepostOf(tweetRepository.getReferenceById(id));
		tweetRepository.save(tweetToRepost);
		return tweetMapper.entityToDto(tweetToRepost);
	}

	@Override
	public TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto) {
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("No tweet found with id: " + id);
		}
		Tweet tweetToDelete = optionalTweet.get();
		if (tweetToDelete.getAuthor().getCredentials().equals(credentialsMapper.DtoToEntity(credentialsDto))) {
			tweetToDelete.setDeleted(true);
			return tweetMapper.entityToDto(tweetRepository.saveAndFlush(tweetToDelete));
		}
		throw new NotAuthorizedException("Incorrect credentials");

	}

	@Override
	public void likeTweet(Long id, CredentialsDto credentialsDto) {
		Optional<Tweet> optionalTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("No tweet found with id: " + id);
		}
		Optional<User> optionalUser = userRepository
				.findByCredentialsAndDeletedFalse(credentialsMapper.DtoToEntity(credentialsDto));
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("No active user with these credentials");
		}

		Tweet tweet = optionalTweet.get();
		User user = optionalUser.get();

		if (user.getLikedTweets().contains(tweet)) {
			return;
		}

		user.getLikedTweets().add(tweet);
		userRepository.saveAndFlush(user);
		tweet.getUsersWhoLiked().add(user);
		tweetRepository.saveAndFlush(tweet);
	}

	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
		Optional<User> optionalUser = userRepository
				.findByCredentialsAndDeletedFalse(credentialsMapper.DtoToEntity(tweetRequestDto.getCredentials()));
		if (optionalUser.isEmpty()) {
			throw new BadRequestException("No active user with these credentials");
		}
		String content = tweetRequestDto.getContent();
		if (content == null || content.equals("")) {
			throw new BadRequestException("Tweet must have content");
		}
		Tweet newTweet = new Tweet();
		newTweet.setContent(content);
		newTweet.setAuthor(optionalUser.get());

		List<String> usernames = new ArrayList<>();
		List<String> hashtags = new ArrayList<>();

		Pattern pattern = Pattern.compile("@(\\w+)");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			usernames.add(matcher.group(1));
		}

		pattern = Pattern.compile("#(\\w+)");
		matcher = pattern.matcher(content);

		while (matcher.find()) {
			hashtags.add(matcher.group(1));
		}

		Set<User> usersMentioned = new HashSet<>();
		for (String u : usernames) {
			Optional<User> user = userRepository.findByCredentials_UsernameAndDeletedFalse(u);
			if (!user.isEmpty()) {
				usersMentioned.add(user.get());
			}
		}
		newTweet.setUsersMentioned(new ArrayList<>(usersMentioned));

		Set<Hashtag> tagSet = new HashSet<>();
		for (String h : hashtags) {
			Optional<Hashtag> optionalTag = hashtagRepository.findByLabel(h);
			if (optionalTag.isEmpty()) {
				Hashtag newTag = new Hashtag();
				newTag.setLabel(h);
				Hashtag savedTag = hashtagRepository.saveAndFlush(newTag);
				tagSet.add(savedTag);
			} else {
				tagSet.add(optionalTag.get());
			}
		}
		newTweet.setHashtags(new ArrayList<>(tagSet));

		Tweet savedTweet = tweetRepository.saveAndFlush(newTweet);

		for (Hashtag h : new ArrayList<>(tagSet)) {
			List<Tweet> taggedTweets = h.getTaggedTweets();
			if (taggedTweets == null) {
				taggedTweets = new ArrayList<>();
				h.setTaggedTweets(taggedTweets);
			}
			taggedTweets.add(savedTweet);
			h.setTaggedTweets(taggedTweets);
			hashtagRepository.saveAndFlush(h);
		}

		return tweetMapper.entityToDto(savedTweet);
	}

	@Override
	public List<TweetResponseDto> getTweetReposts(Long id) {
		if (getTweet(id) == null) {
			throw new NotFoundException("No tweet with id " + id);
		}
		List<TweetResponseDto> allTweets = getTweets();
		List<TweetResponseDto> reposts = new ArrayList<TweetResponseDto>();
		for (TweetResponseDto tweet : allTweets) {
			if (getTweet(id).equals(tweet.getRepostOf())) {
				reposts.add(tweet);
			}
		}
		return reposts;
	}

	@Override
	public List<UserResponseDto> getTweetMentions(Long id) {
		List<UserResponseDto> mentionedUsers = new ArrayList<UserResponseDto>();
		if (getTweet(id) == null) {
			throw new NotFoundException("No tweet with id " + id);
		}
		String[] tweetContent = getTweet(id).getContent().split(" ");
		List<String> tweetWords = Arrays.asList(tweetContent);
		for (String word : tweetWords) {
			if (word.charAt(0) == '@' && (userRepository.findByCredentialsUsername(word.substring(1)) != null)) {
				mentionedUsers.add(userMapper.entityToDto(userRepository.findByCredentialsUsername(word.substring(1))));
			}
		}
		return mentionedUsers;
	}

	@Override
	public TweetResponseDto createTweetReply(Long id, TweetRequestDto tweetRequestDto) {
		Optional<Tweet> replyTo = tweetRepository.findByIdAndDeletedFalse(id);
		if (replyTo.isEmpty()) {
			throw new BadRequestException("No tweet with id: " + id);
		}
		String content = tweetRequestDto.getContent();
		if (content == null || content.equals("")) {
			throw new BadRequestException("Tweet must have content");
		}
		CredentialsDto credentials = credentialsMapper.entityToDto(tweetRequestDto.getCredentials());
		Optional<User> optionalUser = userRepository.findByCredentials_UsernameAndCredentials_PasswordAndDeletedFalse(
				credentials.getUsername(), credentials.getPassword());
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("No user found with those credentials");
		}

		Tweet reply = new Tweet();
		reply.setContent(content);
		reply.setInReplyTo(replyTo.get());
		reply.setAuthor(optionalUser.get());

		List<String> usernames = new ArrayList<>();
		List<String> hashtags = new ArrayList<>();

		Pattern pattern = Pattern.compile("@(\\w+)");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			usernames.add(matcher.group(1));
		}

		pattern = Pattern.compile("#(\\w+)");
		matcher = pattern.matcher(content);

		while (matcher.find()) {
			hashtags.add(matcher.group(1));
		}

		Set<User> usersMentioned = new HashSet<>();
		for (String u : usernames) {
			Optional<User> user = userRepository.findByCredentials_UsernameAndDeletedFalse(u);
			if (!user.isEmpty()) {
				usersMentioned.add(user.get());
			}
		}
		reply.setUsersMentioned(new ArrayList<>(usersMentioned));

		Set<Hashtag> tagSet = new HashSet<>();
		for (String h : hashtags) {
			Optional<Hashtag> optionalTag = hashtagRepository.findByLabel(h);
			if (optionalTag.isEmpty()) {
				Hashtag newTag = new Hashtag();
				newTag.setLabel(h);
				Hashtag savedTag = hashtagRepository.saveAndFlush(newTag);
				tagSet.add(savedTag);
			} else {
				tagSet.add(optionalTag.get());
			}
		}
		reply.setHashtags(new ArrayList<>(tagSet));

		Tweet savedTweet = tweetRepository.saveAndFlush(reply);

		for (Hashtag h : new ArrayList<>(tagSet)) {
			List<Tweet> taggedTweets = h.getTaggedTweets();
			if (taggedTweets == null) {
				taggedTweets = new ArrayList<>();
				h.setTaggedTweets(taggedTweets);
			}
			taggedTweets.add(savedTweet);
			h.setTaggedTweets(taggedTweets);
			hashtagRepository.saveAndFlush(h);
		}

		return tweetMapper.entityToDto(reply);
	}

}
