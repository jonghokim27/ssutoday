/**
 * @filename    : DeviceUnregisterRequestDto.java
 * @description : DeviceController unregister request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.dto.DeviceUnregisterParamDto;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class DeviceUnregisterRequestDto {
    @NotEmpty(message = "osType is empty.")
    @Pattern(regexp = "ios|android", message = "Invalid osType.")
    private String osType;

    @NotEmpty(message = "uuid is empty.")
    private String uuid;

    @NotNull
    public DeviceUnregisterParamDto toDeviceUnregisterParamDto(@NotNull Student student){
        return DeviceUnregisterParamDto.builder()
                .student(student)
                .osType(this.osType)
                .uuid(this.uuid)
                .build();
    }
}
