/**
 * @filename    : ReserveListRequestDto.java
 * @description : ReserveController list request dto
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.*;
import kr.ac.ssu.ssutoday.service.dto.ArticleListParamDto;
import kr.ac.ssu.ssutoday.service.dto.ReserveListParamDto;
import lombok.Data;

@Data
public class ReserveListRequestDto {
    @NotNull(message = "page is null.")
    @Min(value = 0)
    private Integer page;

    @NotNull(message = "type is null.")
    @Min(value = 0)
    @Max(value = 1)
    private Integer type;

    @org.jetbrains.annotations.NotNull
    public ReserveListParamDto toReserveListParamDto(@org.jetbrains.annotations.NotNull Integer studentId){
        return ReserveListParamDto.builder()
                .studentId(studentId)
                .page(this.page)
                .type(this.type)
                .build();
    }
}
