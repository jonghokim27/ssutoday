/**
 * @filename    : Device.java
 * @description : Device entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idx", nullable = false)
    private Integer idx;
    @Basic
    @Column(name = "StudentId", nullable = false)
    private Integer studentId;
    @Basic
    @Column(name = "osType", nullable = false, length = 10)
    private String osType;
    @Basic
    @Column(name = "uuid", nullable = false, length = 200)
    private String uuid;
    @Basic
    @Column(name = "pushToken", nullable = false, length = 200)
    private String pushToken;
    @Basic
    @Column(name = "notice", nullable = false)
    private Integer notice;
    @Basic
    @Column(name = "reserve", nullable = false)
    private Integer reserve;
    @Basic
    @Column(name = "lms", nullable = false)
    private Integer lms;
    @Basic
    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "updatedAt", nullable = true)
    private Timestamp updatedAt;
    @ManyToOne
    @JoinColumn(name = "StudentId", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Student studentByStudentId;
}
