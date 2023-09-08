/**
 * @filename    : ReserveStatusParamDto.java
 * @description : ReserveService reserve status param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReserveStatusParamDto {
    private Integer studentId;
    private Integer idx;
}
