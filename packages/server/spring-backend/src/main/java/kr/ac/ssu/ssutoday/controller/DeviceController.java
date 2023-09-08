/**
 * @filename    : DeviceController.java
 * @description : REST API controller for /device
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller;

import jakarta.validation.Valid;
import kr.ac.ssu.ssutoday.common.CommonResponse;
import kr.ac.ssu.ssutoday.common.StatusCode;
import kr.ac.ssu.ssutoday.controller.dto.DeviceCheckVersionRequestDto;
import kr.ac.ssu.ssutoday.controller.dto.DeviceRegisterRequestDto;
import kr.ac.ssu.ssutoday.controller.dto.DeviceUnregisterRequestDto;
import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.service.DeviceService;
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

@Controller
@RequestMapping("/device")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {
    /**
     * DI
     */
    private final DeviceService deviceService;
    private final StatusCode statusCode;

    /**
     * Register Device (04)
     * /device/register
     * @param deviceRegisterRequestDto register device params
     * @return null
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/register")
    @ResponseBody
    public CommonResponse register(@NotNull @Valid @RequestBody DeviceRegisterRequestDto deviceRegisterRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        deviceService.deviceRegister(deviceRegisterRequestDto.toDeviceRegisterParamDto(student));

        return new CommonResponse(statusCode.SSU2040, student.toStudentProfileResponseDto(), statusCode.SSU2040_MSG);
    }

    /**
     * Unregister Device (05)
     * /device/unregister
     * @param deviceUnregisterRequestDto unregister device params
     * @return null
     * @author jonghokim27
     */
    @Nullable
    @PostMapping("/unregister")
    @ResponseBody
    public CommonResponse unregister(@NotNull @Valid @RequestBody DeviceUnregisterRequestDto deviceUnregisterRequestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Student student = (Student) authentication.getPrincipal();

        try {
            deviceService.deviceUnregister(deviceUnregisterRequestDto.toDeviceUnregisterParamDto(student));
            return new CommonResponse(statusCode.SSU2050, student.toStudentProfileResponseDto(), statusCode.SSU2050_MSG);
        }
        catch (Exception e){
            return new CommonResponse(statusCode.SSU4050, student.toStudentProfileResponseDto(), statusCode.SSU4050_MSG);
        }
    }

    /**
     * Check Version (07)
     * /device/checkVersion
     * @param deviceCheckVersionRequestDto check version params
     * @return check version result (DeviceCheckVersionResponseDto)
     * @author jonghokim27
     */
    @NotNull
    @PostMapping("/checkVersion")
    @ResponseBody
    public CommonResponse checkVersion(@NotNull @Valid @RequestBody DeviceCheckVersionRequestDto deviceCheckVersionRequestDto){
        Boolean updateRequired = deviceService.deviceCheckVersion(deviceCheckVersionRequestDto.toDeviceCheckVersionParamDto());

        if(updateRequired){
            return new CommonResponse(statusCode.SSU2071, null, statusCode.SSU2071_MSG);
        } else {
            return new CommonResponse(statusCode.SSU2070, null, statusCode.SSU2070_MSG);
        }
    }
}
