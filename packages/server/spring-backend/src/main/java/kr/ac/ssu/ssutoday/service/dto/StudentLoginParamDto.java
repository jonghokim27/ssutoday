/**
 * @filename    : StudentLoginParamDto.java
 * @description : StudentService login param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.Student;
import kr.ac.ssu.ssutoday.vo.JWTPayloadVo;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

@Builder
@Data
public class StudentLoginParamDto {
    private Integer id;
    private String name;
    private String major;
    private String status;

    @NotNull
    public JWTPayloadVo toJWTPayloadVO(){
        return JWTPayloadVo.builder()
                .studentId(this.id)
                .name(this.name)
                .major(this.major)
                .build();
    }

    @NotNull
    public Student toStudent(){
        return Student.builder()
                .id(this.id)
                .name(this.name)
                .major(this.major)
                .isAdmin(0)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}
