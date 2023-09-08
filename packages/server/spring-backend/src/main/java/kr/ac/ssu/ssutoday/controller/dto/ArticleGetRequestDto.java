/**
 * @filename    : ArticleGetRequestDto.java
 * @description : ArticleController get request dto
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.*;
import kr.ac.ssu.ssutoday.service.dto.ArticleGetParamDto;
import lombok.Data;

@Data
public class ArticleGetRequestDto {
    @NotNull(message = "idx is null.")
    @Min(value = 1)
    private Integer idx;

    @org.jetbrains.annotations.NotNull
    public ArticleGetParamDto toArticleGetParamDto(){
        return ArticleGetParamDto.builder()
                .idx(this.idx)
                .build();
    }
}
