/**
 * @filename    : Reserve.java
 * @description : Reserve entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import kr.ac.ssu.ssutoday.vo.ReserveDetailVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reserve {
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
    @Column(name = "createdAt", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "deletedAt", nullable = true)
    private Timestamp deletedAt;
    @ManyToOne
    @JoinColumn(name = "StudentId", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Student studentByStudentId;
    @ManyToOne
    @JoinColumn(name = "roomNo", referencedColumnName = "no", nullable = false, insertable = false, updatable = false)
    private Room roomByRoomNo;
    @JsonManagedReference
    @OneToMany(mappedBy = "reserveByReserveIdx", fetch = FetchType.LAZY)
    private Collection<VerifyPhoto> verifyPhotosByIdx;

    @NotNull
    public ReserveDetailVo toReserveDetailVo(){
        return ReserveDetailVo.builder()
                .idx(this.idx)
                .roomNo(this.roomNo)
                .date(this.date)
                .startBlock(this.startBlock)
                .endBlock(this.endBlock)
                .createdAt(this.createdAt)
                .deletedAt(this.deletedAt)
                .roomByRoomNo(this.roomByRoomNo)
                .verifyPhotosByIdx(this.verifyPhotosByIdx)
                .build();
    }
}
