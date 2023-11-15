/**
 * @filename    : DeviceGetOptionRequestDto.java
 * @description : DeviceController get option request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.dto.DeviceGetOptionParamDto;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class DeviceGetOptionRequestDto {
    @NotEmpty(message = "osType is empty.")
    @Pattern(regexp = "ios|android", message = "Invalid osType.")
    private String osType;

    @NotEmpty(message = "uuid is empty.")
    private String uuid;

    @NotNull
    public DeviceGetOptionParamDto toDeviceGetOptionParamDto(@NotNull Student student){
        return DeviceGetOptionParamDto.builder()
                .student(student)
                .osType(this.osType)
                .uuid(this.uuid)
                .build();
    }
}
