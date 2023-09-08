/**
 * @filename    : SSOGenerateTokenParamDto.java
 * @description : SSOService generate token param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SSOGenerateTokenParamDto {
    private Student student;
    private String clientId;
}
