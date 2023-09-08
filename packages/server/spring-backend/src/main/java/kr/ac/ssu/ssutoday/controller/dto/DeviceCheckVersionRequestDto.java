/**
 * @filename    : DeviceCheckVersionRequestDto.java
 * @description : DeviceController device check request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import kr.ac.ssu.ssutoday.service.dto.DeviceCheckVersionParamDto;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class DeviceCheckVersionRequestDto {
    @NotEmpty(message = "osType is empty.")
    @Pattern(regexp = "ios|android", message = "Invalid osType.")
    private String osType;

    @NotEmpty(message = "version is empty.")
    @Pattern(regexp = "[0-9].[0-9].[0-9]", message = "Invalid version.")
    private String version;

    @NotNull
    public DeviceCheckVersionParamDto toDeviceCheckVersionParamDto(){
        return DeviceCheckVersionParamDto.builder()
                .osType(this.osType)
                .version(this.version)
                .build();
    }
}
