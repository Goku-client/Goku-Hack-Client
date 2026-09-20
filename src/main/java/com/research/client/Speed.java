package com.research.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

public class Speed {
    private static final Identifier ID = Identifier.of("researchclient", "speed");

    public static void tick(MinecraftClient mc) {
        EntityAttributeInstance attr = mc.player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (attr == null) return;
        double want = Config.I.speed ? 0.2 * Math.round(Config.I.speedLevel) : 0;
        EntityAttributeModifier cur = attr.getModifier(ID);
        if (want == 0) {
            if (cur != null) attr.removeModifier(ID);
            return;
        }
        if (cur == null || cur.value() != want) {
            attr.removeModifier(ID);
            attr.addTemporaryModifier(new EntityAttributeModifier(ID, want,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }
}
