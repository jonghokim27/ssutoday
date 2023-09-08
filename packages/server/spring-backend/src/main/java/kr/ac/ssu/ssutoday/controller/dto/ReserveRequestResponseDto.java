/**
 * @filename    : ReserveRequestResponseDto.java
 * @description : ReserveController reserve response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReserveRequestResponseDto {
    private Integer idx;
}