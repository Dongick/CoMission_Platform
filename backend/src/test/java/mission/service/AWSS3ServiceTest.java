package mission.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AWSS3ServiceTest {
    @Mock
    private S3Client s3Client;
    @InjectMocks
    private AWSS3Service awsS3Service;
    @Test
    void uploadFile() throws IOException {
        // Given
        MultipartFile multipartFile = mock(MultipartFile.class);
        String dirCheck = "missions/";
        String bucketName = "test-bucket";
        String fileName = "test.jpg";
        String key = dirCheck + fileName;
        String contentType = "image/jpg";
        long fileSize = 1024;
        byte[] fileContent = "test content".getBytes();
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/missions/" + fileName;

        ReflectionTestUtils.setField(awsS3Service, "bucketName", bucketName);

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getBytes()).thenReturn(fileContent);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        S3Utilities s3Utilities = mock(S3Utilities.class);
        when(s3Client.utilities()).thenReturn(s3Utilities);
        when(s3Client.utilities().getUrl(any(GetUrlRequest.class))).thenReturn(new URL(expectedUrl));

        // When
        String actualUrl = awsS3Service.uploadFile(multipartFile, dirCheck);

        // Then
        Assertions.assertThat(expectedUrl).isEqualTo(actualUrl);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(s3Client.utilities()).getUrl(any(GetUrlRequest.class));
    }

    @Test
    void deleteFile() throws IOException {
        // Given
        String dirCheck = "missions/";
        String filePath = "https://test-bucket.s3.amazonaws.com/missions/test.jpg";

        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(null);

        // When
        awsS3Service.deleteFile(filePath, dirCheck);

        // Then
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}