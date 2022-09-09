package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.domain.TweetRequest;
import com.tweetapp.exception.InvalidOperationException;
import com.tweetapp.model.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;

@SpringBootTest
public class TestTweetService {

	@Mock
	TweetRepository tweetRepository;

	@Mock
	UserRepository userRepository;

	@Spy
	ObjectMapper objectMapper;

	@InjectMocks
	TweetService tweetService;

	@Test
	void deleteTweet_InvalidAction() {
		TweetRequest tweet = TweetRequest.builder().loginId("someother").build();
		Optional<TweetRequest> optional = Optional.of(tweet);
		when(tweetRepository.findById(anyString())).thenReturn(optional);

		InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
			tweetService.deleteTweet("sometweet", "some");
		});
		String expectedMsg = "you cannot perform this action";

		assertEquals(expectedMsg, exception.getMessage());
		verify(tweetRepository, times(0)).delete(tweet);
	}

	@Test
	void deleteTweet_InvalidTweetId() {
		TweetRequest tweet = TweetRequest.builder().loginId("someother").build();
		Optional<TweetRequest> optional = Optional.ofNullable(null);
		when(tweetRepository.findById(anyString())).thenReturn(optional);

		InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
			tweetService.deleteTweet("sometweet", "some");
		});
		String expectedMsg = "Invalid Tweet Id";

		assertEquals(expectedMsg, exception.getMessage());
		verify(tweetRepository, times(0)).delete(tweet);

	}



	@Test
	void updateTweet_InvalidTweetId() {

		TweetRequest tweetRequest = TweetRequest.builder().message("Hello1").build();

		TweetRequest tweet = TweetRequest.builder().loginId("some").build();
		Optional<TweetRequest> optional = Optional.ofNullable(null);
		when(tweetRepository.findById(anyString())).thenReturn(optional);

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			tweetService.updateTweet(tweetRequest, "some");
		});
		String expectedMsg = "Invalid Tweet Id";

		assertEquals(expectedMsg, exception.getMessage());
		verify(tweetRepository, times(0)).save(tweet);
	}

	@Test
	void updateTweet_InvalidAction() {

		TweetRequest tweetRequest = TweetRequest.builder().loginId("loginId").message("Hello1").build();

		TweetRequest tweet = TweetRequest.builder().loginId("someOtherLoginId").build();
		Optional<TweetRequest> optional = Optional.ofNullable(tweet);
		when(tweetRepository.findById(anyString())).thenReturn(optional);

		InvalidOperationException exception = assertThrows(InvalidOperationException.class, () -> {
			tweetService.updateTweet(tweetRequest, "someTweetId");
		});
		String expectedMsg = "you cannot perform this action";

		assertEquals(expectedMsg, exception.getMessage());
		verify(tweetRepository, times(0)).save(tweet);
	}

	@Test
	void updateTweet_ValidCase() throws InvalidOperationException {

		TweetRequest tweetRequest = TweetRequest.builder().loginId("loginId").message("Hello2").build();

		TweetRequest tweet = TweetRequest.builder().loginId("loginId").message("hello1").build();
		Optional<TweetRequest> optional = Optional.ofNullable(tweet);
		when(tweetRepository.findById(anyString())).thenReturn(optional);

		int ActualStatus = tweetService.updateTweet(tweet, "someTweetId").getStatusCodeValue();

		int expectedResult =200;

		assertEquals(expectedResult, ActualStatus);

	}
}
