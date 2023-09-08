/**
 * @filename    : ClientKeyFilter.java
 * @description : Custom auth filter for Client Key
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import kr.ac.ssu.ssutoday.service.StudentService;
import kr.ac.ssu.ssutoday.service.dto.StudentValidateParamDto;
import kr.ac.ssu.ssutoday.service.dto.StudentValidateReturnDto;
import kr.ac.ssu.ssutoday.util.HTTPRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientKeyFilter extends OncePerRequestFilter {
    /**
     * DI
     */
    private final HTTPRequestUtil httpRequestUtil;
    private final StatusCode statusCode;

    /**
     * Client key from environment variable
     */
    @Value("${spring.client.key}")
    private String CLIENT_KEY;

    /**
     * Do filter
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            if(request.getRequestURI().equals("/sso/validateToken")){
                filterChain.doFilter(request, response);
                return;
            }

            Optional<String> clientKey = httpRequestUtil.getClientKey(request);
            if(clientKey.isEmpty()){
                throw new Exception("No client key provided.");
            }

            if(!clientKey.get().equals(CLIENT_KEY)){
                throw new Exception("Client key does not match.");
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("Client key validation failed.", e);

            response.setStatus(400);
            response.setContentType("application/json");
            CommonResponse res = new CommonResponse(statusCode.SSU4003, null, statusCode.SSU4003_MSG);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(res));
        }
    }

}
