/**
 * @filename    : StudentValidateReturnDto.java
 * @description : StudentService validate return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Builder
@Data
public class StudentValidateReturnDto {
    private Student student;
    private Optional<String> accessToken;
    private Optional<String> refreshToken;
}
