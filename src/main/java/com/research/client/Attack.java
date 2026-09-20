package com.research.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class Attack {
    public static void tick(MinecraftClient mc) {
        Config c = Config.I;
        if (!c.autoAttack || mc.interactionManager == null) return;
        if (!(mc.targetedEntity instanceof LivingEntity t) || !Targets.valid(mc, t)) return;
        int k = Targets.kind(t);
        if (k == 0 && !c.attackPlayers) return;
        if (k != 0 && !c.attackMobs) return;
        if (mc.player.getAttackCooldownProgress(0.5f) < 1.0f) return;
        mc.interactionManager.attackEntity(mc.player, t);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
