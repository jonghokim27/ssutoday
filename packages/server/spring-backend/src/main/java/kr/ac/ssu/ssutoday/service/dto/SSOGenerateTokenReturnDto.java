/**
 * @filename    : SSOGenerateTokenReturnDto.java
 * @description : SSOService generate token return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.SSOGenerateTokenResponseDto;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class SSOGenerateTokenReturnDto {
    private String ssoToken;
    private String callbackUrl;

    @NotNull
    public SSOGenerateTokenResponseDto toSSOGenerateTokenResponseDto(){
        return SSOGenerateTokenResponseDto.builder()
                .ssoToken(this.ssoToken)
                .callbackUrl(this.callbackUrl)
                .build();
    }
}
