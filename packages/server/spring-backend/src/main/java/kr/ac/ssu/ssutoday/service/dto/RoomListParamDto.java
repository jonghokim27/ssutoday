/**
 * @filename    : RoomListParamDto.java
 * @description : RoomService list param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RoomListParamDto {
    private String major;
}
