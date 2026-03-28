package com.coordscreenshot;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoordScreenshotRenderer {

    public static boolean pendingScreenshot = false;
    public static double playerX, playerY, playerZ;
    public static String dimension = "";

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (!pendingScreenshot) return;
            pendingScreenshot = false;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            // Capture musi być na wątku render (tutaj)
            Framebuffer framebuffer = client.getFramebuffer();
            NativeImage nativeImage = ScreenshotCapture.takeScreenshot(framebuffer);
            int width = nativeImage.getWidth();
            int height = nativeImage.getHeight();

            // Konwertuj do BufferedImage
            BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int abgr = nativeImage.getColor(x, y);
                    int r = abgr & 0xFF;
                    int g = (abgr >> 8) & 0xFF;
                    int b = (abgr >> 16) & 0xFF;
                    buffered.setRGB(x, y, (r << 16) | (g << 8) | b);
                }
            }
            nativeImage.close();

            // Rysowanie i zapis na osobnym wątku (nie blokuje gry)
            double cx = playerX, cy = playerY, cz = playerZ;
            String dim = dimension;
            new Thread(() -> {
                try {
                    drawAndSave(buffered, cx, cy, cz, dim, client.runDirectory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private static void drawAndSave(BufferedImage img, double x, double y, double z,
                                     String dim, File runDir) throws IOException {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String line1 = String.format("XYZ: %.2f / %.2f / %.2f", x, y, z);
        String line2 = String.format("Block: %d %d %d  |  %s  |  %s",
                (int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z), dim, timestamp);

        Font font = new Font("Monospaced", Font.BOLD, 16);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        int pad = 8;
        int boxW = Math.max(fm.stringWidth(line1), fm.stringWidth(line2
