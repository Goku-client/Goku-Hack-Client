package com.research.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

public class AutoTotem {
    private static int cooldown = 0;

    public static void tick(MinecraftClient mc) {
        Config c = Config.I;
        if (cooldown > 0) { cooldown--; return; }
        if (!c.totemAuto && !c.totemHover) return;
        if (mc.interactionManager == null) return;
        if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;

        List<Slot> slots = mc.player.playerScreenHandler.slots;

        // Hover mode: totem under the mouse in the inventory screen goes to the offhand
        if (c.totemHover && mc.currentScreen instanceof InventoryScreen s) {
            double mx = mc.mouse.getX() * s.width / mc.getWindow().getWidth();
            double my = mc.mouse.getY() * s.height / mc.getWindow().getHeight();
            int gx = (s.width - 176) / 2, gy = (s.height - 166) / 2;
            for (int i = 9; i <= 44 && i < slots.size(); i++) {
                Slot sl = slots.get(i);
                if (mx >= gx + sl.x && mx < gx + sl.x + 16 && my >= gy + sl.y && my < gy + sl.y + 16) {
                    if (sl.getStack().isOf(Items.TOTEM_OF_UNDYING)) { swap(mc, i); return; }
                    break;
                }
            }
        }

        // Auto mode: refill the offhand from anywhere in the inventory
        if (c.totemAuto && mc.currentScreen == null) {
            for (int i = 9; i <= 44 && i < slots.size(); i++) {
                if (slots.get(i).getStack().isOf(Items.TOTEM_OF_UNDYING)) { swap(mc, i); return; }
            }
        }
    }

    private static void swap(MinecraftClient mc, int slotId) {
        mc.interactionManager.clickSlot(0, slotId, 40, SlotActionType.SWAP, mc.player);
        cooldown = (int) Math.round(Config.I.totemDelay);
    }
    }
