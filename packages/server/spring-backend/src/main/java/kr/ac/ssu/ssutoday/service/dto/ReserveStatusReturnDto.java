/**
 * @filename    : ReserveStatusReturnDto.java
 * @description : ReserveService reserve status return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.ReserveStatusResponseDto;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class ReserveStatusReturnDto {
    private Integer status;

    @NotNull
    public ReserveStatusResponseDto toReserveStatusResponseDto(){
        return ReserveStatusResponseDto.builder()
                .status(this.status)
                .build();
    }
}
