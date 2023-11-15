/**
 * @filename    : StudentUpdateXnApiTokenParamDto.java
 * @description : StudentService update xnApiToken param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentUpdateXnApiTokenParamDto {
    private Student student;
    private String xnApiToken;
}
