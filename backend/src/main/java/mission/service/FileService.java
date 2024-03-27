package mission.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
<<<<<<< HEAD
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    public String uploadFile(MultipartFile photoData) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".jpg"; // 확장자는 이미지 형식에 맞게 변경
        Path filePath = Paths.get(uploadDirectory, fileName);
=======
    @Value("${file.upload.authentication.directory}")
    private String uploadAuthenticationDirectory;
    @Value("${file.upload.mission.directory}")
    private String uploadMissionDirectory;

    // 인증글 작성 시 사진이 존재하면 해당 사진을 서버에 저장
    public String uploadAuthenticationFile(MultipartFile photoData) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".jpg"; // 확장자는 이미지 형식에 맞게 변경
        Path filePath = Paths.get(uploadAuthenticationDirectory, fileName);
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

        byte[] bytes = photoData.getBytes();
        Files.write(filePath, bytes);

        return filePath.toString();
    }

<<<<<<< HEAD
=======
    // 미션 생성시 사진을 서버에 저장
    public String uploadMissionFile(MultipartFile photoData) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".jpg"; // 확장자는 이미지 형식에 맞게 변경
        Path filePath = Paths.get(uploadMissionDirectory, fileName);

        byte[] bytes = photoData.getBytes();
        Files.write(filePath, bytes);

        return filePath.toString();
    }

    // 서버에 존재하는 사진을 삭제
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.delete(path);
    }
}
