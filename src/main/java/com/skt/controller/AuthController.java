package com.skt.controller;

import com.skt.component.JwtUtil;
import com.skt.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    JwtUtil jwtUtil;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        boolean success = authService.authenticate(username, password);

        if (success) {
            String token = jwtUtil.generateToken(username);

            return ResponseEntity.ok(Map.of(
                    "message", username,
                    "token", token
            ));
        } else {
            return ResponseEntity.status(401)
                    .body("Invalid username or password ❌");
        }
    }
}