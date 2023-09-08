/**
 * @filename    : StudentLoginResponseDto.java
 * @description : StudentController login response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentLoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private Integer studentId;
    private String name;
    private String major;
}
