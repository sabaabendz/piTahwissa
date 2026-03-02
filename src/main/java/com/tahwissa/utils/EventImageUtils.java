package com.tahwissa.utils;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

/**
 * Stores event images in a local "images" folder (works on any PC).
 * Uses working directory so path is portable (IDE or JAR).
 */
public final class EventImageUtils {

    private static final String IMAGES_DIR = "images";
    private static final String[] ALLOWED_EXT = { ".jpg", ".jpeg", ".png", ".gif", ".webp" };

    private EventImageUtils() {}

    /** Directory where event images are stored (relative to user.dir). */
    public static Path getImagesDirectory() {
        Path dir = Paths.get(System.getProperty("user.dir"), IMAGES_DIR);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }

    /** Copy a chosen file into the images folder and return the stored filename. */
    public static String saveEventImage(Path sourceFile) {
        if (sourceFile == null || !Files.isRegularFile(sourceFile)) return null;
        String ext = getExtension(sourceFile);
        if (ext == null) ext = ".jpg";
        String filename = "event_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path target = getImagesDirectory().resolve(filename);
        try {
            Files.copy(sourceFile, target);
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Load image for display: first from local images folder, then from classpath /images/. */
    public static Image loadEventImage(String filename) {
        if (filename == null || filename.isBlank()) return null;
        Path local = getImagesDirectory().resolve(filename);
        if (Files.isRegularFile(local)) {
            try {
                return new Image(Files.newInputStream(local));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String resourcePath = "/images/" + filename;
        try (InputStream is = EventImageUtils.class.getResourceAsStream(resourcePath)) {
            if (is != null) return new Image(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Path to the image file on disk (for copying from FileChooser). */
    public static Path getImagePath(String filename) {
        if (filename == null || filename.isBlank()) return null;
        return getImagesDirectory().resolve(filename);
    }

    private static String getExtension(Path path) {
        String name = path.getFileName().toString();
        int i = name.lastIndexOf('.');
        if (i <= 0) return null;
        String ext = name.substring(i).toLowerCase(Locale.ROOT);
        for (String e : ALLOWED_EXT) if (ext.equals(e)) return ext;
        return null;
    }
}
