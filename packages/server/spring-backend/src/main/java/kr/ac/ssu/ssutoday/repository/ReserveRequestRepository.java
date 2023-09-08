/**
 * @filename    : ReserveRequestRepository.java
 * @description : Repository for ReserveRequest entity
 * @author      : jonghokim27
 */


package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.ReserveRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReserveRequestRepository extends JpaRepository<ReserveRequest, Integer> {
    Optional<ReserveRequest> findByIdxAndStudentId(Integer id, Integer studentId);
}
