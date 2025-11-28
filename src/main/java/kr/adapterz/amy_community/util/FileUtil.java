package kr.adapterz.amy_community.util;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class FileUtil {

    public String saveBase64Image(String base64, String uploadDir, String filename) throws Exception {

        if (base64.contains(",")) {
            base64 = base64.split(",", 2)[1];
        }

        // Base64 → byte[]
        byte[] decoded = Base64.decodeBase64(base64);

        // 디렉토리 생성 (없는 경우 자동 생성)
        Files.createDirectories(Paths.get(uploadDir));

        // 실제 저장 경로
        String filePath = uploadDir + filename;

        try (OutputStream stream = new FileOutputStream(filePath)) {
            stream.write(decoded);
        }

        return filename;
    }
}