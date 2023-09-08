/**
 * @filename    : StudentLogoutReturnDto.java
 * @description : StudentService logout return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentLogoutParamDto {
    private Student student;
    private String refreshToken;
}
