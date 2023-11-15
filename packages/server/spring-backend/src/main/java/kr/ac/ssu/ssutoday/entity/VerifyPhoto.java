/**
 * @filename    : VerifyPhoto.java
 * @description : VerifyPhoto entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class VerifyPhoto {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idx", nullable = false)
    private Integer idx;
    @Basic
    @Column(name = "ReserveIdx", nullable = false)
    private Integer reserveIdx;
    @Basic
    @Column(name = "url", nullable = false)
    private String url;
    @Basic
    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ReserveIdx", referencedColumnName = "idx", nullable = false, insertable = false, updatable = false)
    private Reserve reserveByReserveIdx;
}
