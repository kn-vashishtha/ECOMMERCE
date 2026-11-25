package org.bootcamp.ecommerce.services;

import org.bootcamp.ecommerce.entity.ProductVariation;
import org.bootcamp.ecommerce.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    String uploadUserImageToFileSystem(MultipartFile file, User user) throws IOException;

    String getUserImageFromFileSystem(User user);

    void checkFolderExists(String path);

    void checkValid(MultipartFile file);

    String uploadProductVariationImage(MultipartFile file, String productId, ProductVariation variation) throws IOException;

    void uploadSecondaryVariationImages(List<MultipartFile> files, String productId, ProductVariation variation) throws IOException;

    String getVariationImageUrl(ProductVariation variation);

    List<String> getSecondaryVariationImageUrls(ProductVariation variation);

    void deletePrimaryVariationImage(ProductVariation variation);

    void deleteSecondaryVariationImages(ProductVariation variation);


}
