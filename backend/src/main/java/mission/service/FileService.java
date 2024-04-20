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
    @Value("${file.upload.authentication.directory}")
    private String uploadAuthenticationDirectory;
    @Value("${file.upload.mission.directory}")
    private String uploadMissionDirectory;
    private static final String MISSION_DIR = "missions/";
    private static final String AUTHENTICATION_DIR = "authentications/";

    // 사진이 존재하면 해당 사진을 서버에 저장
    public String uploadFile(MultipartFile photoData, String dirCheck) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".jpg"; // 확장자는 이미지 형식에 맞게 변경
        Path filePath = dirCheck == MISSION_DIR ? Paths.get(uploadMissionDirectory, fileName) : Paths.get(uploadAuthenticationDirectory, fileName);

        byte[] bytes = photoData.getBytes();
        Files.write(filePath, bytes);

        return filePath.toString();
    }

    // 서버에 존재하는 사진을 삭제
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.delete(path);
    }
}
