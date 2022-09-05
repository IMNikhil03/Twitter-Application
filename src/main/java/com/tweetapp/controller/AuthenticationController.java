package com.tweetapp.controller;

import javax.validation.Valid;

import ch.qos.logback.core.net.SyslogOutputStream;
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
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	AuthenticationService authenticationService;
	
	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Value("${tweet.app.jwtExpirationMs}")
	private long jwtExpirationMs;
	
	@PostMapping("/register")
	public User registerUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest ) throws InvalidOperationException {
		
		User registeredUser = authenticationService.registerNewUser(userRegisterRequest);
		
		return registeredUser;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) throws InvalidOperationException {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtUtils.generateJwtToken(authentication);

		User user = authenticationService.getUserDetails(loginRequest.getLoginId());
		LoginResponse loginResponse = LoginResponse.builder().jwtToken(jwt).build();

		return ResponseEntity.ok(loginResponse);

	}
	
	@PostMapping("/{loginId}/forgot")
	public ResponseEntity<?> forgotPassword(@RequestHeader("Authorization") String token, @PathVariable String loginId, @Valid @RequestBody  ForgotPasswordRequest forgotPasswordRequest) throws InvalidOperationException{

		System.out.println(jwtUtils.validateJwtToken(token) );
	if(jwtUtils.validateJwtToken(token) && jwtUtils.getUserNameFromJwtToken(token).equals(loginId)) {
		System.out.println("Login ID :  " + loginId);
		System.out.println("Validated ");
		forgotPasswordRequest.setLoginId(loginId);
		User user = authenticationService.changePassword(forgotPasswordRequest);
		return ResponseEntity.ok(user);
	}
	return new ResponseEntity<>("Invalid loginId", HttpStatus.valueOf(500));
	}
	
}
