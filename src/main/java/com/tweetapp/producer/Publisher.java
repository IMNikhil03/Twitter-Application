package com.tweetapp.producer;

import com.tweetapp.config.MessagingConfig;
import com.tweetapp.domain.TweetRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Publisher {

    @Autowired
    private RabbitTemplate template;

    public ResponseEntity sendTweet(TweetRequest tweetRequest) {
        tweetRequest.setTweetId(UUID.randomUUID().toString());
        //restaurantservice
        //payment service
        template.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, tweetRequest);
        return new ResponseEntity("Added Tweet Successfully", HttpStatus.CREATED);
    }
}
