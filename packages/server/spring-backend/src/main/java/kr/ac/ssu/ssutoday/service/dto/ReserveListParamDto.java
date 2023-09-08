/**
 * @filename    : ReserveListParamDto.java
 * @description : ReserveService list param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReserveListParamDto {
    private Integer studentId;
    private Integer page;
    private Integer type;
}
