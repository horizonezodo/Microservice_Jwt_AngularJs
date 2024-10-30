package com.horizonezodo.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String email;
    private String phone;
    private String password;
    private String avatarImg;
    private Instant dayCreate;
    private BigDecimal wallet;
    private boolean isActivate;
    private boolean emailActivate;
    private boolean phoneActivate;
    private boolean isLocked;
    private Instant dayLocked;
    private String bio;
    private String otherEmail;
    private String address;
    private LocalDate birthDay;
    private BigDecimal totalPayAmount;
    private Long accountRank;
    private String role;
}
