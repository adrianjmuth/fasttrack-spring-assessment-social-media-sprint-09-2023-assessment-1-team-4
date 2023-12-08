package com.cooksys.socialmedia.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.cooksys.socialmedia.embeds.Credentials;
import com.cooksys.socialmedia.embeds.Profile;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="user_table")
@Entity
@NoArgsConstructor
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

	@Id
	@GeneratedValue
	private Long id;
	
	@CreationTimestamp
	private Timestamp joined;
	
	@Embedded
	private Profile profile;
	
	@Embedded
	private Credentials credentials;
	
	private boolean deleted = false;
	
	
	@OneToMany(mappedBy = "author")
	@JsonIdentityReference(alwaysAsId = true)
	private List<Tweet> tweets;
	
	@ManyToMany
	@JoinTable(name = "followers_following")
	@JsonIdentityReference(alwaysAsId = true)
	private List<User> following;
	
	@ManyToMany(mappedBy = "following")
	@JsonIdentityReference(alwaysAsId = true)
	private List<User> followers;
	
	@ManyToMany
	@JoinTable(
			name = "user_likes", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "tweet_id")
			)
	@JsonIdentityReference(alwaysAsId = true)
	private List<Tweet> likedTweets;
	
	@ManyToMany(mappedBy = "usersMentioned")
	@JsonIdentityReference(alwaysAsId = true)
	private List<Tweet> mentionedTweets;
	
}