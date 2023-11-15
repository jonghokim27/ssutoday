/**
 * @filename    : RoomRepository.java
 * @description : Repository for Room entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {
    @Query(value = "SELECT * FROM Room WHERE no = ?1 AND JSON_CONTAINS(major, ?2)", nativeQuery = true)
    Optional<Room> findByIdAndMajor(String roomNo, String major);

    @Query(value = "SELECT * FROM Room WHERE JSON_CONTAINS(major, ?1)", nativeQuery = true)
    List<Room> findAllByMajor(String major);
}
