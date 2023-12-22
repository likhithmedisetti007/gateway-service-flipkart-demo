package com.likhith.gateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.likhith.gateway.document.User;
import com.likhith.gateway.dto.UserResponse;
import com.likhith.gateway.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
				.password(user.getPassword()).roles(user.getRoles()).build();
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getAllUsers() {

		List<UserResponse> userServiceResponseList = new ArrayList<>();
		List<User> users = userRepository.findAll();

		if (!CollectionUtils.isEmpty(users)) {
			userServiceResponseList = users.stream().map(user -> {
				UserResponse userServiceResponse = new UserResponse();
				BeanUtils.copyProperties(user, userServiceResponse);
				return userServiceResponse;
			}).collect(Collectors.toList());
		}

		return userServiceResponseList;
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public UserResponse getUser(String username) {

		UserResponse userResponse = new UserResponse();
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			BeanUtils.copyProperties(user.get(), userResponse);
			return userResponse;
		} else {
			return null;
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	public UserResponse getOtherUser(String username) {

		UserResponse userResponse = new UserResponse();
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			BeanUtils.copyProperties(user.get(), userResponse);
			return userResponse;
		} else {
			return null;
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	public String createUser(User user) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());

		if (userFromDB.isPresent()) {
			message = "User already available";
		} else {
			String encodedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encodedPassword);

			userRepository.save(user);
			message = "User created successfully";
		}

		return message;
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public String updateUser(User user) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());

		if (userFromDB.isEmpty()) {
			message = "No user found that can be updated";
		} else {
			boolean anyUpdate = false;

			if (!ObjectUtils.isEmpty(user.getPassword())) {
				String encodedPassword = passwordEncoder.encode(user.getPassword());
				userFromDB.get().setPassword(encodedPassword);
				anyUpdate = true;
			}
			if (!ObjectUtils.isEmpty(user.getRoles())) {
				userFromDB.get().setRoles(user.getRoles());
				anyUpdate = true;
			}

			if (anyUpdate) {
				userRepository.save(userFromDB.get());
				message = "User updated successfully";
			} else {
				message = "Nothing to update";
			}
		}

		return message;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public String updateOtherUser(User user) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());

		if (userFromDB.isEmpty()) {
			message = "No user found that can be updated";
		} else {
			boolean anyUpdate = false;

			if (!ObjectUtils.isEmpty(user.getPassword())) {
				return "Only SELF USER can update the password";
			}
			if (!ObjectUtils.isEmpty(user.getRoles())) {
				userFromDB.get().setRoles(user.getRoles());
				anyUpdate = true;
			}

			if (anyUpdate) {
				userRepository.save(userFromDB.get());
				message = "User updated successfully";
			} else {
				message = "Nothing to update";
			}
		}

		return message;
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public String deleteUser(String username) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(username);

		if (userFromDB.isEmpty()) {
			message = "No user found that can be deleted";
		} else {
			userRepository.delete(userFromDB.get());

			message = "User deleted successfully";
		}

		return message;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public String deleteOtherUser(String username) {

		String message = null;
		Optional<User> userFromDB = userRepository.findByUsername(username);

		if (userFromDB.isEmpty()) {
			message = "No user found that can be deleted";
		} else {
			userRepository.delete(userFromDB.get());

			message = "User deleted successfully";
		}

		return message;
	}

	public UserRepository getUserRepository() {
		return userRepository;
	}

}
