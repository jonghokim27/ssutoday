/**
 * @filename    : StudentLoginRequestDto.java
 * @description : StudentController login request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.ac.ssu.ssutoday.service.dto.UsaintAuthParamDto;
import lombok.Data;

@Data
public class StudentLoginRequestDto {
    @JsonProperty("sToken")
    @NotEmpty(message = "sToken is empty.")
    private String sToken;

    @JsonProperty("sIdno")
    @NotNull(message = "sIdno is null.")
    private Integer sIdno;

    @org.jetbrains.annotations.NotNull
    public UsaintAuthParamDto toUsaintAuthParamDto(){
        return UsaintAuthParamDto.builder()
                .sToken(this.sToken)
                .sIdno(this.sIdno)
                .build();
    }
}
