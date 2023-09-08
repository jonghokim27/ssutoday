/**
 * @filename    : SSOTokenRepository.java
 * @description : Repository for SSOToken entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.SSOToken;
import org.springframework.data.repository.CrudRepository;

public interface SSOTokenRepository extends CrudRepository<SSOToken, String> {
}
