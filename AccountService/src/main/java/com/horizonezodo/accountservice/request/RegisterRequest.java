package com.horizonezodo.accountservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String role;
}
