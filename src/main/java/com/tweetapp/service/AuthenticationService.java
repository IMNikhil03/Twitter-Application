package com.tweetapp.service;

import java.util.List;
import java.util.Optional;

import com.tweetapp.domain.LoginRequest;
import com.tweetapp.domain.LoginResponse;
import com.tweetapp.security.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.domain.ForgotPasswordRequest;
import com.tweetapp.domain.UserRegisterRequest;
import com.tweetapp.exception.InvalidOperationException;
import com.tweetapp.model.User;
import com.tweetapp.repository.UserRepository;

@Service
@Slf4j
public class AuthenticationService {


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<Object> registerNewUser(UserRegisterRequest userRegisterRequest) throws InvalidOperationException {

        boolean loginIdExists = userRepository.existsByLoginId(userRegisterRequest.getLoginId());
        if (loginIdExists) {
            throw new InvalidOperationException("Login Id already exists");
        }
        boolean emailExists = userRepository.existsByEmail(userRegisterRequest.getEmail());
        if (emailExists) {
            throw new InvalidOperationException("Email already exists");
        }

        User newUser = User.buildUser(userRegisterRequest);

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
        log.info("registered successfully");
        return new ResponseEntity<>("User Added Successfully", HttpStatus.CREATED);
    }

    public ResponseEntity<LoginRequest> getUserDetails(LoginRequest loginRequest) throws InvalidOperationException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword()));

        List<User> findByLoginId = userRepository.findByLoginId(loginRequest.getLoginId());
        if (findByLoginId.size() == 0) {
            log.info("At Login : ");
            log.error("Not Accesible : No Such User");
            return new ResponseEntity(LoginResponse.builder().loginId(loginRequest.getLoginId()).valid(false).token("Invalid User").build(), HttpStatus.FORBIDDEN);
        } else {
            User user = findByLoginId.get(0);
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);
                log.info("login successful");
                return new ResponseEntity(LoginResponse.builder().loginId(loginRequest.getLoginId()).valid(true).token(jwt).build(), HttpStatus.OK);
            } else {
                log.info("At Login : ");
                log.error("Not Accesible : Invalid Password");
                return new ResponseEntity(LoginResponse.builder().loginId(loginRequest.getLoginId()).valid(false).token("Invalid Password").build(), HttpStatus.FORBIDDEN);
            }
        }
    }

    public ResponseEntity<Object> changePassword(ForgotPasswordRequest forgotPasswordRequest) throws InvalidOperationException {
        List<User> findByLoginId = userRepository.findByLoginId(forgotPasswordRequest.getLoginId());
        if (findByLoginId.isEmpty()) {
            log.info("user not found");
            throw new InvalidOperationException("user not present!!");
        }
        log.info("resetting the password");
        User user = findByLoginId.get(0);
        user.setPassword(passwordEncoder.encode(forgotPasswordRequest.getPassword()));
//		 userRepository.deleteByLoginId(forgotPasswordRequest.getLoginId());
        userRepository.save(user);
        return new ResponseEntity<>("Password reset successfull", HttpStatus.OK);
    }

    public ResponseEntity<LoginResponse> validate(String authToken) throws InvalidOperationException {
//		String token1 = authToken;
        String token1 = authToken.substring(7);
        LoginResponse res = new LoginResponse();
        if (jwtUtils.validateJwtToken(token1)) {
            List<User> users = userRepository.findByLoginId(jwtUtils.getUserNameFromJwtToken(token1));
            if (!users.isEmpty()) {
                res.setLoginId(users.get(0).getLoginId());
                res.setValid(true);
                res.setToken("token successfully validated");
                log.info("token successfully validated");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
        } else {
            res.setValid(false);
            res.setToken("Invalid Token Received");
            log.info("At Validity : ");
            log.error("Token Has Expired");
            return new ResponseEntity<>(res, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(res, HttpStatus.NO_CONTENT);


    }
}
