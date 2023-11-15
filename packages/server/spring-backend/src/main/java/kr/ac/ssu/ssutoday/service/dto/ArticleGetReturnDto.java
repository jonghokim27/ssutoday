/**
 * @filename    : ArticleGetReturnDto.java
 * @description : ArticleService get return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.ArticleGetResponseDto;
import kr.ac.ssu.ssutoday.entity.Article;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class ArticleGetReturnDto {
    private Article article;

    @NotNull
    public ArticleGetResponseDto toArticleGetResponseDto(){
        return ArticleGetResponseDto.builder()
                .article(this.article)
                .build();
    }
}
