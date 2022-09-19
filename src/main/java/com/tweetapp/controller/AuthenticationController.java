package com.tweetapp.controller;

import javax.validation.Valid;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.tweetapp.domain.ForgotPasswordRequest;
import com.tweetapp.domain.LoginRequest;
import com.tweetapp.domain.LoginResponse;
import com.tweetapp.domain.UserRegisterRequest;
import com.tweetapp.exception.InvalidOperationException;
import com.tweetapp.model.User;
import com.tweetapp.security.jwt.JwtUtils;
import com.tweetapp.service.AuthenticationService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/tweets")
@CrossOrigin(origins = "https://tweetappfse2.azurewebsites.net/")
@Slf4j
public class AuthenticationController {


    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${tweet.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest) throws InvalidOperationException {

        log.info("In User Service - Registering User");
        log.debug("registering user {}", userRegisterRequest);
        return authenticationService.registerNewUser(userRegisterRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) throws InvalidOperationException {
        log.info("inside user service to login");
        log.debug("Login user name: {}", loginRequest.getLoginId());
        return authenticationService.getUserDetails(loginRequest);

    }

    @PutMapping("/{loginId}/forgot")
    public ResponseEntity<?> forgotPassword(@PathVariable String loginId, @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) throws InvalidOperationException {
        log.info("inside user service to change forgot password {}", loginId);
        return authenticationService.changePassword(forgotPasswordRequest);
    }

    @GetMapping(value = "/validate")
    public ResponseEntity<LoginResponse> getValidity(@RequestHeader("Authorization") final String token) throws InvalidOperationException {
        log.info("inside user service to validate the token");
        return authenticationService.validate(token);
    }

}
