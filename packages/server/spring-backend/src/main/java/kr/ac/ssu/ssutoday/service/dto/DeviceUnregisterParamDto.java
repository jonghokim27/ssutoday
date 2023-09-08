/**
 * @filename    : DeviceUnregisterParamDto.java
 * @description : DeviceService unregister param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeviceUnregisterParamDto {
    private Student student;
    private String osType;
    private String uuid;
}
