package com.coordscreenshot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class CoordScreenshotClient implements ClientModInitializer {

    private static KeyBinding screenshotKey;
    private static boolean wasPressed = false;

    @Override
    public void onInitializeClient() {
        // Rejestracja klawisza (domyślnie F2, można zmienić w Ustawienia > Sterowanie)
        screenshotKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.coordscreenshot.take",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F2,
                "category.coordscreenshot"
        ));

        // Rejestracja renderera HUD z nałożonymi cordami
        CoordScreenshotRenderer.register();

        // Obsługa naciśnięcia klawisza
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isPressed = screenshotKey.isPressed();
            if (isPressed && !wasPressed) {
                triggerScreenshot(client);
            }
            wasPressed = isPressed;
        });
    }

    private void triggerScreenshot(MinecraftClient client) {
        if (client.player == null || client.world == null) return;

        PlayerEntity player = client.player;

        CoordScreenshotRenderer.playerX = player.getX();
        CoordScreenshotRenderer.playerY = player.getY();
        CoordScreenshotRenderer.playerZ = player.getZ();

        String dim = client.world.getRegistryKey().getValue().toString();
        CoordScreenshotRenderer.dimension = dim.contains("nether") ? "Nether" :
                                             dim.contains("end") ? "The End" : "Overworld";

        CoordScreenshotRenderer.pendingScreenshot = true;
    }
}
