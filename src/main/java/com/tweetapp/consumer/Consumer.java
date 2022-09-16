package com.tweetapp.consumer;

import com.tweetapp.config.MessagingConfig;
import com.tweetapp.domain.TweetRequest;
import com.tweetapp.service.TweetService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @Autowired
    TweetService tweetService;
    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(TweetRequest orderStatus) {
        System.out.println("Message recieved from queue : " + tweetService.saveTweet(orderStatus));

    }
}
