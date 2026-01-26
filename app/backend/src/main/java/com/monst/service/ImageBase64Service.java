package com.monst.service;

import com.monst.dto.response.MonsterFullResponse.ImageData;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class ImageBase64Service {

    public ImageData load(String relativePath, boolean includeBase64) {
        if (relativePath == null || relativePath.isBlank()) {
            return null;
        }

        try {
            Path p = Paths.get(relativePath);
            if (!Files.exists(p) || !Files.isRegularFile(p)) {
                return new ImageData(relativePath, null, null);
            }

            String mimeType = Files.probeContentType(p);
            if (mimeType == null) {
                mimeType = guessMimeTypeByExt(relativePath);
            }

            if (!includeBase64) {
                // base64は載せない（一覧の軽量化）
                return new ImageData(relativePath, mimeType, null);
            }

            byte[] bytes = Files.readAllBytes(p);
            String base64 = Base64.getEncoder().encodeToString(bytes);

            return new ImageData(relativePath, mimeType, base64);

        } catch (Exception e) {
            return new ImageData(relativePath, null, null);
        }
    }

    private String guessMimeTypeByExt(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".png"))
            return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
            return "image/jpeg";
        if (lower.endsWith(".webp"))
            return "image/webp";
        return "application/octet-stream";
    }
}
