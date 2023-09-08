/**
 * @filename    : SSOClientRepository.java
 * @description : Repository for SSOClient entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.SSOClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SSOClientRepository extends JpaRepository<SSOClient, String> {
    Optional<SSOClient> findByIdAndSecret(String id, String secret);
}
