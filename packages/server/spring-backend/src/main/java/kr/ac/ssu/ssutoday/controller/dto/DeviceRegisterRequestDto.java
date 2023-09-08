/**
 * @filename    : DeviceRegisterRequestDto.java
 * @description : DeviceController register request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.dto.DeviceRegisterParamDto;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class DeviceRegisterRequestDto {
    @NotEmpty(message = "osType is empty.")
    @Pattern(regexp = "ios|android", message = "Invalid osType.")
    private String osType;

    @NotEmpty(message = "uuid is empty.")
    private String uuid;

    @NotEmpty(message = "pushToken is empty.")
    private String pushToken;

    @NotNull
    public DeviceRegisterParamDto toDeviceRegisterParamDto(@NotNull Student student){
        return DeviceRegisterParamDto.builder()
                .student(student)
                .osType(this.osType)
                .uuid(this.uuid)
                .pushToken(this.pushToken)
                .build();
    }
}
