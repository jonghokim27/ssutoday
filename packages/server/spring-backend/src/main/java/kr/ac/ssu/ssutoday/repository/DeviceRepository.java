/**
 * @filename    : DeviceRepository.java
 * @description : Repository for Device entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Integer> {
    Optional<Device> findByStudentIdAndOsTypeAndUuid(Integer studentId, String osType, String uuid);
    List<Device> findAllByStudentId(Integer studentId);
}

