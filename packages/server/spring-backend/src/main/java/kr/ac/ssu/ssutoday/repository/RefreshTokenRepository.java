/**
 * @filename    : RefreshTokenRepository.java
 * @description : Repository for RefreshToken entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
