/**
 * @filename    : RefreshToken.java
 * @description : RefreshToken entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash(value = "refreshToken", timeToLive = 3600 * 24 * 120L)
public class RefreshToken {
    @Id
    private String refreshToken;
    private String accessToken;
    private Integer studentId;
    private String name;
    private String major;
}
