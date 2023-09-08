/**
 * @filename    : RoomGetReturnDto.java
 * @description : RoomService get return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Room;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Builder
@Data
public class RoomGetReturnDto {
    private Optional<Room> room;
}
