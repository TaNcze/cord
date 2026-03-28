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

            // Krótkie opóźnienie żeby frame był gotowy
            new Thread(() -> {
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}

                try {
                    captureWithCoords(client);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private static void captureWithCoords(MinecraftClient client) throws IOException {
        // Pobierz screenshot z framebuffera
        Framebuffer framebuffer = client.getFramebuffer();
        NativeImage nativeImage = ScreenshotCapture.takeScreenshot(framebuffer);

        int width = nativeImage.getWidth();
        int height = nativeImage.getHeight();

        // Konwertuj NativeImage -> BufferedImage
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int abgr = nativeImage.getColor(x, y);
                // NativeImage używa ABGR, konwertuj na ARGB
                int a = (abgr >> 24) & 0xFF;
                int b = (abgr >> 16) & 0xFF;
                int g = (abgr >> 8) & 0xFF;
                int r = abgr & 0xFF;
                buffered.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
            }
        }
        nativeImage.close();

        // Rysuj cordy na obrazie (niewidoczne podczas gry!)
        Graphics2D g2d = buffered.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String line1 = String.format("XYZ: %.2f / %.2f / %.2f", playerX, playerY, playerZ);
        String line2 = String.format("Block: %d %d %d  |  %s  |  %s",
                (int)Math.floor(playerX), (int)Math.floor(playerY), (int)Math.floor(playerZ),
                dimension, timestamp);

        Font font = new Font("Monospaced", Font.BOLD, 16);
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int padding = 8;
        int boxW = Math.max(fm.stringWidth(line1), fm.stringWidth(line2)) + padding * 2;
        int boxH = fm.getHeight() * 2 + padding * 2 + 4;
        int boxX = 6;
        int boxY = height - boxH - 6;

        // Tło
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 8, 8);

        // Obramowanie
        g2d.setColor(new Color(85, 255, 255, 200));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 8, 8);

        // Tekst
        g2d.setColor(new Color(255, 255, 85));
        g2d.drawString(line1, boxX + padding, boxY + padding + fm.getAscent());
        g2d.setColor(Color.WHITE);
        g2d.drawString(line2, boxX + padding, boxY + padding + fm.getAscent() + fm.getHeight() + 2);

        g2d.dispose();

        // Zapisz plik
        File screenshotsDir = new File(client.runDirectory, "screenshots");
        if (!screenshotsDir.exists()) screenshotsDir.mkdirs();
        String filename = "coords_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".png";
        ImageIO.write(buffered, "PNG", new File(screenshotsDir, filename));
    }
}
