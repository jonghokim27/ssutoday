/**
 * @filename    : DeviceCheckVersionParamDto.java
 * @description : DeviceService check version param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeviceCheckVersionParamDto {
    private String osType;
    private String version;
}
