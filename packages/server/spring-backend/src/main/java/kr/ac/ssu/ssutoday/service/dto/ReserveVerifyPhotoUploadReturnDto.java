/**
 * @filename    : ReserveVerifyPhotoUploadReturnDto.java
 * @description : ReserveService verify photo upload return DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReserveVerifyPhotoUploadReturnDto {
    private Integer status;
}
