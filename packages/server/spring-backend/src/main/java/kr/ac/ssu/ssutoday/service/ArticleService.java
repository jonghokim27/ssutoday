/**
 * @filename    : ArticleService.java
 * @description : Article Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.service.dto.ArticleGetParamDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleGetReturnDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleListParamDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleListReturnDto;
import org.jetbrains.annotations.NotNull;

public interface ArticleService {
    /**
     * Article list
     * @param articleListParamDto list params
     * @return list result (ArticleListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    ArticleListReturnDto articleList(ArticleListParamDto articleListParamDto);

    /**
     * Article get
     * @param articleGetParamDto get params
     * @return get result (ArticleGetReturnDto)
     * @throws Exception thrown when no article with matching idx found
     * @author jonghokim27
     */
    @NotNull
    ArticleGetReturnDto articleGet(ArticleGetParamDto articleGetParamDto) throws Exception;
}
