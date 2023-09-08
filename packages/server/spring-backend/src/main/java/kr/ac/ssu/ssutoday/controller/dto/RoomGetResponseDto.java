/**
 * @filename    : RoomGetResponseDto.java
 * @description : RoomController get response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import kr.ac.ssu.ssutoday.vo.RoomVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomGetResponseDto {
    private RoomVo room;
}