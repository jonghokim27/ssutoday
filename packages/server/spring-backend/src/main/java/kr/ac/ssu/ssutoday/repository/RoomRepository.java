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
    @Query(value = "SELECT * FROM Room WHERE no = ?1 AND major & ?2 > 0", nativeQuery = true)
    Optional<Room> findByIdAndMajor(String roomNo, Integer major);

    @Query(value = "SELECT * FROM Room WHERE major & ?1 > 0", nativeQuery = true)
    List<Room> findAllByMajor(Integer major);
}
