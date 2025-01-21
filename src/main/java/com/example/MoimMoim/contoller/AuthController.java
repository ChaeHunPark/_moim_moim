package com.example.MoimMoim.contoller;

import com.example.MoimMoim.jwtUtil.JWTUtil;
import com.example.MoimMoim.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> getNewAccessToken (HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshAccessToken(request, response);
    }

}
