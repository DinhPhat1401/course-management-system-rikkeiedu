package edu.rk.camel.coursemanagementsystem.security;

import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import edu.rk.camel.coursemanagementsystem.model.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        ApiResponse<Void> apiResponse = ApiResponse.error("UNAUTHORIZED", "Vui lòng đăng nhập để tiếp tục hoặc token không hợp lệ", HttpServletResponse.SC_UNAUTHORIZED);
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        
        String json = String.format(
            "{\"success\":false,\"status_code\":401,\"error_code\":\"UNAUTHORIZED\",\"message\":\"Vui lòng đăng nhập để tiếp tục hoặc token không hợp lệ\",\"timestamp\":\"%s\"}",
            java.time.Instant.now().toString()
        );
        
        OutputStream out = response.getOutputStream();
        out.write(json.getBytes("UTF-8"));
        out.flush();
    }
}
