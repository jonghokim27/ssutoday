/**
 * @filename    : HTTPRequestUtil.java
 * @description : HTTP Request Util
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.util;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HTTPRequestUtil {
    /**
     * Get access token from HTTP Request
     * @param request HttpServletRequest
     * @return access token (optional)
     * @author jonghokim27
     */
    @NotNull
    public Optional<String> getAccessToken(@NotNull HttpServletRequest request) {
        final String HEADER_KEY = "Authorization";
        final String PREFIX = "Bearer ";

        Optional<String> token = Optional.ofNullable(request.getHeader(HEADER_KEY));

        if (token.isPresent() && token.get().startsWith(PREFIX)) {
            return Optional.of(token.get().substring(PREFIX.length()));
        }

        return Optional.empty();
    }

    /**
     * Get refresh token from HTTP Request
     * @param request HttpServletRequest
     * @return refresh token (optional)
     * @author jonghokim27
     */
    @NotNull
    public Optional<String> getRefreshToken(@NotNull HttpServletRequest request){
        final String HEADER_KEY = "Refresh-Token";
        return Optional.ofNullable(request.getHeader(HEADER_KEY));
    }

    /**
     * Get client key from HTTP Request
     * @param request HttpServletRequest
     * @return client key (optional)
     * @author jonghokim27
     */
    @NotNull
    public Optional<String> getClientKey(@NotNull HttpServletRequest request){
        final String HEADER_KEY = "Client-Key";
        return Optional.ofNullable(request.getHeader(HEADER_KEY));
    }
}
