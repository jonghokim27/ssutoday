/**
 * @filename    : RoomReserveListParamDto.java
 * @description : ReserveService list (for room) param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Builder
@Data
public class RoomReserveListParamDto {
    private Integer studentId;
    private String roomNo;
    private Date date;
}
