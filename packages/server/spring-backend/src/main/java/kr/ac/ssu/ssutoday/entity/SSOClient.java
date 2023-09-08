/**
 * @filename    : SSOClient.java
 * @description : SSOClient entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SSOClient {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;
    @Basic
    @Column(name = "secret", nullable = false, length = 500)
    private String secret;
    @Basic
    @Column(name = "callbackUrl", nullable = false, length = 200)
    private String callbackUrl;
}
