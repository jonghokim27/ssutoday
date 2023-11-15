/**
 * @filename    : RoomListRequestDto.java
 * @description : RoomController list request DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import kr.ac.ssu.ssutoday.service.dto.RoomReserveListParamDto;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;

@Data
public class RoomListRequestDto {
    @NotEmpty(message = "date is empty.")
    @Pattern(regexp = "202[3-9]-(0[1-9]|1[0-2])-(0[1-9]|1[0-9]|2[0-9]|3[0-1])", message = "Invalid date.")
    private String date;

    @NotNull
    public RoomReserveListParamDto toRoomReserveListParamDto(@NotNull Integer studentId, @NotNull Boolean isAdmin, @NotNull String roomNo){
        return RoomReserveListParamDto.builder()
                .studentId(studentId)
                .isAdmin(isAdmin)
                .roomNo(roomNo)
                .date(Date.valueOf(this.date))
                .build();
    }
}
