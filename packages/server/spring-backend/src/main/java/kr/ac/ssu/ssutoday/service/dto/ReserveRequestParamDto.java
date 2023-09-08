/**
 * @filename    : ReserveRequestParamDto.java
 * @description : ReserveService reserve request param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import kr.ac.ssu.ssutoday.entity.ReserveRequest;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.sql.Timestamp;

@Builder
@Data
public class ReserveRequestParamDto {
    private Integer studentId;
    private String roomNo;
    private Date date;
    private Integer startBlock;
    private Integer endBlock;

    @NotNull
    public ReserveRequest toReserveRequest(){
        return ReserveRequest.builder()
                .studentId(this.studentId)
                .roomNo(this.roomNo)
                .date(this.date)
                .startBlock(this.startBlock)
                .endBlock(this.endBlock)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .status(0)
                .build();
    }
}
