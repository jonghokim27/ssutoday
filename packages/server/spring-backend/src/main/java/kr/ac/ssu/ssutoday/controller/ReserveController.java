/**
 * @filename    : ReserveController.java
 * @description : REST API controller for /reserve
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import kr.ac.ssu.ssutoday.controller.dto.*;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.exception.ConfigDisabledException;
import kr.ac.ssu.ssutoday.exception.FileUploadFailedException;
import kr.ac.ssu.ssutoday.service.ReserveService;
import kr.ac.ssu.ssutoday.service.RoomService;
import kr.ac.ssu.ssutoday.service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;

@Controller
@RequestMapping("/reserve")
@RequiredArgsConstructor
@Slf4j
public class ReserveController {
    /**
     * DI
     */
    private final ReserveService reserveService;
    private final RoomService roomService;
    private final StatusCode statusCode;

    /**
     * Request Reserve (09)
     * /reserve/request
     * @param reserveRequestRequestDto request reserve params
     * @return request reserve result (ReserveRequestResponseDto)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/request")
    @ResponseBody
    public CommonResponse request(@NotNull @Valid @RequestBody ReserveRequestRequestDto reserveRequestRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        if(reserveRequestRequestDto.getStartBlock() > reserveRequestRequestDto.getEndBlock()){
            return new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        }

        if(reserveRequestRequestDto.getEndBlock() - reserveRequestRequestDto.getStartBlock() > 3){
            return new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        }

        RoomGetParamDto roomGetParamDto = RoomGetParamDto.builder()
                .roomNo(reserveRequestRequestDto.getRoomNo())
                .major(student.getMajor())
                .isAdmin(student.getIsAdmin() == 1)
                .build();

        RoomGetReturnDto roomGetReturnDto  = roomService.roomGet(roomGetParamDto);
        if(roomGetReturnDto.getRoom().isEmpty()){
            return new CommonResponse(statusCode.SSU4000, null, statusCode.SSU4000_MSG);
        }

        try{
            ReserveRequestReturnDto reserveRequestReturnDto = reserveService.reserveRequest(
                    reserveRequestRequestDto.toReserveRequestParamDto(student.getId()));

            return new CommonResponse(statusCode.SSU2090, reserveRequestReturnDto.toReserveRequestResponseDto(), statusCode.SSU2090_MSG);
        } catch (ConfigDisabledException e){
            return new CommonResponse(statusCode.SSU5091, null, statusCode.SSU5091_MSG);
        } catch (Exception e){
            return new CommonResponse(statusCode.SSU5090, null, statusCode.SSU5090_MSG);
        }
    }

    /**
     * Get Reserve Status (12)
     * /reserve/status
     * @param reserveStatusRequestDto get status params
     * @return get status result (ReserveStatusResponseDto)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/status")
    @ResponseBody
    public CommonResponse status(@NotNull @Valid @RequestBody ReserveStatusRequestDto reserveStatusRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        try {
            ReserveStatusReturnDto reserveStatusReturnDto = reserveService.reserveStatus(reserveStatusRequestDto.toReserveStatusParamDto(student.getId()));
            return new CommonResponse(statusCode.SSU2120, reserveStatusReturnDto.toReserveStatusResponseDto(), statusCode.SSU2120_MSG);
        } catch (Exception e){
            return new CommonResponse(statusCode.SSU4120, null, statusCode.SSU4120_MSG);
        }
    }

    /**
     * Get Reserve List (13)
     * /reserve/list
     * @param reserveListRequestDto get list params
     * @return get list result (ReserveListResponseDto)
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/list")
    @ResponseBody
    public CommonResponse list(@NotNull @Valid @RequestBody ReserveListRequestDto reserveListRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        ReserveListReturnDto reserveListReturnDto = reserveService.reserveList(reserveListRequestDto.toReserveListParamDto(student.getId()));

        return new CommonResponse(statusCode.SSU2130, reserveListReturnDto.toReserveListResponseDto(), statusCode.SSU2130_MSG);
    }

    /**
     * Cancel Reserve (14)
     * /reserve/cancel
     * @param reserveCancelRequestDto cancel params
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/cancel")
    @ResponseBody
    public CommonResponse cancel(@NotNull @Valid @RequestBody ReserveCancelRequestDto reserveCancelRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        try{
            ReserveCancelReturnDto reserveCancelReturnDto = reserveService.reserveCancel(reserveCancelRequestDto.toReserveCancelParamDto(student.getId()));
            if(reserveCancelReturnDto.getStatus() == 2) {
                return new CommonResponse(statusCode.SSU4141, null, statusCode.SSU4141_MSG);
            } else if(reserveCancelReturnDto.getStatus() == 3) {
                return new CommonResponse(statusCode.SSU4142, null, statusCode.SSU4142_MSG);
            } else if(reserveCancelReturnDto.getStatus() == 4) {
                return new CommonResponse(statusCode.SSU4143, null, statusCode.SSU4143_MSG);
            }

            return new CommonResponse(statusCode.SSU2140, null, statusCode.SSU2140_MSG);

        } catch(Exception e){
            return new CommonResponse(statusCode.SSU4140, null, statusCode.SSU4140_MSG);
        }

    }

    /**
     * Upload verify photo (20)
     * /reserve/verifyPhoto/upload
     * @param reserveVerifyPhotoUploadRequestDto upload params
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/verifyPhoto/upload")
    @ResponseBody
    public CommonResponse uploadVerifyPhoto(@NotNull @Valid ReserveVerifyPhotoUploadRequestDto reserveVerifyPhotoUploadRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        try{
            ReserveVerifyPhotoUploadReturnDto reserveVerifyPhotoUploadReturnDto =
                    reserveService.verifyPhotoUpload(reserveVerifyPhotoUploadRequestDto.toReserveVerifyPhotoUploadParamDto(student.getId()));

            if(reserveVerifyPhotoUploadReturnDto.getStatus() == 2){
                return new CommonResponse(statusCode.SSU4201, null, statusCode.SSU4201_MSG);
            } else if(reserveVerifyPhotoUploadReturnDto.getStatus() == 3){
                return new CommonResponse(statusCode.SSU4202, null, statusCode.SSU4202_MSG);
            } else if(reserveVerifyPhotoUploadReturnDto.getStatus() == 4){
                return new CommonResponse(statusCode.SSU4203, null, statusCode.SSU4203_MSG);
            } else if(reserveVerifyPhotoUploadReturnDto.getStatus() == 5){
                return new CommonResponse(statusCode.SSU4204, null, statusCode.SSU4204_MSG);
            }

            return new CommonResponse(statusCode.SSU2200, null, statusCode.SSU2200_MSG);
        } catch(FileUploadFailedException e){
            return new CommonResponse(statusCode.SSU5200, null, statusCode.SSU5200_MSG);
        } catch(Exception e){
            return new CommonResponse(statusCode.SSU4200, null, statusCode.SSU4200_MSG);
        }

    }

}
