/**
 * @filename    : PushMessageVo.java
 * @description : Push Message Value Object
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PushMessageVo {
    private String type;
    private String topic;
    private String token;
    private String title;
    private String body;
    private String link;
}
