package com.khemiri.InternManager.interceptors;

import com.khemiri.InternManager.dto.responses.TokenPayloadResponse;
import com.khemiri.InternManager.utils.JwtManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class IdentityVerificationInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtManager jwtManager;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getTokenFromCookies(request.getCookies());

        if (token == null || !isValidToken(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token not valid");
            return false;
        }
        return true;
    }

    private String getTokenFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isValidToken(String token) {
        TokenPayloadResponse payloadResponse = jwtManager.validateJwtToken(token);
        return payloadResponse != null;
    }
}
