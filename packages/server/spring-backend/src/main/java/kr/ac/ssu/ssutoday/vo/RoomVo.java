/**
 * @filename    : RoomVo.java
 * @description : Room Value Object
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RoomVo {
    private String no;
    private String name;
    private Integer capacity;
    private String location;
    private String tags;
    private String image;
    private String bigImage;
    private List<ReserveVo> reserves;
}
