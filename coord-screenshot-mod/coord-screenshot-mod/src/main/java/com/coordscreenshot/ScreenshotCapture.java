package com.coordscreenshot;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ScreenshotCapture {

    public static NativeImage takeScreenshot(Framebuffer framebuffer) {
        int width = framebuffer.textureWidth;
        int height = framebuffer.textureHeight;

        NativeImage image = new NativeImage(width, height, false);

        RenderSystem.bindTexture(framebuffer.getColorAttachment());
        image.loadFromTextureImage(0, false);
        image.mirrorVertically();

        return image;
    }
}
