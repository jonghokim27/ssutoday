/**
 * @filename    : ReserveCancelReturnDto.java
 * @description : ReserveService cancel return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReserveCancelReturnDto {
    private Integer status;
}
