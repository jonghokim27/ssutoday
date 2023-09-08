/**
 * @filename    : Version.java
 * @description : Version entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class Version {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idx", nullable = false)
    private Integer idx;
    @Basic
    @Column(name = "osType", nullable = false, length = 10)
    private String osType;
    @Basic
    @Column(name = "requiredVersion", nullable = false, length = 10)
    private String requiredVersion;
}
