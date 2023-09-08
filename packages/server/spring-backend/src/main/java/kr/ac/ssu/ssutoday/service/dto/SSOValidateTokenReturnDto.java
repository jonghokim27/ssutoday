/**
 * @filename    : SSOValidateTokenReturnDto.java
 * @description : SSOService valite token return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.SSOValidateTokenResponseDto;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class SSOValidateTokenReturnDto {
    private Integer studentId;
    private String name;
    private String major;

    @NotNull
    public SSOValidateTokenResponseDto toSSOValidateTokenResponseDto(){
        return SSOValidateTokenResponseDto.builder()
                .studentId(this.studentId)
                .name(this.name)
                .major(this.major)
                .build();
    }
}
