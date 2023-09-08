/**
 * @filename    : ReserveRepository.java
 * @description : Repository for Reserve entity
 * @author      : jonghokim27
 */


package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.Reserve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ReserveRepository extends JpaRepository<Reserve, Integer> {
    long countByDateAndRoomNoAndStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndDeletedAtIsNull(Date date, String roomNo, Integer startBlock, Integer endBlock);
    List<Reserve> findAllByStudentIdAndDateAndDeletedAtIsNull(Integer studentId, Date date);
    List<Reserve> findAllByRoomNoAndDateAndDeletedAtIsNull(String roomNo, Date date);
    @Query(value = "select * from Reserve " +
            "where StudentId = ?1 AND " +
            "(date < ?2 OR (date = ?2 AND endBlock < ?3))",
            countQuery = "select * from Reserve " +
                    "where StudentId = ?1 AND " +
                    "(date < ?2 OR (date = ?2 AND endBlock < ?3))",
            nativeQuery = true)
    Page<Reserve> findAllPreviousReserve(Integer studentId, Date date, Integer nowBlock, PageRequest pageRequest);

    @Query(value = "select * from Reserve " +
            "where StudentId = ?1 AND " +
            "(date > ?2 OR (date = ?2 AND endBlock >= ?3))",
            countQuery = "select * from Reserve " +
                    "where StudentId = ?1 AND " +
                    "(date > ?2 OR (date = ?2 AND endBlock >= ?3))",
            nativeQuery = true)
    Page<Reserve> findAllWaitingReserve(Integer studentId, Date date, Integer nowBlock, PageRequest pageRequest);

    List<Reserve> findAllByDateAndStartBlockAndDeletedAtIsNull(Date date, Integer startBlock);
    List<Reserve> findAllByDateAndEndBlockAndDeletedAtIsNull(Date date, Integer endBlock);

    Optional<Reserve> findByIdxAndStudentIdAndDeletedAtIsNull(Integer idx, Integer studentId);
}