/**
 * @filename    : UsaintAuthReturnDto.java
 * @description : AuthService uSaintAuth return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class UsaintAuthReturnDto {
    private Integer id;
    private String name;
    private String major;
    private String status;

    @NotNull
    public StudentLoginParamDto toStudentLoginParamDto(){
        return StudentLoginParamDto.builder()
                .id(this.id)
                .name(this.name)
                .major(this.major)
                .build();
    }
}
