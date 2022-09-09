package com.tweetapp.consumer;

import com.tweetapp.config.TweetConfig;
import com.tweetapp.domain.TweetRequest;
import com.tweetapp.service.TweetService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TweetConsumer {

    @Autowired
    TweetService tweetService;

    @RabbitListener(queues = TweetConfig.QUEUE)
    public void consumeMessagFromQueue(TweetRequest tweetRequest) {
        System.out.println("Message received from Queue : " + tweetRequest);
        tweetService.saveTweet(tweetRequest);
    }
}
