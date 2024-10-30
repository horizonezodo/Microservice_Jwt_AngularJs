package com.horizonezodo.accountservice.service;

import com.horizonezodo.accountservice.model.User;
import com.horizonezodo.accountservice.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repo.findUserByEmail(username);
        Optional<User> userOpt = repo.findUserByPhone(username);
        if(userOptional.isPresent()){
            return UserDetailImpl.build(userOptional.get());
        }else if(userOpt.isPresent()){
            return UserDetailImpl.build(userOpt.get());
        }
        return null;
    }
}
