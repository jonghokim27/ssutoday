/**
 * @filename    : SSOToken.java
 * @description : SSOToken entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@RedisHash(value = "ssoToken", timeToLive = 60)
public class SSOToken {
    @Id
    private String ssoToken;
    private String clientId;
    private Integer studentId;
    private String name;
    private String major;
}
