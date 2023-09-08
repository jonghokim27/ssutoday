/**
 * @filename    : StudentLoginReturnDto.java
 * @description : StudentService login return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.StudentLoginResponseDto;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class StudentLoginReturnDto {
    private String accessToken;
    private String refreshToken;
    private Integer studentId;
    private String name;
    private String major;

    @NotNull
    public StudentLoginResponseDto toStudentLoginResponseDto(){
        return StudentLoginResponseDto.builder()
                .accessToken(this.accessToken)
                .refreshToken(this.refreshToken)
                .studentId(this.studentId)
                .name(this.name)
                .major(this.major)
                .build();
    }
}
