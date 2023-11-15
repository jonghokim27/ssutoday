/**
 * @filename    : ReserveListResponseDto.java
 * @description : ReserveController list response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import kr.ac.ssu.ssutoday.vo.ReserveDetailVo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReserveListResponseDto {
    private List<ReserveDetailVo> reserves;
    private Integer totalPages;
}
