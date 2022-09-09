package com.tweetapp.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.tweetapp.model.ResponseMessage;
import com.tweetapp.model.ResponseTweet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tweetapp.domain.TweetRequest;
import com.tweetapp.exception.InvalidOperationException;
import com.tweetapp.model.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TweetService {

    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserRepository userRepository;


    public ResponseEntity<Object> deleteTweet(String tweetId, String loginId) throws InvalidOperationException {
        Optional<TweetRequest> optionalTweet = tweetRepository.findById(tweetId);
        if (!optionalTweet.isPresent()) {
            throw new InvalidOperationException("Invalid Tweet Id");
        }
        TweetRequest tweet = optionalTweet.get();
        if (!tweet.getLoginId().equals(loginId)) {
            throw new InvalidOperationException("you cannot perform this action");
        }
        tweet.setStatus(false);
        log.info("Validation is successfull for the Tweet: {}", optionalTweet.get());
        tweetRepository.deleteByTweetId(tweetId);
        log.info("successfull deleted the Tweet: {}", optionalTweet.get());
        return new ResponseEntity<Object>("Deleting Tweet Successfully", HttpStatus.OK);
    }

    public ResponseEntity<Object> updateTweet(TweetRequest tweetRequest, String tweetId) throws InvalidOperationException {
        log.info("inside tweet service Implementation to update tweet data");

        Optional<TweetRequest> optionalTweet = tweetRepository.findById(tweetId);
        if (!optionalTweet.isPresent()) {
            throw new IllegalArgumentException("Invalid Tweet Id");
        }
        TweetRequest tweet = optionalTweet.get();
        if (!tweet.getLoginId().equals(tweetRequest.getLoginId())) {
            throw new InvalidOperationException("you cannot perform this action");
        }
        log.info("Validation is successfull for the Tweet: {}", tweet);
        tweet.setMessage(tweetRequest.getMessage());
        tweet.setTime(LocalDateTime.now());
        tweetRepository.deleteByTweetId(tweetId);
        tweetRepository.save(tweet);
        log.info("successfull updated the Tweet: {}", tweet);
        return new ResponseEntity<Object>("Updated Tweet Successfully", HttpStatus.OK);
    }

    public ResponseEntity<Object> toggleTweetLike(String tweetId, String loginId) throws InvalidOperationException {
        log.info("inside tweet service Implementation to like tweet");
        Optional<TweetRequest> optionalTweet = tweetRepository.findById(tweetId);
        if (!optionalTweet.isPresent()) {
            throw new InvalidOperationException("Invalid Tweet Id");
        }
        log.info("Validation is successfull for the Tweet: {}", optionalTweet.get());
        TweetRequest tweet = optionalTweet.get();
        if (tweet.getLikes() != null) {
            tweet.getLikes().add(loginId);
            tweet.setLikes(tweet.getLikes());
        } else {
            Set<String> set = new HashSet<String>();
            set.add(loginId);
            tweet.setLikes(set);
        }
        tweetRepository.deleteByTweetId(tweetId);
        tweetRepository.save(tweet);

        return new ResponseEntity<Object>("liked the Tweet Successfully", HttpStatus.OK);
    }

    public ResponseEntity<Object> unLikeTweet(String username, String id) throws InvalidOperationException {
        log.info("inside tweet service Implementation to unlike tweet");
        Optional<TweetRequest> tweet = tweetRepository.findById(id);
        if (tweet.isEmpty()) {
            throw new InvalidOperationException("Tweet not found exception");
        }
        if (tweet.get().getLikes() != null) {
            tweet.get().getLikes().remove(username);
            tweet.get().setLikes(tweet.get().getLikes());
        }
        tweetRepository.deleteByTweetId(id);
        tweetRepository.save(tweet.get());
        return new ResponseEntity<Object>("Un-liked the Tweet Successfully", HttpStatus.OK);
    }

    public ResponseEntity<Object> replyTweet(String loginId, String tweetId, String reply) {
        Optional<TweetRequest> optionalTweet = tweetRepository.findById(tweetId);
        if (!optionalTweet.isPresent()) {
            throw new IllegalArgumentException("Invalid Tweet Id");
        }
        log.info("Validation is successfull for the Tweet: {}", optionalTweet.get());
        TweetRequest tweet = optionalTweet.get();
        User user = userRepository.findByLoginId(loginId).get(0);

        reply = reply.replace("+", " ");
        reply = reply.replace("=", "");
        reply = user.getFirstName() + " " + user.getLastName() + "-" + reply;
        if (tweet.getReplies() != null)
            tweet.getReplies().add(reply);
        else {
            List<String> l = new ArrayList<String>();
            l.add(reply);
            tweet.setReplies(l);
        }
        tweetRepository.deleteByTweetId(tweetId);
        tweetRepository.save(tweet);
        return new ResponseEntity<Object>("Replied to Tweet Successfully", HttpStatus.OK);

    }

    public ResponseEntity<?> getAllTweets() {
        List<TweetRequest> tweets = tweetRepository.findAll();
        System.out.println(!tweets.isEmpty() + " " + tweets.size());
        if (!tweets.isEmpty() && tweets.size() > 0)
            return new ResponseEntity<Object>(formatData(tweets), HttpStatus.OK);
        return new ResponseEntity<Object>(new ResponseMessage("No Tweets Found"), HttpStatus.NO_CONTENT);
    }

    public List<ResponseTweet> formatData(List<TweetRequest> list) {
        log.info("inside tweet service Implementation to format data :" + list);
        List<ResponseTweet> result = new ArrayList<>();
        for (TweetRequest tweet : list) {
            List<User> users = userRepository.findByLoginId(tweet.getLoginId());
            User user = users.get(0);
            ResponseTweet tweet1 = new ResponseTweet(tweet.getTweetId(), tweet.getMessage(),
                    tweet.getTime(), tweet.getLoginId(), tweet.getLikes(), tweet.getReplies(), user.getFirstName(),
                    user.getLastName(), tweet.isStatus());
            result.add(tweet1);
            System.out.println(tweet1);
        }
        return result;
    }

    public ResponseEntity<Object> getAllTweetsOfUser(String loginId) throws InvalidOperationException {

        log.info("inside tweet service Implementation to get tweets by username");
        List<TweetRequest> tweets = tweetRepository.findAll().stream().filter(o -> o.getLoginId().equals(loginId)).collect(Collectors.toList());
        if (!tweets.isEmpty() && tweets.size() > 0)
            return new ResponseEntity<Object>(formatData(tweets), HttpStatus.OK);
        return new ResponseEntity<Object>(new ResponseMessage("No Tweets Found"), HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> saveTweet(TweetRequest tweetRequest) {
        String tweetId = UUID.randomUUID().toString();
        tweetRequest.setTweetId(tweetId);
        tweetRequest.setStatus(true);
        tweetRequest.setTime(LocalDateTime.now());
        tweetRepository.save(tweetRequest);
        return new ResponseEntity("Added Tweet Successfully", HttpStatus.CREATED);
    }
}
