/**
 * @filename    : Config.java
 * @description : Config entity
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Config {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "key", nullable = false, length = 100)
    private String key;
    @Basic
    @Column(name = "value", nullable = false, length = 100)
    private String value;
}
