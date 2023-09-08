/**
 * @filename    : ReserveStatusResponseDto.java
 * @description : ReserveController reserve status DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveStatusResponseDto {
    private Integer status;
}