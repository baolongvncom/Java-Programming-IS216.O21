package com.Gomoku;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class ImageStorage {
    private static BufferedImage imgX;
    private static BufferedImage imgY;

    static {
        try {
            // Đọc file hình ảnh X O
            imgX = ImageIO.read(Objects.requireNonNull(ImageStorage.class.getResource("/com/Gomoku/GomokuX.png")));
            imgY = ImageIO.read(Objects.requireNonNull(ImageStorage.class.getResource("/com/Gomoku/GomokuO.png")));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getXImage() {
        return imgX;
    }

    public static BufferedImage getOImage() {
        return imgY;
    }
}
