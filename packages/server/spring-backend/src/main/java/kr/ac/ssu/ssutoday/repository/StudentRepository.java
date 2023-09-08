/**
 * @filename    : StudentRepository.java
 * @description : Repository for Student entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.repository;

import kr.ac.ssu.ssutoday.entity.Student;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    @NotNull
    Optional<Student> findById(@NotNull Integer id);
}

