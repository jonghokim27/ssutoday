/**
 * @filename    : ReserveVerifyPhotoUploadRequest.java
 * @description : ReserveController verify photo upload request dto
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.controller.dto;

import jakarta.validation.constraints.NotNull;
import kr.ac.ssu.ssutoday.service.dto.ReserveVerifyPhotoUploadParamDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ReserveVerifyPhotoUploadRequestDto {
    @NotNull(message = "idx is null.")
    private Integer idx;

    @NotNull(message = "file is null.")
    private MultipartFile file;

    @org.jetbrains.annotations.NotNull
    public ReserveVerifyPhotoUploadParamDto toReserveVerifyPhotoUploadParamDto(@org.jetbrains.annotations.NotNull Integer studentId){
        return ReserveVerifyPhotoUploadParamDto.builder()
                .studentId(studentId)
                .idx(this.idx)
                .file(this.file)
                .build();
    }
}
