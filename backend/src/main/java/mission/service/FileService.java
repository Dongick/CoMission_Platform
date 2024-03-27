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
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    public String uploadFile(MultipartFile photoData) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".jpg"; // 확장자는 이미지 형식에 맞게 변경
        Path filePath = Paths.get(uploadDirectory, fileName);

        byte[] bytes = photoData.getBytes();
        Files.write(filePath, bytes);

        return filePath.toString();
    }

    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.delete(path);
    }
}
