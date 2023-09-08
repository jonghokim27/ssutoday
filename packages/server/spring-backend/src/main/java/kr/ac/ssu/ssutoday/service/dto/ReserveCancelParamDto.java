/**
 * @filename    : ReserveCancelParamDto.java
 * @description : ReserveService cancel param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReserveCancelParamDto {
    private Integer studentId;
    private Integer idx;
}
