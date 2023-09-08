/**
 * @filename    : RoomGetParamDto.java
 * @description : RoomService get param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RoomGetParamDto {
    private String roomNo;
    private String major;
}
