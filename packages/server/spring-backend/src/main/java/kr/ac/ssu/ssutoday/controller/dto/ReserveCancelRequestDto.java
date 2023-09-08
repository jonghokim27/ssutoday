/**
 * @filename    : ReserveCancelRequestDto.java
 * @description : ReserveController cancel request dto
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.ssu.ssutoday.service.dto.ReserveCancelParamDto;
import lombok.Data;

@Data
public class ReserveCancelRequestDto {
    @NotNull(message = "idx is null.")
    private Integer idx;

    @org.jetbrains.annotations.NotNull
    public ReserveCancelParamDto toReserveCancelParamDto(@org.jetbrains.annotations.NotNull Integer studentId){
        return ReserveCancelParamDto.builder()
                .studentId(studentId)
                .idx(this.idx)
                .build();
    }
}
