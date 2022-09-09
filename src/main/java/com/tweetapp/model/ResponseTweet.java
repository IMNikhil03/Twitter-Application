package com.tweetapp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTweet {


    private String id;
    private String message;
    private LocalDateTime time;
    private String loginId;
    private Set<String> likes;
    private List<String> replies;
    private String fname;
    private String lname;
    private boolean Status;
}
