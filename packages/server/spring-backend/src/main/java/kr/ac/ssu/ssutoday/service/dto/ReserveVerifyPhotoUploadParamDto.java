/**
 * @filename    : ReserveVerifyPhotoUploadParamDto.java
 * @description : ReserveService verify photo upload param DTO
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.service.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
public class ReserveVerifyPhotoUploadParamDto {
    private Integer studentId;
    private Integer idx;
    private MultipartFile file;
}
