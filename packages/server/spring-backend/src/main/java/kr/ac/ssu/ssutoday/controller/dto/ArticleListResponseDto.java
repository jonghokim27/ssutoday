/**
 * @filename    : ArticleListResponseDto.java
 * @description : ArticleController list response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import kr.ac.ssu.ssutoday.entity.Article;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticleListResponseDto {
    List<Article> articles;
    Integer totalPages;
}
