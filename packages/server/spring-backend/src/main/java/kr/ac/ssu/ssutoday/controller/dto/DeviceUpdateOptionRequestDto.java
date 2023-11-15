/**
 * @filename    : DeviceUpdateOptionRequestDto.java
 * @description : DeviceController update option request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.dto.DeviceUpdateOptionParamDto;
import lombok.Data;

@Data
public class DeviceUpdateOptionRequestDto {
    @NotEmpty(message = "osType is empty.")
    @Pattern(regexp = "ios|android", message = "Invalid osType.")
    private String osType;

    @NotEmpty(message = "uuid is empty.")
    private String uuid;
    @NotEmpty(message = "option is empty.")
    @Pattern(regexp = "notice|reserve|lms", message = "Invalid option.")
    private String option;
    @NotNull
    private Boolean value;

    @org.jetbrains.annotations.NotNull
    public DeviceUpdateOptionParamDto toDeviceUpdateOptionParamDto(@org.jetbrains.annotations.NotNull Student student){
        return DeviceUpdateOptionParamDto.builder()
                .student(student)
                .osType(this.osType)
                .uuid(this.uuid)
                .option(this.option)
                .value(this.value)
                .build();
    }
}
