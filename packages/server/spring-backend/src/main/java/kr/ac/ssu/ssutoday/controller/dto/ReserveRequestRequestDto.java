/**
 * @filename    : ReserveRequestRequestDto.java
 * @description : ReserveController reserve request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.*;
import kr.ac.ssu.ssutoday.service.dto.ReserveRequestParamDto;
import lombok.Data;

import java.sql.Date;

@Data
public class ReserveRequestRequestDto {
    @NotEmpty(message = "roomNo is empty.")
    @Pattern(regexp = "(2[a-c]|5a)", message = "Invalid roomNo.")
    private String roomNo;

    @NotEmpty(message = "date is empty.")
    @Pattern(regexp = "202[3-9]-(0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-9]|3[0-1])", message = "Invalid date.")
    private String date;

    @NotNull(message = "startBlock is null.")
    @Min(value = 12)
    @Max(value = 43)
    private Integer startBlock;

    @NotNull(message = "endBlock is null.")
    @Min(value = 12)
    @Max(value = 43)
    private Integer endBlock;

    @org.jetbrains.annotations.NotNull
    public ReserveRequestParamDto toReserveRequestParamDto(@org.jetbrains.annotations.NotNull Integer studentId){
        return ReserveRequestParamDto.builder()
                .studentId(studentId)
                .roomNo(this.roomNo)
                .date(Date.valueOf(this.date))
                .startBlock(this.startBlock)
                .endBlock(this.endBlock)
                .build();
    }
}
