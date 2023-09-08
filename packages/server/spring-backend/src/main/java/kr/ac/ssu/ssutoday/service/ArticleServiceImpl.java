package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.entity.Article;
import kr.ac.ssu.ssutoday.repository.ArticleRepository;
import kr.ac.ssu.ssutoday.service.dto.ArticleGetParamDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleGetReturnDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleListParamDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleListReturnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    /**
     * DI
     */
    private final ArticleRepository articleRepository;

    /**
     * Article list
     * @param articleListParamDto list params
     * @return list result (ArticleListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    @Override
    public ArticleListReturnDto articleList(ArticleListParamDto articleListParamDto) {
        final int pageSize = 20;

        PageRequest pageRequest;
        if(articleListParamDto.getOrderBy().equals("DESC")) {
            pageRequest = PageRequest.of(articleListParamDto.getPage(), pageSize, Sort.by("createdAt", "idx").descending());
        } else{
            pageRequest = PageRequest.of(articleListParamDto.getPage(), pageSize, Sort.by("createdAt", "idx").ascending());
        }

        Page<Article> articlePage = articleRepository.findAllArticles(
                articleListParamDto.getProvider(),
                "%" + articleListParamDto.getSearch() + "%",
                pageRequest
        );

        return ArticleListReturnDto.builder()
                .articles(articlePage.get().toList())
                .totalPages(articlePage.getTotalPages())
                .build();
    }

    /**
     * Article get
     * @param articleGetParamDto get params
     * @return get result (ArticleGetReturnDto)
     * @throws Exception thrown when no article with matching idx found
     * @author jonghokim27
     */
    @NotNull
    @Override
    public ArticleGetReturnDto articleGet(ArticleGetParamDto articleGetParamDto) throws Exception {
        Optional<Article> articleOptional = articleRepository.findById(articleGetParamDto.getIdx());
        if(articleOptional.isEmpty()){
            log.debug("No article found with idx {}", articleGetParamDto.getIdx());
            throw new Exception();
        }

        return ArticleGetReturnDto.builder()
                .article(articleOptional.get())
                .build();
    }
}
