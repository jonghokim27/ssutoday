/**
 * @filename    : APIResponseDto.java
 * @description : API Response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.provider.dto;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class APIResponseDto {
    @NotNull
    private String body;

    @NotNull
    private Map<String, List<String>> headers;
}
