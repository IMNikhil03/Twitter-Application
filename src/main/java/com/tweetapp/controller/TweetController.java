package com.tweetapp.controller;

import java.util.List;

import javax.validation.Valid;

import com.tweetapp.producer.TweetProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.domain.TweetReplyRequest;
import com.tweetapp.domain.TweetRequest;
import com.tweetapp.exception.InvalidOperationException;
import com.tweetapp.model.Tweet;
import com.tweetapp.service.TweetService;

@RestController
@RequestMapping("/api/v1.0/tweets")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*",maxAge = 3600)
public class TweetController {

	@Autowired
	TweetProducer tweetProducer;

	@Autowired
	TweetProducer tweetPublisher;


	@Autowired
	TweetService tweetService;

	@PostMapping("/{loginId}/add")
	public ResponseEntity<TweetRequest> postNewTweet(Authentication authentication,@PathVariable String loginId,
			@RequestBody @Valid TweetRequest tweetRequest) throws Exception {
		if(!authentication.getName().equals(loginId)) {
			throw new InvalidOperationException("you cannot perform this action!!");
		}

		tweetRequest.setLoginId(loginId);

		TweetRequest tweet = tweetPublisher.sendNewTweet(tweetRequest);

		return new ResponseEntity<>(tweet, HttpStatus.CREATED);
	}
	
	@PostMapping("/{loginId}/post")
	public ResponseEntity<Tweet> postTweet(Authentication authentication,@PathVariable String loginId,
			@RequestBody @Valid TweetRequest tweetRequest) throws Exception {
		System.out.println("\n\\n\nAuthentcayion : "+authentication);
		if(!authentication.getName().equals(loginId)) {
			throw new InvalidOperationException("you cannot perform this action!!");
		}
		
		tweetRequest.setLoginId(loginId);

		Tweet tweet = tweetService.saveTweet(tweetRequest);

		return new ResponseEntity<>(tweet, HttpStatus.CREATED);
	}

	@DeleteMapping("/{loginId}/delete/{tweetId}")
	public ResponseEntity<?> deleteTweet(Authentication authentication,@PathVariable String loginId, @PathVariable String tweetId) throws InvalidOperationException {
		
		if(!authentication.getName().equals(loginId)) {
			throw new InvalidOperationException("you cannot perform this action!!");
		}

		tweetService.deleteTweet(tweetId,loginId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/{loginId}/update/{tweetId}")
	public ResponseEntity<?> updateTweet(Authentication authentication, @PathVariable String loginId, @PathVariable String tweetId,
			@RequestBody @Valid TweetRequest tweetRequest) throws InvalidOperationException {

		if(!authentication.getName().equals(loginId)) {
			throw new InvalidOperationException("you cannot perform this action!!");
		}
		
		tweetRequest.setLoginId(loginId);

		Tweet updatedTweet = tweetService.updateTweet(tweetRequest, tweetId);

		return new ResponseEntity<>(updatedTweet, HttpStatus.OK);
	}

	@PutMapping("/{loginId}/like/{tweetId}")
	public ResponseEntity<?> likeTweet(@PathVariable String loginId, @PathVariable String tweetId) {
		tweetService.toggleTweetLike(tweetId, loginId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{loginId}/reply/{tweetId}")
	public ResponseEntity<?> replyTweet(@PathVariable String loginId, @PathVariable String tweetId,
			@RequestBody @Valid TweetReplyRequest tweetReplyRequest) {
		tweetReplyRequest.setLoginId(loginId);
		tweetReplyRequest.setTweetId(tweetId);
		Tweet tweet = tweetService.replyTweet(tweetReplyRequest);
		return new ResponseEntity<>(tweet, HttpStatus.OK);
	}

	@GetMapping("/all")
	public ResponseEntity<List<Tweet>> getAllTweets() {
		List<Tweet> tweets = tweetService.getAllTweets();
		return new ResponseEntity<>(tweets, HttpStatus.OK);
	}
	@GetMapping("/{loginId}")
	public ResponseEntity<List<Tweet>> getAllTweetsOfUser(@PathVariable String loginId) throws InvalidOperationException {
		List<Tweet> tweets = tweetService.getAllTweetsOfUser(loginId);
		return new ResponseEntity<>(tweets, HttpStatus.OK);
	}
	
}
