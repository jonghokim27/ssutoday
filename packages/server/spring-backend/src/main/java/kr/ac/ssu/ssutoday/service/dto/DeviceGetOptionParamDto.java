/**
 * @filename    : DeviceGetOptionParamDto.java
 * @description : DeviceService get option param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeviceGetOptionParamDto {
    private Student student;
    private String osType;
    private String uuid;
}
