/**
 * @filename    : StudentUpdateXnApiTokenRequest.java
 * @description : StudentController update xnApiToken request dto
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.dto.StudentUpdateXnApiTokenParamDto;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class StudentUpdateXnApiTokenRequestDto {
    private String xnApiToken;

    @NotNull
    public StudentUpdateXnApiTokenParamDto toStudentUpdateXnApiTokenParamDto(@NotNull Student student){
        return StudentUpdateXnApiTokenParamDto.builder()
                .student(student)
                .xnApiToken(this.xnApiToken)
                .build();
    }
}
