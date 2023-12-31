/**
 * @filename    : ArticleGetParamDto.java
 * @description : ArticleService get param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArticleGetParamDto {
    private Integer idx;
}
