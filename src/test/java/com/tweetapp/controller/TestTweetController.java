package com.tweetapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.domain.LoginRequest;
import com.tweetapp.domain.LoginResponse;
import com.tweetapp.domain.TweetRequest;
import com.tweetapp.domain.UserRegisterRequest;
import com.tweetapp.exception.InvalidOperationException;
import com.tweetapp.model.User;
import com.tweetapp.repository.UserRepository;
import com.tweetapp.service.TweetService;

@SpringBootTest
@AutoConfigureMockMvc
public class TestTweetController {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	TweetService tweetService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passeordEncoder;

	private String token = "";

	private String testTweetId = "testId";

	private User testUser = null;

	@BeforeEach
	void setUp() throws Exception {

		UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder().firstName("test").lastName("testing")
				.email("test@ing.com").loginId("test_user").password(passeordEncoder.encode("Test@Password1")).build();

		testUser = User.buildUser(userRegisterRequest);

		if (userRepository.existsByEmail(testUser.getEmail())) {
			userRepository.findByEmail(testUser.getEmail()).stream().forEach(user -> {
				userRepository.delete(user);
			});
		}

		if (userRepository.existsByLoginId(testUser.getLoginId())) {
			userRepository.findByEmail(testUser.getLoginId()).stream().forEach(user -> {
				userRepository.delete(user);
			});
		}

		testUser = userRepository.save(testUser);

		LoginRequest loginRequest = LoginRequest.builder().loginId("test_user").password("Test@Password1").build();
		String json = objectMapper.writeValueAsString(loginRequest);

		MvcResult mvcResult = mockMvc
				.perform(post("/api/v1.0/tweets/login").content(json).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString();

		LoginResponse loginResponse = objectMapper.readValue(contentAsString, LoginResponse.class);

		token = loginResponse.getToken();

	}

	@AfterEach
	void tearDown() {
		token = "";
		testTweetId = "";
		if (testUser != null)
			userRepository.delete(testUser);
	}

	@Test
	void testUpdateTweet_InvalidCase() throws Exception {

		// given
		TweetRequest tweetRequest = TweetRequest.builder().message("Hello1").build();
		String json = objectMapper.writeValueAsString(tweetRequest);

		// when
		InvalidOperationException response=new InvalidOperationException("you cannot perform this action");
		when(tweetService.updateTweet(isA(TweetRequest.class), anyString())).thenThrow(response);

		MvcResult mvcResult = mockMvc.perform(put("/api/v1.0/tweets/test_user2/update/" + testTweetId)
				.header("Authorization", "Bearer " + token).content(json).contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		String contentAsString = mvcResult.getResponse().getContentAsString();

		String expectedOutput = "you cannot perform this action";
		
		// then
		assertEquals(expectedOutput, contentAsString);

	}
	
	@Test
	void testUpdateTweet_ValidCase() throws Exception {

		// given
		TweetRequest tweetRequest = TweetRequest.builder().message("Hello1").build();
		String json = objectMapper.writeValueAsString(tweetRequest);
		ResponseEntity<Object> response=new ResponseEntity<>("Updated Tweet Successfully",HttpStatus.OK);

		when(tweetService.updateTweet(isA(TweetRequest.class), anyString())).thenReturn(response);

		mockMvc.perform(put("/api/v1.0/tweets/test_user/update/" + testTweetId)
				.header("Authorization", "Bearer " + token).content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());


	}

	@Test
	void testPostTweet_InValidMessage() throws Exception {

		TweetRequest tweetRequest = TweetRequest.builder().message("").build();
		String json = objectMapper.writeValueAsString(tweetRequest);

		when(tweetService.updateTweet(isA(TweetRequest.class), anyString())).thenReturn(null);
		String expectedContent = "message - must not be blank";

		mockMvc.perform(put("/api/v1.0/tweets/test_user/update/" + testTweetId)
				.header("Authorization", "Bearer " + token).content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError()).andExpect(content().string(expectedContent));

	}

	@Test
	void testDeleteTweet_ValidCase() throws Exception {

		when(tweetService.deleteTweet(anyString(), anyString())).thenReturn(new ResponseEntity<Object>("liked the Tweet Successfully",HttpStatus.OK));

		mockMvc.perform(delete("/api/v1.0/tweets/test_user/delete/" + testTweetId)
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}
	
	@Test
	void testDeleteTweet_InvalidCase1() throws Exception {

		when(tweetService.deleteTweet(anyString(), anyString())).thenThrow(new InvalidOperationException("Invalid Tweet Id"));

		mockMvc.perform(delete("/api/v1.0/tweets/test_user2/delete/" + testTweetId)
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());

	}
	


	@Test
	void testDeleteTweet_InValidCase() throws Exception {

		doThrow(new InvalidOperationException("Tweet id not found")).when(tweetService).deleteTweet(anyString(),
				anyString());
		String expectedContent = "Tweet id not found";

		mockMvc.perform(delete("/api/v1.0/tweets/test_user/delete/" + testTweetId)
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError()).andExpect(content().string(expectedContent));

	}

	@Test
	void testLikeTweet() throws Exception {

		when(tweetService.toggleTweetLike(testTweetId, "tweetApp")).thenReturn(new ResponseEntity<Object>("liked the Tweet Successfully",HttpStatus.OK));

		mockMvc.perform(put("/api/v1.0/tweets/" + "tweetApp" + "/like/" + testTweetId)
				.header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

	@Test
	void testReplyTweet() throws Exception {

		// given
		TweetRequest tweetRequest = TweetRequest.builder().message("Hello1").build();
		String json = objectMapper.writeValueAsString("ReplyRequest");


		// when
		when(tweetService.replyTweet("test_usr", testTweetId, json)).thenReturn(new ResponseEntity<Object>("Replied to Tweet Successfully", HttpStatus.OK));

		mockMvc.perform(post("/api/v1.0/tweets/test_user/reply/" + testTweetId)
						.header("Authorization", "Bearer " + token).content(json).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testGetAllTweets() throws Exception {

		TweetRequest tweetRequest = TweetRequest.builder().message("Hello1").build();
		when(tweetService.getAllTweets()).thenReturn(new ResponseEntity<>(null,HttpStatus.OK));

		mockMvc.perform(get("/api/v1.0/tweets/all").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

	}
	

	@Test
	void testGetAllTweetsOfUser() throws Exception {

		TweetRequest tweetRequest = TweetRequest.builder().message("Hello1").build();
		when(tweetService.getAllTweetsOfUser(anyString())).thenReturn(new ResponseEntity<>(null,HttpStatus.OK));


		mockMvc.perform(get("/api/v1.0/tweets/test_user").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
	}

}