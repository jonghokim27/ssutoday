/**
 * @filename    : JWTPayloadVO.java
 * @description : JWT Payload Value Object
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.vo;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class JWTPayloadVo {
    @NotNull
    private Integer studentId;
    @NotNull
    private String name;
    @NotNull
    private String major;
}
