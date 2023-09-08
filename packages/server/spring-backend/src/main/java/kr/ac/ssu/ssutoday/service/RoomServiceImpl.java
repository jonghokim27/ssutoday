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
        int majorInteger = switch (roomGetParamDto.getMajor()) {
            case "cse" -> 4;
            case "sw" -> 2;
            case "media" -> 1;
            default -> 0;
        };

        Optional<Room> roomOptional = roomRepository.findByIdAndMajor(roomGetParamDto.getRoomNo(), majorInteger);

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
        int majorInteger = switch (roomListParamDto.getMajor()) {
            case "cse" -> 4;
            case "sw" -> 2;
            case "media" -> 1;
            default -> 0;
        };

        List<Room> roomList = roomRepository.findAllByMajor(majorInteger);

        return RoomListReturnDto.builder()
                .rooms(roomList)
                .build();
    }
}
