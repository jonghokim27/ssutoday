/**
 * @filename    : ReserveListResponseDto.java
 * @description : ReserveController list response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import kr.ac.ssu.ssutoday.entity.Reserve;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReserveListResponseDto {
    List<Reserve> reserves;
    Integer totalPages;
}
