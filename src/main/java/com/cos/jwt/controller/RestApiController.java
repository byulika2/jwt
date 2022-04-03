package com.cos.jwt.controller;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestApiController {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @GetMapping("/home")
  public String home() {
    return "<h1>Home Sweet Home!! 왜 안되니?</h1>";
  }

  @PostMapping("/join")
  public String join(@RequestBody User user) {
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    user.setRole("ROLE_USER");
    userRepository.save(user);
    return "회원가입완료";
  }


  // manager, admin 접근가능
  @GetMapping("/api/v1/user")
  public String user() {
    return "user";
  }

  @GetMapping("/api/v1/manager")
  public String manager() {
    return "manager";
  }

  @GetMapping("/api/v1/admin")
  public String admin() {
    return "admin";
  }
}
