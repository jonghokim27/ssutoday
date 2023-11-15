/**
 * @filename    : ReserveDetailVo.java
 * @description : Reserve Detail Value Object
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.vo;

import kr.ac.ssu.ssutoday.entity.Room;
import kr.ac.ssu.ssutoday.entity.VerifyPhoto;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;

@Builder
@Data
public class ReserveDetailVo {
    private Integer idx;
    private String roomNo;
    private Date date;
    private Integer startBlock;
    private Integer endBlock;
    private Timestamp createdAt;
    private Timestamp deletedAt;
    private Room roomByRoomNo;
    private Collection<VerifyPhoto> verifyPhotosByIdx;
    private Boolean isContinuous;
}
