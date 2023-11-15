/**
 * @filename    : Room.java
 * @description : Room entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import kr.ac.ssu.ssutoday.vo.ReserveVo;
import kr.ac.ssu.ssutoday.vo.RoomVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Room {
    @Id
    @Column(name = "no", nullable = false, length = 10)
    private String no;
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "major", nullable = false)
    private Object major;
    @Basic
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    @Basic
    @Column(name = "location", nullable = false, length = 50)
    private String location;
    @Basic
    @Column(name = "tags", nullable = false, length = 50)
    private String tags;
    @Basic
    @Column(name = "image", nullable = false, length = 200)
    private String image;
    @Basic
    @Column(name = "bigImage", nullable = false, length = 200)
    private String bigImage;

    @NotNull
    public RoomVo toRoomVo(@NotNull List<ReserveVo> reserveVoList){
        return RoomVo.builder()
                .no(this.no)
                .name(this.name)
                .capacity(this.capacity)
                .location(this.location)
                .tags(this.tags)
                .image(this.image)
                .bigImage(this.bigImage)
                .reserves(reserveVoList)
                .build();
    }
}
