package com.tweetapp.producer;

import com.tweetapp.config.TweetConfig;
import com.tweetapp.domain.TweetRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class TweetProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public TweetRequest sendNewTweet(TweetRequest tweetRequest) {

        String tweetId = UUID.randomUUID().toString();
        tweetRequest.setTweetId(tweetId);
        //RestaurantName
        //Payment Service


        rabbitTemplate.convertAndSend(TweetConfig.EXCAHNGE,TweetConfig.ROUTING_KEY,tweetRequest);
        return tweetRequest;
    }

}


