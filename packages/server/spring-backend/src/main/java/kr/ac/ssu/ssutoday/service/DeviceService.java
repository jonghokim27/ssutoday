/**
 * @filename    : DeviceService.java
 * @description : Device Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.service.dto.*;
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
     * @throws Exception thrown when device does not exist
     * @author jonghokim27
     */
    void deviceUnregister(@NotNull DeviceUnregisterParamDto deviceUnregisterParamDto) throws Exception;

    /**
     * Get device option
     * @param deviceGetOptionParamDto device params
     * @return device option (DeviceGetOptionReturnDto)
     * @throws Exception thrown when device does not exist
     * @author jonghokim27
     */
    @NotNull
    DeviceGetOptionReturnDto deviceGetOption(@NotNull DeviceGetOptionParamDto deviceGetOptionParamDto) throws Exception;

    /**
     * Update device option
     * @param deviceUpdateOptionParamDto device and option params
     * @throws Exception thrown when device does not exist
     * @author jonghokim27
     */
    void deviceUpdateOption(@NotNull DeviceUpdateOptionParamDto deviceUpdateOptionParamDto) throws Exception;

    /**
     * Check version
     * @param deviceCheckVersionParamDto check version params
     * @return updated required (Boolean)
     * @author jonghokim27
     */
    @NotNull
    Boolean deviceCheckVersion(@NotNull DeviceCheckVersionParamDto deviceCheckVersionParamDto);
}
