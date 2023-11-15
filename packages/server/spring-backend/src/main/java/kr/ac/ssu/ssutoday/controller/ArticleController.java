/**
 * @filename    : ArticleController.java
 * @description : REST API controller for /article
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller;

import jakarta.validation.Valid;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import kr.ac.ssu.ssutoday.controller.dto.ArticleGetRequestDto;
import kr.ac.ssu.ssutoday.controller.dto.ArticleListRequestDto;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.ArticleService;
import kr.ac.ssu.ssutoday.service.dto.ArticleGetReturnDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleListReturnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/article")
@RequiredArgsConstructor
@Slf4j
public class ArticleController {
    /**
     * DI
     */
    private final ArticleService articleService;
    private final StatusCode statusCode;

    /**
     * Article list (06)
     * /article/list
     * @param articleListRequestDto article list params
     * @return article list result (ArticleListResponseDto)
     * @author jonghokim27
     */
    @NotNull
    @PostMapping("/list")
    @ResponseBody
    public CommonResponse list(@NotNull @Valid @RequestBody ArticleListRequestDto articleListRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        List<String> selectedProviders = articleListRequestDto.getProvider();
        for(int i=0; i<selectedProviders.size(); i++){
            if(selectedProviders.get(i).equals("major")){
                selectedProviders.set(i, student.getMajor());
            }
        }

        ArticleListReturnDto articleListReturnDto = articleService.articleList(articleListRequestDto.toArticleListParamDto());

        return new CommonResponse(statusCode.SSU2060, articleListReturnDto.toArticleListResponseDto(), statusCode.SSU2060_MSG);
    }

    /**
     * Article get (08)
     * /article/get
     * @param articleGetRequestDto article get params
     * @return article (Article)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/get")
    @ResponseBody
    public CommonResponse get(@NotNull @Valid @RequestBody ArticleGetRequestDto articleGetRequestDto){
        ArticleGetReturnDto articleGetReturnDto;
        try {
            articleGetReturnDto = articleService.articleGet(articleGetRequestDto.toArticleGetParamDto());
        } catch(Exception e){
            return new CommonResponse(statusCode.SSU4080, null, statusCode.SSU4080_MSG);
        }

        return new CommonResponse(statusCode.SSU2080, articleGetReturnDto.toArticleGetResponseDto(), statusCode.SSU2080_MSG);
    }
}
