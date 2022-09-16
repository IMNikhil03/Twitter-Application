package com.tweetapp.controller;

import javax.validation.Valid;

import com.tweetapp.producer.Publisher;
import com.tweetapp.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tweetapp.domain.TweetRequest;
import com.tweetapp.exception.InvalidOperationException;
import com.tweetapp.service.TweetService;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/tweets")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", maxAge = 3600)
public class TweetController {

    @Autowired
    Publisher tweetProducer;



    @Autowired
    TweetService tweetService;

    @Autowired
    AuthenticationService authService;

    @PostMapping("/{loginId}/add")
    public ResponseEntity postNewTweet(@RequestHeader("Authorization") final String token, @PathVariable String loginId,
                                       @RequestBody @Valid TweetRequest tweetRequest) throws Exception {
        log.info("inside tweet service controller to add a tweets");
        if (authService.validate(token).getBody().isValid()) {

            log.info("Inside post tweet in controller");
            tweetRequest.setLoginId(loginId);
            return tweetProducer.sendTweet(tweetRequest);
//            return tweetService.saveTweet(tweetRequest);

        }
        throw new InvalidOperationException("you cannot perform this action!!");


    }

    @DeleteMapping("/{loginId}/delete/{tweetId}")
    public ResponseEntity<?> deleteTweet(@RequestHeader("Authorization") String token, @PathVariable String loginId, @PathVariable String tweetId) throws InvalidOperationException {
        log.info("deleted");
        log.info("inside tweet service controller to delete tweets");
        if (authService.validate(token).getBody().isValid()) {
            return tweetService.deleteTweet(tweetId, loginId);
        }
        throw new InvalidOperationException("you cannot perform this action!!");

    }

    @PutMapping("/{loginId}/update/{tweetId}")
    public ResponseEntity<?> updateTweet(@RequestHeader("Authorization") String token, @PathVariable String loginId, @PathVariable String tweetId,
                                         @RequestBody @Valid TweetRequest tweetRequest) throws InvalidOperationException {
        log.info("inside tweet service controller to update tweets");
        if (authService.validate(token).getBody().isValid()) {
            tweetRequest.setLoginId(loginId);

            return tweetService.updateTweet(tweetRequest, tweetId);

        }
        throw new InvalidOperationException("you cannot perform this action!!");
    }

    @PutMapping("/{loginId}/like/{tweetId}")
    public ResponseEntity<?> likeTweet(@RequestHeader("Authorization") String token, @PathVariable String loginId, @PathVariable String tweetId) throws InvalidOperationException {
        log.info("inside tweet service controller to like tweets");
        if (authService.validate(token).getBody().isValid()) {
            return tweetService.toggleTweetLike(tweetId, loginId);
        }
        throw new InvalidOperationException("Token Expired or Invalid , Login again ...");
    }

    @PutMapping("/{loginId}/unlike/{tweetId}")
    public ResponseEntity<?> unLikeTweet(@RequestHeader("Authorization") String token, @PathVariable String loginId, @PathVariable String tweetId) throws InvalidOperationException {
        log.info("inside tweet service controller to like tweets");
        if (authService.validate(token).getBody().isValid()) {
            return tweetService.toggleTweetLike(tweetId, loginId);
        }
        throw new InvalidOperationException("Token Expired or Invalid , Login again ...");
    }

    @PostMapping("/{loginId}/reply/{tweetId}")
    public ResponseEntity<?> replyTweet(@RequestHeader("Authorization") String token, @PathVariable String loginId, @PathVariable String tweetId
            , @RequestBody String reply) throws InvalidOperationException {
        log.info("inside tweet service controller to reply tweets");
        if (authService.validate(token).getBody().isValid()) {
            return tweetService.replyTweet(loginId, tweetId, reply);
        }
        throw new InvalidOperationException("Token Expired or Invalid , Login again ...");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTweets(@RequestHeader("Authorization") String token) throws InvalidOperationException {
        log.info("inside tweet service controller to all tweets");
        if (authService.validate(token).getBody().isValid()) {
            return tweetService.getAllTweets();
        }
        throw new InvalidOperationException("Token Expired or Invalid , Login again ...");

    }

    @GetMapping("/{loginId}")
    public ResponseEntity<?> getAllTweetsOfUser(@RequestHeader("Authorization") String token, @PathVariable String loginId) throws InvalidOperationException {
        log.info("inside tweet service controller to all tweets");
        if (authService.validate(token).getBody().isValid()) {
            return tweetService.getAllTweetsOfUser(loginId);
        }
        throw new InvalidOperationException("Token Expired or Invalid , Login again ...");
    }

}
