package com.example.MoimMoim.service;

import com.example.MoimMoim.jwtUtil.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



/*
* DB에 저장, 조회, 삭제 로직을 추가하거나
* Redis를 사용해 서버의 주도권을 가지는 로직 추가해야할 것.
*
* */

@Service
public class AuthService {

    private final JWTUtil jwtUtil;

    @Autowired
    public AuthService(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // 엑세스 토큰이 만료되었을 때 재발급 해준다.
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {


        String refresh = null;

        // refresh 토큰은 cookie에 저장 되기 때문에 cookie에서 찾는다.
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        // refresh가 없다.
        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 유효기간이 다되었다.
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }


        // 유저이름과 롤을 가져온다.
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // Refresh 토큰도 함께 갱신, Refresh Rotate
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
