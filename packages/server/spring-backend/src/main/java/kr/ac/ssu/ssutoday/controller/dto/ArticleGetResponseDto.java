/**
 * @filename    : ArticleGetResponseDto.java
 * @description : ArticleController get response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import kr.ac.ssu.ssutoday.entity.Article;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArticleGetResponseDto {
    Article article;
}
