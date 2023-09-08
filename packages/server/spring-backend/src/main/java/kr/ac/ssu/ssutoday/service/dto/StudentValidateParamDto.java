/**
 * @filename    : StudentValidateParamDto.java
 * @description : StudentService validate param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentValidateParamDto {
    private String accessToken;
    private String refreshToken;
}
