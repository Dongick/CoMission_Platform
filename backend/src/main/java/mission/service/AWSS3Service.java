package mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AWSS3Service {
    private final S3Client s3Client;
    private static final String MISSION_DIR = "missions/";
    private static final String AUTHENTICATION_DIR = "authentications/";
    @Value("${spring.cloud.aws.s3.bucket}")
    private final String bucketName;

    // 사진을 AWS S3에 저장
    public String uploadFile(MultipartFile multipartFile, String dirCheck) throws IOException{
        String fileName = getFileName(multipartFile);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .key((dirCheck == MISSION_DIR ? MISSION_DIR : AUTHENTICATION_DIR) + fileName)
                .build();
        RequestBody requestBody = RequestBody.fromBytes(multipartFile.getBytes());
        s3Client.putObject(putObjectRequest, requestBody);

        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key((dirCheck == MISSION_DIR ? MISSION_DIR : AUTHENTICATION_DIR) + fileName)
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }

    // AWS S3에 존재하는 사진을 삭제
    public void deleteFile(String filePath, String dirCheck) throws IOException{
        String imageName = filePath.substring(filePath.lastIndexOf("/") + 1);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key((dirCheck == MISSION_DIR ? MISSION_DIR : AUTHENTICATION_DIR) + imageName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private String getFileName(MultipartFile multipartFile) {
        String now = String.valueOf(System.currentTimeMillis());
        String originName = multipartFile.getOriginalFilename();
        String extension = originName.substring(originName.lastIndexOf("."));

        return now + UUID.randomUUID().toString() + extension;
    }
}
