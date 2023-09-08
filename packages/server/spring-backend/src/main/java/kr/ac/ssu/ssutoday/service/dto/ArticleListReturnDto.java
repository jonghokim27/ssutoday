/**
 * @filename    : ArticleListReturnDto.java
 * @description : ArticleService list return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.ssu.ssutoday.controller.dto.ArticleListResponseDto;
import kr.ac.ssu.ssutoday.entity.Article;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticleListReturnDto {
    List<Article> articles;
    Integer totalPages;

    @NotNull
    public ArticleListResponseDto toArticleListResponseDto(){
        return ArticleListResponseDto.builder()
                .articles(this.articles)
                .totalPages(this.totalPages)
                .build();
    }
}
