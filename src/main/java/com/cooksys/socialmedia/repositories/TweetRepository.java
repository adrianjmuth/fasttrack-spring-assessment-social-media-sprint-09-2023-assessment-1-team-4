package com.cooksys.socialmedia.repositories;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

	List<Tweet> findAllByDeletedFalse();

	List<Tweet> findAllByDeletedFalseOrderByPostedDesc();

	Optional<Tweet> findByIdAndDeletedFalse(Long id);

	List<Tweet> findByPostedBeforeAndDeletedFalseOrderByPostedDesc(Timestamp timestamp);

	List<Tweet> findByPostedAfterAndDeletedFalseOrderByPostedAsc(Timestamp timestamp);

	List<Tweet> findByAuthorAndDeletedFalseOrderByPostedDesc(User author);

	List<Tweet> findByAuthorAndDeletedFalse(User user);

}
