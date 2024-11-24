package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*",maxAge = 3600)
public class UserController {

    @Autowired
    UserRepository repo;

    @PostMapping
    void save(@RequestBody User user){
        repo.save(user);
    }

    @GetMapping
    List<User>userList(){
        return repo.findAll();
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable int id){
        repo.deleteById(id);
    }
    
}
