/**
 * @filename    : ReserveListReturnDto.java
 * @description : ReserveService list return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.ReserveListResponseDto;
import kr.ac.ssu.ssutoday.vo.ReserveDetailVo;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Builder
@Data
public class ReserveListReturnDto {
    private List<ReserveDetailVo> reserves;
    private Integer totalPages;

    @NotNull
    public ReserveListResponseDto toReserveListResponseDto(){
        return ReserveListResponseDto.builder()
                .reserves(this.reserves)
                .totalPages(this.totalPages)
                .build();
    }
}
