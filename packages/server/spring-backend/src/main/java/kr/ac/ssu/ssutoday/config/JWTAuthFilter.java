/**
 * @filename    : JWTAuthFilter.java
 * @description : Custom auth filter for JSON Web Tokens
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.ssu.ssutoday.service.StudentService;
import kr.ac.ssu.ssutoday.service.dto.StudentValidateParamDto;
import kr.ac.ssu.ssutoday.service.dto.StudentValidateReturnDto;
import kr.ac.ssu.ssutoday.util.HTTPRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
public class JWTAuthFilter extends OncePerRequestFilter {
    /**
     * DI
     */
    private final StudentService studentService;
    private final HTTPRequestUtil httpRequestUtil;

    /**
     * Do filter
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<String> accessToken = httpRequestUtil.getAccessToken(request);
            Optional<String> refreshToken = httpRequestUtil.getRefreshToken(request);
            if (accessToken.isEmpty()) {
                throw new Exception("No access token provided.");
            }
            if (refreshToken.isEmpty()) {
                throw new Exception("No refresh token provided.");
            }

            StudentValidateParamDto studentValidateParamDto = StudentValidateParamDto.builder()
                    .accessToken(accessToken.get())
                    .refreshToken(refreshToken.get())
                    .build();

            StudentValidateReturnDto studentValidateReturnDto = studentService.validateStudent(studentValidateParamDto);
            if(studentValidateReturnDto.getAccessToken().isPresent()){
                response.addHeader("Access-Token", studentValidateReturnDto.getAccessToken().get());
            }
            if(studentValidateReturnDto.getRefreshToken().isPresent()){
                response.addHeader("Refresh-Token", studentValidateReturnDto.getRefreshToken().get());
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(studentValidateReturnDto.getStudent(), "", List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        filterChain.doFilter(request, response);
    }

}
