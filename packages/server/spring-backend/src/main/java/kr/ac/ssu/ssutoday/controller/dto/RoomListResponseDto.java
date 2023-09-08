/**
 * @filename    : RoomListResponseDto.java
 * @description : RoomController list response DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import kr.ac.ssu.ssutoday.vo.RoomVo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoomListResponseDto {
    private List<RoomVo> rooms;
}