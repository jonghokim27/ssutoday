/**
 * @filename    : RoomReserveListReturnDto.java
 * @description : ReserveService list (for room) return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.vo.ReserveVo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RoomReserveListReturnDto {
    private List<ReserveVo> reserves;
}
