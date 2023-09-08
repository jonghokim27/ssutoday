/**
 * @filename    : DeviceService.java
 * @description : Device Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.service.dto.DeviceCheckVersionParamDto;
import kr.ac.ssu.ssutoday.service.dto.DeviceRegisterParamDto;
import kr.ac.ssu.ssutoday.service.dto.DeviceUnregisterParamDto;
import org.jetbrains.annotations.NotNull;

public interface DeviceService {
    /**
     * Register device
     * @param deviceRegisterParamDto device params
     * @author jonghokim27
     */
    void deviceRegister(@NotNull DeviceRegisterParamDto deviceRegisterParamDto);

    /**
     * Unregister device
     * @param deviceUnregisterParamDto device params
     * @author jonghokim27
     */
    void deviceUnregister(@NotNull DeviceUnregisterParamDto deviceUnregisterParamDto) throws Exception;

    /**
     * Check version
     * @param deviceCheckVersionParamDto check version params
     * @return updated required (Boolean)
     * @author jonghokim27
     */
    @NotNull
    Boolean deviceCheckVersion(@NotNull DeviceCheckVersionParamDto deviceCheckVersionParamDto);
}
