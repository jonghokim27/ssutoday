/**
 * @filename    : DeviceRegisterParamDto.java
 * @description : DeviceService register param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeviceRegisterParamDto {
    private Student student;
    private String osType;
    private String uuid;
    private String pushToken;
}
