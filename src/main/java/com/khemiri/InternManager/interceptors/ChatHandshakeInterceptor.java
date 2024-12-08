package com.khemiri.InternManager.interceptors;
import com.khemiri.InternManager.dto.responses.TokenPayloadResponse;
import com.khemiri.InternManager.utils.JwtManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import java.util.List;
import java.util.Map;
@Component
public class ChatHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private final JwtManager jwtManager;

    public ChatHandshakeInterceptor(JwtManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception
    {
        HttpHeaders headers = request.getHeaders();
        String token = extractTokenFromCookies(headers);
        if(isValidToken(token)) {
            attributes.put("token", token);
            return true;
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;

    }

    private String extractTokenFromCookies(HttpHeaders headers) {
        List<String> cookies = headers.get(HttpHeaders.COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("token=")) {
                    return cookie.substring("token=".length());
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

