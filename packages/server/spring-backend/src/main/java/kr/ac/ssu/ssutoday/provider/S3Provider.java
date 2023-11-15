/**
 * @filename    : S3Provider.java
 * @description : Provider for S3
 * @author      : jonghokim27
 */

package kr.ac.ssu.ssutoday.provider;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Provider {
    /**
     * DI
     */
    private final AmazonS3Client amazonS3Client;

    /**
     * Upload file to S3 bucket
     * @param fileName name of file
     * @param file multipart file
     * @throws Exception thrown when failed to upload file
     */
    public void uploadFile(@NotNull String bucket, @NotNull String fileName, @NotNull MultipartFile file) throws Exception{
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
        } catch(Exception e){
            throw new Exception("Failed to upload file to S3.");
        }
    }
}
