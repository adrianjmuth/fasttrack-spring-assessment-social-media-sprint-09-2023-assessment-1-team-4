package com.cooksys.socialmedia.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.socialmedia.embeds.Credentials;
import com.cooksys.socialmedia.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findAllByDeletedFalse();

	Optional<User> findByIdAndDeletedFalse(Long id);

	Optional<User> findByCredentialsAndDeletedFalse(Credentials credentials);

	Optional<User> findByCredentials(Credentials credentials);

	Optional<User> findByCredentials_UsernameAndDeletedFalse(String username);

	User findByCredentialsUsernameAndCredentialsPasswordAndDeletedFalse(String username, String password);

	User findByCredentialsUsernameAndDeletedFalse(String username);

	User findByCredentialsUsername(String username);

	User findByCredentialsUsernameAndCredentialsPassword(String username, String password);

	Optional<User> findByCredentials_UsernameAndCredentials_PasswordAndDeletedFalse(String username, String password);

	Optional<User> getUserByCredentialsUsername(String username);

}
