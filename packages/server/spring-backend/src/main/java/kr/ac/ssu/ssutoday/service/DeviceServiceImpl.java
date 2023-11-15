/**
 * @filename    : DeviceService.java
 * @description : Device Service
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service;

import kr.ac.ssu.ssutoday.entity.Device;
import kr.ac.ssu.ssutoday.entity.Version;
import kr.ac.ssu.ssutoday.repository.DeviceRepository;
import kr.ac.ssu.ssutoday.repository.VersionRepository;
import kr.ac.ssu.ssutoday.service.dto.*;
import kr.ac.ssu.ssutoday.vo.VersionVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService{
    /**
     * DI
     */
    private final DeviceRepository deviceRepository;
    private final VersionRepository versionRepository;

    /**
     * Register device
     * @param deviceRegisterParamDto device params
     * @author jonghokim27
     */
    @Override
    public void deviceRegister(@NotNull DeviceRegisterParamDto deviceRegisterParamDto) {
        Optional<Device> deviceOptional = deviceRepository.findByStudentIdAndOsTypeAndUuid(
                deviceRegisterParamDto.getStudent().getId(),
                deviceRegisterParamDto.getOsType(),
                deviceRegisterParamDto.getUuid()
        );

        Device device;
        if(deviceOptional.isPresent()){
            device = deviceOptional.get();
            device.setPushToken(deviceRegisterParamDto.getPushToken());
            device.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        } else{
            device = Device.builder()
                    .studentId(deviceRegisterParamDto.getStudent().getId())
                    .osType(deviceRegisterParamDto.getOsType())
                    .uuid(deviceRegisterParamDto.getUuid())
                    .pushToken(deviceRegisterParamDto.getPushToken())
                    .notice(1)
                    .reserve(1)
                    .lms(1)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .build();
        }

        deviceRepository.save(device);
    }

    /**
     * Unregister device
     * @param deviceUnregisterParamDto device params
     * @throws Exception thrown when device does not exist
     * @author jonghokim27
     */
    @Override
    public void deviceUnregister(@NotNull DeviceUnregisterParamDto deviceUnregisterParamDto) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findByStudentIdAndOsTypeAndUuid(
                deviceUnregisterParamDto.getStudent().getId(),
                deviceUnregisterParamDto.getOsType(),
                deviceUnregisterParamDto.getUuid()
        );

        if(deviceOptional.isEmpty()){
            log.debug("Device with osType {} and uuid {} does not exist.",
                    deviceUnregisterParamDto.getOsType(),
                    deviceUnregisterParamDto.getUuid());
            throw new Exception("Device with osType and uuid does not exist.");
        }

        deviceRepository.delete(deviceOptional.get());
    }

    /**
     * Check version
     * @param deviceCheckVersionParamDto check version params
     * @return updated required (Boolean)
     * @author jonghokim27
     */
    @NotNull
    @Override
    public Boolean deviceCheckVersion(@NotNull DeviceCheckVersionParamDto deviceCheckVersionParamDto) {
        Version dbVersion = versionRepository.getByOsType(deviceCheckVersionParamDto.getOsType());

        VersionVo required = new VersionVo(dbVersion.getRequiredVersion());
        VersionVo user = new VersionVo(deviceCheckVersionParamDto.getVersion());

        return user.compareTo(required) > 0;
    }

    /**
     * Get device option
     * @param deviceGetOptionParamDto device params
     * @return device option (DeviceGetOptionReturnDto)
     * @throws Exception thrown when device does not exist
     * @author jonghokim27
     */
    @NotNull
    @Override
    public DeviceGetOptionReturnDto deviceGetOption(@NotNull DeviceGetOptionParamDto deviceGetOptionParamDto) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findByStudentIdAndOsTypeAndUuid(
                deviceGetOptionParamDto.getStudent().getId(),
                deviceGetOptionParamDto.getOsType(),
                deviceGetOptionParamDto.getUuid()
        );

        if(deviceOptional.isEmpty()){
            log.debug("Device with osType {} and uuid {} does not exist.",
                    deviceGetOptionParamDto.getOsType(),
                    deviceGetOptionParamDto.getUuid());
            throw new Exception("Device with osType and uuid does not exist.");
        }
        Device device = deviceOptional.get();

        return DeviceGetOptionReturnDto.builder()
                .notice(device.getNotice() == 1)
                .reserve(device.getReserve() == 1)
                .lms(device.getLms() == 1)
                .build();
    }

    /**
     * Update device option
     * @param deviceUpdateOptionParamDto device and option params
     * @throws Exception thrown when device does not exist
     * @author jonghokim27
     */
    @Override
    public void deviceUpdateOption(@NotNull DeviceUpdateOptionParamDto deviceUpdateOptionParamDto) throws Exception {
        Optional<Device> deviceOptional = deviceRepository.findByStudentIdAndOsTypeAndUuid(
                deviceUpdateOptionParamDto.getStudent().getId(),
                deviceUpdateOptionParamDto.getOsType(),
                deviceUpdateOptionParamDto.getUuid()
        );

        if(deviceOptional.isEmpty()){
            log.debug("Device with osType {} and uuid {} does not exist.",
                    deviceUpdateOptionParamDto.getOsType(),
                    deviceUpdateOptionParamDto.getUuid());
            throw new Exception("Device with osType and uuid does not exist.");
        }
        Device device = deviceOptional.get();

        if(deviceUpdateOptionParamDto.getOption().equals("notice")) {
            device.setNotice(deviceUpdateOptionParamDto.getValue() ? 1 : 0);
        } else if(deviceUpdateOptionParamDto.getOption().equals("reserve")) {
            device.setReserve(deviceUpdateOptionParamDto.getValue() ? 1 : 0);
        } else if(deviceUpdateOptionParamDto.getOption().equals("lms")) {
            device.setLms(deviceUpdateOptionParamDto.getValue() ? 1 : 0);
        }

        device.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        deviceRepository.save(device);
    }
}
