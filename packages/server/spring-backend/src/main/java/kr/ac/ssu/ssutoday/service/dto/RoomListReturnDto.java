/**
 * @filename    : RoomListReturnDto.java
 * @description : RoomService list return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Room;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RoomListReturnDto {
    private List<Room> rooms;
}
