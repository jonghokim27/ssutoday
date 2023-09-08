/**
 * @filename    : ArticleListRequestDto.java
 * @description : ArticleController list request dto
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.*;
import kr.ac.ssu.ssutoday.service.dto.ArticleListParamDto;
import lombok.Data;

@Data
public class ArticleListRequestDto {
    @NotNull(message = "page is null.")
    @Min(value = 0)
    private Integer page;

    @NotEmpty(message = "orderBy is null.")
    @Pattern(regexp = "ASC|DESC", message = "Invalid orderBy.")
    private String orderBy;

    @NotNull(message = "search is null.")
    private String search;

    @NotNull(message = "provider is null.")
    @Min(value = 0)
    @Max(value = 31)
    private Integer provider;

    @org.jetbrains.annotations.NotNull
    public ArticleListParamDto toArticleListParamDto(){
        return ArticleListParamDto.builder()
                .page(this.page)
                .orderBy(this.orderBy)
                .search(this.search)
                .provider(this.provider)
                .build();
    }
}
