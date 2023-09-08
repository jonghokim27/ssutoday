/**
 * @filename    : ReserveStatusRequestDto.java
 * @description : ReserveController reserve status DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.*;
import kr.ac.ssu.ssutoday.service.dto.ReserveRequestParamDto;
import kr.ac.ssu.ssutoday.service.dto.ReserveStatusParamDto;
import lombok.Data;

import java.sql.Date;

@Data
public class ReserveStatusRequestDto {
    @NotNull(message = "idx is empty.")
    private Integer idx;

    @org.jetbrains.annotations.NotNull
    public ReserveStatusParamDto toReserveStatusParamDto(@org.jetbrains.annotations.NotNull Integer studentId){
        return ReserveStatusParamDto.builder()
                .studentId(studentId)
                .idx(this.idx)
                .build();
    }
}
