package com.tweetapp.repository;

import java.util.List;
import java.util.Optional;

import com.tweetapp.domain.TweetRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface    TweetRepository extends MongoRepository<TweetRequest, String> {
    public List<TweetRequest> findAll();

    @Query("{tweetId :?0}")
    public Optional<TweetRequest> findById(String tweetId);

    @Query(value = "{tweetId : ?0}", delete = true)
    public void deleteByTweetId(String id);

}
