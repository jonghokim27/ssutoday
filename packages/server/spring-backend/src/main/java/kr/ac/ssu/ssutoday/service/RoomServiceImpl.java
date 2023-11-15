/**
 * @filename    : RoomServiceImpl.java
 * @description : Room Service Implementation
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.entity.Room;
import kr.ac.ssu.ssutoday.repository.RoomRepository;
import kr.ac.ssu.ssutoday.service.dto.RoomGetParamDto;
import kr.ac.ssu.ssutoday.service.dto.RoomGetReturnDto;
import kr.ac.ssu.ssutoday.service.dto.RoomListParamDto;
import kr.ac.ssu.ssutoday.service.dto.RoomListReturnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    /**
     * DI
     */
    private final RoomRepository roomRepository;


    /**
     * Get room
     * @param roomGetParamDto get params
     * @return get result (RoomGetReturnDto)
     * @author jonghokim27
     */
    @NotNull
    @Override
    public RoomGetReturnDto roomGet(RoomGetParamDto roomGetParamDto) {
        Optional<Room> roomOptional;

        if(!roomGetParamDto.getIsAdmin()){
            roomOptional = roomRepository.findByIdAndMajor(roomGetParamDto.getRoomNo(), '"' + roomGetParamDto.getMajor() + '"');
        } else {
            roomOptional = roomRepository.findById(roomGetParamDto.getRoomNo());
        }

        return RoomGetReturnDto.builder()
                .room(roomOptional)
                .build();
    }

    /**
     * List room
     * @param roomListParamDto list params
     * @return list result (RoomListReturnDto)
     * @author jonghokim27
     */
    @NotNull
    @Override
    public RoomListReturnDto roomList(RoomListParamDto roomListParamDto) {
        List<Room> roomList;

        if(!roomListParamDto.getIsAdmin()) {
            roomList = roomRepository.findAllByMajor('"' + roomListParamDto.getMajor() + '"');
        } else {
            roomList = roomRepository.findAll();
        }

        return RoomListReturnDto.builder()
                .rooms(roomList)
                .build();
    }
}
