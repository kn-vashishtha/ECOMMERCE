package org.bootcamp.ecommerce.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bootcamp.ecommerce.entity.ProductVariation;
import org.bootcamp.ecommerce.entity.User;
import org.bootcamp.ecommerce.exception.FileSavingException;
import org.bootcamp.ecommerce.exception.ImageNotValidException;
import org.bootcamp.ecommerce.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "bmp");
    @Value("${image.base_path}")
    private String BASE_PATH;

    public String uploadUserImageToFileSystem(MultipartFile file, User user) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot != -1 && dot < originalFilename.length() - 1) {
            ext = originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT);
        }
        String userIdStr = user.getId().toString();
        Path usersDir = Path.of(BASE_PATH, "users");
        String targetFileName = userIdStr + "." + ext;
        Path targetPath = usersDir.resolve(targetFileName);
        try {
            Files.createDirectories(usersDir);
            for (String allowedExt : ALLOWED_IMAGE_EXTENSIONS) {
                Path p = usersDir.resolve(userIdStr + "." + allowedExt);
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            }
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "File uploaded successfully: " + targetPath.toAbsolutePath().toString().toLowerCase();
        } catch (IOException ex) {
            throw new FileSavingException();
        }
    }

    public String getUserImageFromFileSystem(User user) {
        String userIdStr = user.getId().toString();
        File usersRoot = new File(BASE_PATH + File.separator + "users");

        if (!usersRoot.exists() || !usersRoot.isDirectory()) {
            return null;
        }
        for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
            File candidate = new File(usersRoot, userIdStr + "." + ext);
            if (candidate.exists() && candidate.isFile()) {
                return ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/users/").path(candidate.getName()).toUriString();
            }
        }
        return null;

    }

    public void checkFolderExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void checkValid(MultipartFile file) {
        if (file == null) return;
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            throw new ImageNotValidException(ALLOWED_IMAGE_EXTENSIONS.toString());
        }
    }

    @Override
    public String uploadProductVariationImage(MultipartFile file, String productId, ProductVariation variation) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        }

        Path variationDir = Path.of(BASE_PATH, "products", productId, "variations");
        Files.createDirectories(variationDir);

        String targetFileName = variation.getId().toString() + "." + ext;
        Path targetPath = variationDir.resolve(targetFileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully uploaded variation image to {}", targetPath);
            return targetFileName;
        } catch (IOException ex) {
            log.error("Failed to store variation image file", ex);
            throw new FileSavingException();
        }
    }

    @Override
    public void uploadSecondaryVariationImages(List<MultipartFile> files, String productId, ProductVariation variation) throws IOException {
        if (files == null || files.isEmpty()) {
            return;
        }
        Path variationDir = Path.of(BASE_PATH, "products", productId, "variations");
        Files.createDirectories(variationDir);

        int counter = 1;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
            }

            String targetFileName = String.format("%s_secondary_%d.%s", variation.getId().toString(), counter++, ext);
            Path targetPath = variationDir.resolve(targetFileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully uploaded secondary image to {}", targetPath);

        }
    }

    @Override
    public String getVariationImageUrl(ProductVariation variation) {
        if (variation == null || variation.getPrimaryImageName() == null || variation.getProduct() == null) {
            return null;
        }
        String productId = variation.getProduct().getId().toString();
        String imageName = variation.getPrimaryImageName();
        File variationImageFile = new File(BASE_PATH + File.separator + "products" + File.separator + productId + File.separator + "variations" + File.separator + imageName);
        if (!variationImageFile.exists()) {
            return null;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/products/").path(productId).path("/variations/").path(imageName).toUriString();
    }

    @Override
    public List<String> getSecondaryVariationImageUrls(ProductVariation variation) {
        if (variation == null || variation.getProduct() == null) {
            return Collections.emptyList();
        }

        String productId = variation.getProduct().getId().toString();
        String variationId = variation.getId().toString();
        File variationDir = new File(BASE_PATH, "products" + File.separator + productId + File.separator + "variations");

        if (!variationDir.exists() || !variationDir.isDirectory()) {
            return Collections.emptyList();
        }

        List<String> urls = new ArrayList<>();
        File[] filesInDir = variationDir.listFiles();
        if (filesInDir == null) return Collections.emptyList();

        final String prefix = variationId + "_secondary_";
        for (File file : filesInDir) {
            if (file.isFile() && file.getName().startsWith(prefix)) {
                String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/files/products/")
                        .path(productId)
                        .path("/variations/")
                        .path(file.getName())
                        .toUriString();
                urls.add(url);
            }
        }
        return urls;
    }

    @Override
    public void deletePrimaryVariationImage(ProductVariation variation) {
        if (variation == null || variation.getPrimaryImageName() == null) return;

        Path variationDir = getVariationDirectoryPath(variation);
        String primaryImageName = variation.getPrimaryImageName();

        try {
            Path imagePath = variationDir.resolve(primaryImageName);
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                log.info("Deleted primary image: {}", imagePath);
            }
        } catch (IOException e) {
            log.error("Could not delete primary image for variation ID {}", variation.getId(), e);
        }
    }

    @Override
    public void deleteSecondaryVariationImages(ProductVariation variation) {
        if (variation == null) return;

        Path variationDir = getVariationDirectoryPath(variation);
        if (!Files.exists(variationDir)) return;

        final String prefix = variation.getId().toString() + "_secondary_";

        try (var stream = Files.list(variationDir)) {
            stream.filter(path -> path.getFileName().toString().startsWith(prefix))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.info("Deleted secondary image: {}", path);
                        } catch (IOException e) {
                            log.error("Could not delete secondary image {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("Could not list secondary images for deletion for variation ID {}", variation.getId(), e);
        }
    }

    private Path getVariationDirectoryPath(ProductVariation variation) {
        String productId = variation.getProduct().getId().toString();
        return Path.of(BASE_PATH, "products", productId, "variations");
    }

}
