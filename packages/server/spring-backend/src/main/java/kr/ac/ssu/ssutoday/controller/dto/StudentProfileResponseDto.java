/**
 * @filename    : StudentProfileResponseDto.java
 * @description : StudentController profile response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentProfileResponseDto {
    private Integer studentId;
    private String name;
    private String major;
}
