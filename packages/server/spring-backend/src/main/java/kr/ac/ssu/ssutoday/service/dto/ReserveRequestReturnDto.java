/**
 * @filename    : ReserveRequestReturnDto.java
 * @description : ReserveService reserve request return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.ReserveRequestResponseDto;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class ReserveRequestReturnDto {
    private Integer idx;

    @NotNull
    public ReserveRequestResponseDto toReserveRequestResponseDto(){
        return ReserveRequestResponseDto.builder()
                .idx(this.idx)
                .build();
    }
}
