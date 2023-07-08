package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

	@Mock
	private UserRepository repository;
	@Mock
	private CustomUserUtil userUtil;

	private UserEntity user;
	private String existsUsername, nonExistsUsername;
	private List<UserDetailsProjection> list;

	@BeforeEach
	void setUp() {
		user = UserFactory.createUserEntity();
		existsUsername = "ADM";
		list = UserDetailsFactory.createCustomAdminClientUser("ADM");



		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existsUsername);
		Mockito.when(repository.searchUserAndRolesByUsername(existsUsername)).thenReturn(list);
		Mockito.when(repository.searchUserAndRolesByUsername(nonExistsUsername)).thenReturn(new ArrayList<>());
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		Mockito.when(repository.findByUsername(any())).thenReturn(Optional.of(user));

		UserEntity result = service.authenticated();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(user.getName(), result.getName());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(repository.findByUsername(any())).thenThrow(EntityNotFoundException.class);

		Assertions.assertThrows(UsernameNotFoundException.class,()->{
			service.authenticated();
		});

	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(existsUsername);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(existsUsername, result.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Assertions.assertThrows(UsernameNotFoundException.class,()->{
			UserDetails result = service.loadUserByUsername(nonExistsUsername);
		});
	}
}
