/**
 * @filename    : Student.java
 * @description : Student entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import kr.ac.ssu.ssutoday.controller.dto.StudentProfileResponseDto;
import kr.ac.ssu.ssutoday.service.dto.StudentLogoutParamDto;
import kr.ac.ssu.ssutoday.vo.JWTPayloadVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Student {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Basic
    @Column(name = "major", nullable = false, length = 10)
    private String major;
    @Basic
    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "updatedAt", nullable = true)
    private Timestamp updatedAt;

    @NotNull
    public JWTPayloadVo toJWTPayloadVO(){
        return JWTPayloadVo.builder()
                .studentId(this.id)
                .name(this.name)
                .major(this.major)
                .build();
    }

    @NotNull
    public StudentLogoutParamDto toStudentLogoutParamDto(@NotNull String refreshToken){
        return StudentLogoutParamDto.builder()
                .student(this)
                .refreshToken(refreshToken)
                .build();
    }

    @NotNull
    public StudentProfileResponseDto toStudentProfileResponseDto(){
        return StudentProfileResponseDto.builder()
                .studentId(this.id)
                .name(this.name)
                .major(this.major)
                .build();
    }
}
