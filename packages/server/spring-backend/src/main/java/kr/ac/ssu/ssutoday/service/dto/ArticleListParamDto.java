/**
 * @filename    : ArticleListParamDto.java
 * @description : ArticleService list param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ArticleListParamDto {
    private Integer page;
    private String orderBy;
    private String search;
    private List<String> provider;
}
