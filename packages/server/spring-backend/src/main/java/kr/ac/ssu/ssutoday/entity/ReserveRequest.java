/**
 * @filename    : ReserveRequest.java
 * @description : Reserve Request entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReserveRequest {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idx", nullable = false)
    private Integer idx;
    @Basic
    @Column(name = "StudentId", nullable = false)
    private Integer studentId;
    @Basic
    @Column(name = "roomNo", nullable = false, length = 10)
    private String roomNo;
    @Basic
    @Column(name = "date", nullable = false)
    private Date date;
    @Basic
    @Column(name = "startBlock", nullable = false)
    private Integer startBlock;
    @Basic
    @Column(name = "endBlock", nullable = false)
    private Integer endBlock;
    @Basic
    @Column(name = "status", nullable = false)
    private Integer status;
    @Basic
    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "updatedAt", nullable = true)
    private Timestamp updatedAt;

    @NotNull
    public Reserve toReserve(){
        return Reserve.builder()
                .studentId(this.studentId)
                .roomNo(this.roomNo)
                .date(this.date)
                .startBlock(this.startBlock)
                .endBlock(this.endBlock)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
    }
}
