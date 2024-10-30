package com.horizonezodo.accountservice.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.horizonezodo.accountservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userName;
    private String email;
    private String phone;
    private String role;
    @JsonIgnore
    private String password;

    public static UserDetailImpl build(User user){
        return new UserDetailImpl(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getPassword()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
