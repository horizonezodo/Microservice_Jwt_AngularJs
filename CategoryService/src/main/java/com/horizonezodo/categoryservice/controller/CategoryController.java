package com.horizonezodo.categoryservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoryController {

    @GetMapping("/all-cate")
    public ResponseEntity<?> getAllCategory(){
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
