package com.horizonezodo.accountservice.controller;

import com.horizonezodo.accountservice.Jwt.JwtUntil;
import com.horizonezodo.accountservice.exception.RefreshTokenException;
import com.horizonezodo.accountservice.model.RefreshToken;
import com.horizonezodo.accountservice.model.User;
import com.horizonezodo.accountservice.repo.UserRepo;
import com.horizonezodo.accountservice.request.LoginRequest;
import com.horizonezodo.accountservice.request.RefreshTokenRequest;
import com.horizonezodo.accountservice.request.RegisterRequest;
import com.horizonezodo.accountservice.response.LoginResponse;
import com.horizonezodo.accountservice.response.MessageResponse;
import com.horizonezodo.accountservice.response.RefreshTokenResponse;
import com.horizonezodo.accountservice.service.RefreshTokenServiceImpl;
import com.horizonezodo.accountservice.service.UserDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController{

    @Autowired
    private UserRepo repo;

    @Autowired
    private JwtUntil until;

    @Autowired
    private RefreshTokenServiceImpl service;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        if(repo.existsByEmail(request.getUserName()) || repo.existsByPhone(request.getUserName())){
            Authentication authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailImpl userDetail = (UserDetailImpl) authentication.getPrincipal();
            String jwt = "";
            if(request.getUserName().contains("@")){
                jwt = until.generateJwtTokenForLoginUsingEmail(userDetail);
            }else{
                jwt = until.generateJwtTokenForLoginUsingPhone(userDetail);
            }

            RefreshToken refreshToken = service.createRefreshToken(userDetail.getId());
            LoginResponse response = new LoginResponse(jwt, refreshToken.getToken(), userDetail.getUsername(), userDetail.getRole());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            MessageResponse response = new MessageResponse("801");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        if(repo.existsByEmail(request.getEmail())){
            MessageResponse response = new MessageResponse("802");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if(repo.existsByPhone(request.getPhone())){
            MessageResponse response = new MessageResponse("803");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setAccountRank(0L);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setUserName(request.getUserName());
        repo.save(user);
        MessageResponse response =  new MessageResponse("800");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request){
        return service.findByToken(request.getRefreshToken())
                .map(service::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = "";
                    if(user.getEmail() != null && !user.getEmail().isEmpty()){
                        newAccessToken = until.generateTokenFromData(user.getEmail());
                    }else if(user.getPhone() != null && !user.getPhone().isEmpty()){
                        newAccessToken = until.generateTokenFromData(user.getPhone());
                    }

                    RefreshToken newRefreshToken = service.updateRefreshToken(request.getRefreshToken());
                    RefreshTokenResponse response = new RefreshTokenResponse(newAccessToken, newRefreshToken.getToken());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }).orElseThrow(() -> new RefreshTokenException(request.getRefreshToken(), "805"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetail.getId();
        service.deleteByUserId(userId);
        MessageResponse response = new MessageResponse("799");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/check-token")
    public ResponseEntity<?> checkToken(@RequestParam String token){
        if(until.verifyToken(token)){
            return new ResponseEntity<>(new MessageResponse("798"),HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new MessageResponse("806"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all-user")
    public ResponseEntity<?> getAllUser(){
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id")Long id){
        Optional<User> opt = repo.findById(id);
        if(opt.isPresent()){
            return new ResponseEntity<>(opt.get(),HttpStatus.OK);
        }else{
            MessageResponse response = new MessageResponse("808");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
    }
}
