/**
 * @filename    : VerifyPhotoRepository.java
 * @description : Repository for VerifyPhoto entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.VerifyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifyPhotoRepository extends JpaRepository<VerifyPhoto, Integer> {
    Optional<VerifyPhoto> findByReserveIdx(Integer reserveIdx);
}
