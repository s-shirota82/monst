package com.monst.master;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MasterImageService {

    // uploads/masters が基点
    private static final @NonNull Path IMAGE_ROOT = Paths.get("uploads", "masters");
    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "webp");

    public @NonNull String save(@NonNull MultipartFile file, @NonNull String type) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("image is empty");
        }

        String ext = getExtensionLower(file.getOriginalFilename());
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("image extension not allowed: " + ext);
        }

        try {
            Path dir = IMAGE_ROOT.resolve(type);
            Files.createDirectories(dir);

            String filename = type + "_" + UUID.randomUUID() + "." + ext;
            Path target = Objects.requireNonNull(dir.resolve(filename), "resolved image path must not be null");

            file.transferTo(target);

            // DBに保存するパス
            return "uploads/masters/" + type + "/" + filename;

        } catch (Exception e) {
            throw new IllegalStateException("failed to save master image: " + type, e);
        }
    }

    private @NonNull String getExtensionLower(String filename) {
        if (filename == null)
            return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1)
            return "";
        return filename.substring(idx + 1).toLowerCase();
    }
}
