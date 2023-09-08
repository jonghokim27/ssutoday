package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ArticleRepository extends CrudRepository<Article, Integer> {
    @Query(value = "select * from Article " +
            "where (provider & ?1) > 0 " +
            "and (title like ?2 or content like ?2)",
            countQuery = "select * from Article " +
                    "where (provider & ?1) > 0 " +
                    "and (title like ?2 or content like ?2)",
            nativeQuery = true)
    Page<Article> findAllArticles(Integer provider, String search, PageRequest pageRequest);
}
