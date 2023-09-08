/**
 * @filename    : RoomService.java
 * @description : Room Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.service.dto.RoomGetParamDto;
import kr.ac.ssu.ssutoday.service.dto.RoomGetReturnDto;
import kr.ac.ssu.ssutoday.service.dto.RoomListParamDto;
import kr.ac.ssu.ssutoday.service.dto.RoomListReturnDto;
import org.jetbrains.annotations.NotNull;

public interface RoomService {
    /**
     * Get room
     * @param roomGetParamDto get params
     * @return get result (RoomGetReturnDto)
     * @author jonghokim27
     */
    @NotNull
    RoomGetReturnDto roomGet(RoomGetParamDto roomGetParamDto);

    /**
     * List room
     * @param roomListParamDto list params
     * @return list result (RoomListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    RoomListReturnDto roomList(RoomListParamDto roomListParamDto);
}
