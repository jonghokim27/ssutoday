/**
 * @filename    : ReserveVo.java
 * @description : Reserve Value Object
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReserveVo {
    private String studentInfo;
    private Integer startBlock;
    private Integer endBlock;
    private Boolean isMine;
}
