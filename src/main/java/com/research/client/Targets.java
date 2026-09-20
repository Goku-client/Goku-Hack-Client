package com.research.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;

public class Targets {
    public static boolean valid(MinecraftClient mc, Entity e) {
        return e instanceof LivingEntity le
            && e != mc.player
            && le.isAlive()
            && !(e instanceof ArmorStandEntity);
    }

    /** 0 = player, 1 = hostile, 2 = passive */
    public static int kind(Entity e) {
        if (e instanceof PlayerEntity) return 0;
        if (e instanceof Monster) return 1;
        return 2;
    }
}
