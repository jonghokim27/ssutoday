/**
 * @filename    : GlobalControllerAdvice.java
 * @description : Global Controller Advice
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice<T> implements ResponseBodyAdvice<T> {
    /**
     * DI
     */
    private final StatusCode statusCode;

    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * Before body write
     */
    @Override
    public T beforeBodyWrite(T body,
                             @NotNull MethodParameter returnType,
                             @NotNull MediaType selectedContentType,
                             @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                             @NotNull ServerHttpRequest request,
                             @NotNull ServerHttpResponse response) {
        if (body instanceof CommonResponse) {
            if (((CommonResponse) body).getStatusCode().startsWith("SSU2"))
                response.setStatusCode(HttpStatus.OK);
            else if (((CommonResponse) body).getStatusCode().startsWith("SSU4"))
                response.setStatusCode(HttpStatus.BAD_REQUEST);
            else if (((CommonResponse) body).getStatusCode().startsWith("SSU5"))
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

            return body;
        } else {
            log.error(body.toString());

            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            CommonResponse commonResponse = new CommonResponse(statusCode.SSU5000, null, statusCode.SSU5000_MSG);
            return (T) commonResponse;
        }
    }

    /**
     * Message not readable -> SSU4000 (HTTP 400)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<CommonResponse> HandlerHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.debug(e.getMessage(), e);

        CommonResponse res = new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * Argument not valid -> SSU4000 (HTTP 400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<CommonResponse> HandlerMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.debug(e.getMessage(), e);

        CommonResponse res = new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * Media type not supported -> SSU4000 (HTTP 400)
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<CommonResponse> HandlerHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, HttpServletRequest request) {
        log.debug(e.getMessage(), e);

        CommonResponse res = new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * No handler found -> SSU4004 (HTTP 400)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<CommonResponse> HandlerNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.debug(e.getMessage(), e);

        CommonResponse res = new CommonResponse(statusCode.SSU4004, null, statusCode.SSU4004_MSG);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * Method not supported -> SSU4004 (HTTP 400)
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<CommonResponse> HandlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.debug(e.getMessage(), e);

        CommonResponse res = new CommonResponse(statusCode.SSU4004, null, statusCode.SSU4004_MSG);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * Any unhandled expections -> SSU5000 (HTTP 500)
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse> HandlerException(Exception e, HttpServletRequest request) {
        log.error(e.getMessage(), e);

        CommonResponse res = new CommonResponse(statusCode.SSU5000, null, statusCode.SSU5000_MSG);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}