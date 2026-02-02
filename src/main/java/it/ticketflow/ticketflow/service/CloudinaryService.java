package it.ticketflow.ticketflow.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.ticketflow.ticketflow.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new BadRequestException("Il file è vuoto");
        }

        // Verifica che sia un'immagine
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Il file deve essere un'immagine");
        }

        try {
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "ticketflow/" + folder,
                    "resource_type", "image"
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String secureUrl = (String) uploadResult.get("secure_url");

            log.info("Immagine caricata con successo: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("Errore durante l'upload dell'immagine", e);
            throw new BadRequestException("Errore durante l'upload dell'immagine: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            // Estrai il public_id dall'URL
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Immagine eliminata: {}", publicId);
            }
        } catch (IOException e) {
            log.warn("Errore durante l'eliminazione dell'immagine: {}", e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        // URL format: https://res.cloudinary.com/{cloud}/image/upload/v{version}/{public_id}.{ext}
        try {
            String[] parts = url.split("/upload/");
            if (parts.length > 1) {
                String pathWithVersion = parts[1];
                // Rimuovi la versione se presente (v1234567890/)
                if (pathWithVersion.matches("v\\d+/.*")) {
                    pathWithVersion = pathWithVersion.substring(pathWithVersion.indexOf("/") + 1);
                }
                // Rimuovi l'estensione
                int lastDot = pathWithVersion.lastIndexOf(".");
                if (lastDot > 0) {
                    return pathWithVersion.substring(0, lastDot);
                }
                return pathWithVersion;
            }
        } catch (Exception e) {
            log.warn("Impossibile estrarre public_id dall'URL: {}", url);
        }
        return null;
    }
}