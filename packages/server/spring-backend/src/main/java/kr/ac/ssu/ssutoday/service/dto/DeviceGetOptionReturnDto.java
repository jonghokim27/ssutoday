/**
 * @filename    : DeviceGetOptionReturnDto.java
 * @description : DeviceService get option return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.controller.dto.DeviceGetOptionResponseDto;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
public class DeviceGetOptionReturnDto {
    private Boolean notice;
    private Boolean reserve;
    private Boolean lms;

    @NotNull
    public DeviceGetOptionResponseDto toDeviceGetOptionResponseDto(){
        return DeviceGetOptionResponseDto.builder()
                .notice(this.notice)
                .reserve(this.reserve)
                .lms(this.lms)
                .build();
    }
}
