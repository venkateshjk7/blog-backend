package simple.blog.blogsite.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class StorageService {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    public String save(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + fileName;
        file.transferTo(new File(filePath));

        // Return a URL path instead of local file path
        return "/uploads/" + fileName;
    }
}


