/**
 * @filename    : RoomController.java
 * @description : REST API controller for /room
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller;

import jakarta.validation.Valid;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import kr.ac.ssu.ssutoday.controller.dto.RoomGetRequestDto;
import kr.ac.ssu.ssutoday.controller.dto.RoomGetResponseDto;
import kr.ac.ssu.ssutoday.controller.dto.RoomListRequestDto;
import kr.ac.ssu.ssutoday.controller.dto.RoomListResponseDto;
import kr.ac.ssu.ssutoday.entity.Room;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.ReserveService;
import kr.ac.ssu.ssutoday.service.RoomService;
import kr.ac.ssu.ssutoday.service.dto.*;
import kr.ac.ssu.ssutoday.vo.RoomVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/room")
@RequiredArgsConstructor
@Slf4j
public class RoomController {
    /**
     * DI
     */
    private final RoomService roomService;
    private final ReserveService reserveService;
    private final StatusCode statusCode;

    /**
     * Get Room (10)
     * /room/get
     * @param roomGetRequestDto get params
     * @return get result (RoomGetResponseDto)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/get")
    @ResponseBody
    public CommonResponse get(@NotNull @Valid @RequestBody RoomGetRequestDto roomGetRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        RoomGetParamDto roomGetParamDto = RoomGetParamDto.builder()
                .roomNo(roomGetRequestDto.getRoomNo())
                .major(student.getMajor())
                .isAdmin(student.getIsAdmin() == 1)
                .build();
        RoomGetReturnDto roomGetReturnDto = roomService.roomGet(roomGetParamDto);

        if(roomGetReturnDto.getRoom().isEmpty()){
            return new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        }

        RoomReserveListReturnDto roomReserveListReturnDto = reserveService.roomReserveList(roomGetRequestDto.toRoomReserveListParamDto(student.getId(), student.getIsAdmin() == 1));
        RoomVo roomVo = roomGetReturnDto.getRoom().get().toRoomVo(roomReserveListReturnDto.getReserves());

        RoomGetResponseDto roomGetResponseDto = RoomGetResponseDto.builder()
                .room(roomVo)
                .build();

        return new CommonResponse(statusCode.SSU2100, roomGetResponseDto, statusCode.SSU2100_MSG);
    }

    /**
     * List Room (11)
     * /room/list
     * @param roomListRequestDto list params
     * @return list result (RoomListResponseDto)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/list")
    @ResponseBody
    public CommonResponse list(@NotNull @Valid @RequestBody RoomListRequestDto roomListRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        RoomListParamDto roomListParamDto = RoomListParamDto.builder()
                .major(student.getMajor())
                .isAdmin(student.getIsAdmin() == 1)
                .build();
        RoomListReturnDto roomListReturnDto = roomService.roomList(roomListParamDto);

        List<RoomVo> roomVoList = new ArrayList<>();
        for(Room room : roomListReturnDto.getRooms()){
            RoomReserveListReturnDto roomReserveListReturnDto = reserveService.roomReserveList(roomListRequestDto.toRoomReserveListParamDto(student.getId(), student.getIsAdmin() == 1, room.getNo()));
            RoomVo roomVo = room.toRoomVo(roomReserveListReturnDto.getReserves());
            roomVoList.add(roomVo);
        }

        RoomListResponseDto roomListResponseDto = RoomListResponseDto.builder()
                .rooms(roomVoList)
                .build();

        return new CommonResponse(statusCode.SSU2110, roomListResponseDto, statusCode.SSU2110_MSG);
    }
}
