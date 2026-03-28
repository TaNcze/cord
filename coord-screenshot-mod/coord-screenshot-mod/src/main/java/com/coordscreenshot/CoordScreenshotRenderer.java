package com.coordscreenshot;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoordScreenshotRenderer {

    public static boolean pendingScreenshot = false;
    public static String coordText = "";
    public static double playerX, playerY, playerZ;
    public static String dimension = "";

    private static boolean overlayVisible = false;
    private static int overlayTimer = 0;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            // Always draw overlay if timer active (for screenshot delay)
            if (overlayTimer > 0) {
                drawCoordsOverlay(drawContext, client);
                overlayTimer--;

                // Take the actual screenshot on frame 2 (gives time to render overlay)
                if (overlayTimer == 1) {
                    captureScreenshot(client);
                }
                return;
            }

            // Trigger overlay when screenshot requested
            if (pendingScreenshot) {
                pendingScreenshot = false;
                overlayTimer = 3; // show for 3 frames, capture on frame 2
            }
        });
    }

    private static void drawCoordsOverlay(DrawContext drawContext, MinecraftClient client) {
        TextRenderer textRenderer = client.textRenderer;
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Background panel
        int padding = 6;
        int lineHeight = 12;
        int panelWidth = 320;
        int panelHeight = 14 * 4 + padding * 2;
        int panelX = 5;
        int panelY = screenHeight - panelHeight - 5;

        // Draw semi-transparent background
        drawContext.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xBB000000);

        // Draw border
        drawContext.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFF55FFFF);
        drawContext.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0xFF55FFFF);
        drawContext.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF55FFFF);
        drawContext.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0xFF55FFFF);

        int textX = panelX + padding;
        int textY = panelY + padding;

        // Title
        drawContext.drawTextWithShadow(textRenderer,
                Text.literal("§b§l📍 Coordinates"),
                textX, textY, 0xFFFFFF);
        textY += lineHeight + 2;

        // XYZ
        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(String.format("§fX: §e%.2f  §fY: §e%.2f  §fZ: §e%.2f", playerX, playerY, playerZ)),
                textX, textY, 0xFFFFFF);
        textY += lineHeight;

        // Block coords
        drawContext.drawTextWithShadow(textRenderer,
                Text.literal(String.format("§fBlock: §a%d, %d, %d",
                        (int)Math.floor(playerX), (int)Math.floor(playerY), (int)Math.floor(playerZ))),
                textX, textY, 0xFFFFFF);
        textY += lineHeight;

        // Dimension + timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        drawContext.drawTextWithShadow(textRenderer,
                Text.literal("§fDim: §d" + dimension + "  §7" + timestamp),
                textX, textY, 0xFFFFFF);
    }

    private static void captureScreenshot(MinecraftClient client) {
        File screenshotsDir = new File(client.runDirectory, "screenshots");
        if (!screenshotsDir.exists()) screenshotsDir.mkdirs();

        String filename = "coords_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".png";
        File outputFile = new File(screenshotsDir, filename);

        try {
            Framebuffer framebuffer = client.getFramebuffer();
            NativeImage image = ScreenshotCapture.takeScreenshot(framebuffer);
            image.writeTo(outputFile);
            image.close();

            client.player.sendMessage(
                Text.literal("§a✔ Screenshot z cordami zapisany: §f" + filename), false
            );
        } catch (IOException e) {
            if (client.player != null) {
                client.player.sendMessage(
                    Text.literal("§cBłąd podczas zapisu screenshota: " + e.getMessage()), false
                );
            }
        }
    }
}
