/**
 * @filename    : APIRequestDto.java
 * @description : API Request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.provider.dto;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Builder
@Data
public class APIRequestDto {
    @NotNull
    private String url;

    @NotNull
    private Map<String, String> headers;
}
