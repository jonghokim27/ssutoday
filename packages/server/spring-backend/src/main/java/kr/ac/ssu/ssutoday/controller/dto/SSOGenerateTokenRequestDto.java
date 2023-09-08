/**
 * @filename    : SSOGenerateTokenRequest.java
 * @description : SSOController generate token request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.*;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.dto.SSOGenerateTokenParamDto;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class SSOGenerateTokenRequestDto {
    @NotEmpty(message = "clientId is empty.")
    private String clientId;

    @NotNull
    public SSOGenerateTokenParamDto toSSOGenerateTokenParamDto(@NotNull Student student){
        return SSOGenerateTokenParamDto.builder()
                .student(student)
                .clientId(this.clientId)
                .build();
    }
}
