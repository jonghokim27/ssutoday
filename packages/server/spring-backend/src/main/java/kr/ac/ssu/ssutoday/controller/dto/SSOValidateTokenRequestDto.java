/**
 * @filename    : SSOValidateTokenRequest.java
 * @description : SSOController validate token request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SSOValidateTokenRequestDto {
    @NotEmpty(message = "ssoToken is empty.")
    private String ssoToken;
}
