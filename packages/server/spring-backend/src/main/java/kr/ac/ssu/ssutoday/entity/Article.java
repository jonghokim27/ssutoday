/**
 * @filename    : Article.java
 * @description : Article entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Article {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idx", nullable = false)
    private Integer idx;
    @Basic
    @Column(name = "provider", nullable = false)
    private String provider;
    @Basic
    @Column(name = "articleNo", nullable = false, length = 200)
    private String articleNo;
    @Basic
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    @Basic
    @Column(name = "content", nullable = true, length = -1)
    private String content;
    @Basic
    @Column(name = "url", nullable = false, length = 200)
    private String url;
    @Basic
    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "updatedAt", nullable = true)
    private Timestamp updatedAt;
}
