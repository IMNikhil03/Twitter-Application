package com.tweetapp.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document
public class TweetRequest {
    private String tweetId;
    private String loginId;
    @NotBlank
    @Size(max = 144)
    private String message;
    private LocalDateTime time;
    private Set<String> likes;
    private List<String> replies;
    @JsonIgnore
    private boolean Status;

}
