package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.entity.Article;
import kr.ac.ssu.ssutoday.service.dto.ArticleListParamDto;
import kr.ac.ssu.ssutoday.service.dto.ArticleListReturnDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ArticleServiceTest {
    @Autowired
    private ArticleService articleService;

    Logger log = LoggerFactory.getLogger(StudentServiceTest.class);

    @Test
    public void getArticle(){
        ArticleListParamDto articleListParamDto = ArticleListParamDto.builder()
                .provider(31)
                .search("")
                .orderBy("DESC")
                .page(9)
                .build();

        ArticleListReturnDto articleListReturnDto = articleService.articleList(articleListParamDto);

        log.info(String.valueOf(articleListReturnDto.getTotalPages()));
        for(Article article : articleListReturnDto.getArticles()){
            log.info(article.getArticleNo() + ": " + article.getTitle());
        }
    }

}
