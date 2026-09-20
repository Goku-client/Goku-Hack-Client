package com.research.client;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ResearchClient implements ClientModInitializer {
    private boolean altWasDown = false;

    @Override
    public void onInitializeClient() {
        Config.load();

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        HudRenderCallback.EVENT.register(Esp::render);

        // Fallback for devices without a keyboard: type /hack in chat
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("hack").executes(ctx -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                mc.send(() -> mc.setScreen(new MenuScreen()));
                return Command.SINGLE_SUCCESS;
            })));
    }

    private void tick(MinecraftClient mc) {
        if (mc.getWindow() == null) return;
        boolean alt = GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS;
        if (alt && !altWasDown) {
            if (mc.currentScreen instanceof MenuScreen) mc.currentScreen.close();
            else if (mc.currentScreen == null) mc.setScreen(new MenuScreen());
        }
        altWasDown = alt;

            if (mc.player != null && mc.world != null) {
            Speed.tick(mc);
            AutoTotem.tick(mc);
            if (mc.currentScreen == null) {
                Aim.tick(mc);
                Attack.tick(mc);
            }
        }
    }
}
                                                            }
