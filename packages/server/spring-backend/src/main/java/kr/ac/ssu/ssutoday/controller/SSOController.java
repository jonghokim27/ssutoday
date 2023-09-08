/**
 * @filename    : SSOController.java
 * @description : REST API controller for /sso
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import kr.ac.ssu.ssutoday.controller.dto.SSOGenerateTokenRequestDto;
import kr.ac.ssu.ssutoday.controller.dto.SSOValidateTokenRequestDto;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.exception.AuthFailedException;
import kr.ac.ssu.ssutoday.service.SSOService;
import kr.ac.ssu.ssutoday.service.dto.SSOGenerateTokenReturnDto;
import kr.ac.ssu.ssutoday.service.dto.SSOValidateTokenParamDto;
import kr.ac.ssu.ssutoday.service.dto.SSOValidateTokenReturnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping("/sso")
@RequiredArgsConstructor
@Slf4j
public class SSOController {
    /**
     * DI
     */
    private final SSOService ssoService;
    private final StatusCode statusCode;

    /**
     * Generate SSO Token (15)
     * /sso/generateToken
     * @param ssoGenerateTokenRequestDto generate params
     * @return generate result (SSOGenerateTokenResponseDto)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/generateToken")
    @ResponseBody
    public CommonResponse generateToken(@NotNull @Valid @RequestBody SSOGenerateTokenRequestDto ssoGenerateTokenRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        try {
            SSOGenerateTokenReturnDto ssoGenerateTokenReturnDto = ssoService.ssoGenerateToken(ssoGenerateTokenRequestDto.toSSOGenerateTokenParamDto(student));
            return new CommonResponse(statusCode.SSU2150, ssoGenerateTokenReturnDto.toSSOGenerateTokenResponseDto(), statusCode.SSU2150_MSG);
        } catch (Exception e){
            return new CommonResponse(statusCode.SSU4150, null, statusCode.SSU4150_MSG);
        }

    }

    /**
     * Validate SSO Token (16)
     * /sso/validateToken
     * @param ssoValidateTokenRequestDto validate params
     * @return validate result (SSOValidateTokenResponseDto)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/validateToken")
    @ResponseBody
    public CommonResponse validateToken(@NotNull @Valid @RequestBody SSOValidateTokenRequestDto ssoValidateTokenRequestDto, @NotNull HttpServletRequest request){
        Optional<String> clientIdOptional = Optional.ofNullable(request.getHeader("X-SSUtoday-Client-Id"));
        Optional<String> clientSecretOptional = Optional.ofNullable(request.getHeader("X-SSUtoday-Client-Secret"));

        if(clientIdOptional.isEmpty() || clientSecretOptional.isEmpty()){
            return new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        }

        SSOValidateTokenParamDto ssoValidateTokenParamDto = SSOValidateTokenParamDto.builder()
                .ssoToken(ssoValidateTokenRequestDto.getSsoToken())
                .clientId(clientIdOptional.get())
                .clientSecret(clientSecretOptional.get())
                .build();

        try{
            SSOValidateTokenReturnDto ssoValidateTokenReturnDto = ssoService.ssoValidateToken(ssoValidateTokenParamDto);
            return new CommonResponse(statusCode.SSU2160, ssoValidateTokenReturnDto.toSSOValidateTokenResponseDto(), statusCode.SSU2160_MSG);
        } catch(AuthFailedException e){
            return new CommonResponse(statusCode.SSU4001, null, statusCode.SSU4001_MSG);
        } catch(Exception e){
            return new CommonResponse(statusCode.SSU4160, null, statusCode.SSU4160_MSG);
        }
    }
}
