package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Tweet {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	private User author;
	
	@CreationTimestamp
	private Timestamp posted;
	
	private boolean deleted = false;
	
	private String content;
	
	@ManyToOne
	@JsonIdentityReference(alwaysAsId = true)
	private Tweet inReplyTo;
	
	@OneToMany(mappedBy = "inReplyTo")
	@JsonIdentityReference(alwaysAsId = true)
	private List<Tweet> replies;
	
	@ManyToOne
	@JsonIdentityReference(alwaysAsId = true)
	private Tweet repostOf;
	
	@OneToMany(mappedBy = "repostOf")
	@JsonIdentityReference(alwaysAsId = true)
    private List<Tweet> reposts;
	
	// users that liked this tweet
	@ManyToMany(mappedBy = "likedTweets")
	@JsonIdentityReference(alwaysAsId = true)
	private List<User> usersWhoLiked;
	
	// mentions
	@ManyToMany
	@JoinTable(name = "user_mentions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tweet_id"))
	@JsonIdentityReference(alwaysAsId = true)
	private List<User> usersMentioned;
	
	@ManyToMany
	@JoinTable(name = "tweet_hashtags", joinColumns = @JoinColumn(name = "tweet_id"), inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
	@JsonIdentityReference(alwaysAsId = true)
	private List<Hashtag> hashtags;
	
}
