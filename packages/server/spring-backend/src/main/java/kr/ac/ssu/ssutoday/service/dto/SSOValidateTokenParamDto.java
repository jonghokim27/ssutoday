/**
 * @filename    : SSOValidateTokenParamDto.java
 * @description : SSOService validate token param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SSOValidateTokenParamDto {
    private String clientId;
    private String clientSecret;
    private String ssoToken;
}
