/**
 * @filename    : UsaintAuthParamDto.java
 * @description : AuthService uSaintAuth param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class UsaintAuthParamDto {
    @NotNull
    private String sToken;

    @NotNull
    private Integer sIdno;
}
