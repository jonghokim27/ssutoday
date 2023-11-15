/**
 * @filename    : DeviceGetOptionResponseDto.java
 * @description : DeviceController get option response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceGetOptionResponseDto {
    private Boolean notice;
    private Boolean reserve;
    private Boolean lms;
}
