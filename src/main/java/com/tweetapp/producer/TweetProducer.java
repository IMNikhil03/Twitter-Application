package com.tweetapp.producer;

import com.tweetapp.config.TweetConfig;
import com.tweetapp.domain.TweetRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TweetProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public ResponseEntity<?> sendNewTweet(TweetRequest tweetRequest) {

        String tweetId = UUID.randomUUID().toString();
        tweetRequest.setTweetId(tweetId);
        tweetRequest.setStatus(true);
        tweetRequest.setTime(LocalDateTime.now());
        //RestaurantName
        //Payment Service
        rabbitTemplate.convertAndSend(TweetConfig.EXCAHNGE, TweetConfig.ROUTING_KEY, tweetRequest);
        return new ResponseEntity("Added Tweet Successfully", HttpStatus.CREATED);
    }

}


