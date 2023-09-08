/**
 * @filename    : SSOGenerateTokenResponse.java
 * @description : SSOController generate token response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SSOGenerateTokenResponseDto {
    private String ssoToken;
    private String callbackUrl;
}
