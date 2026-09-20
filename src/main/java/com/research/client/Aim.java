package com.research.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class Aim {
    public static void tick(MinecraftClient mc) {
        Config c = Config.I;
        if (!c.aim) return;

        Vec3d eye = mc.player.getEyePos();
        float curYaw = mc.player.getYaw();
        float curPitch = mc.player.getPitch();

        Entity best = null;
        double bestAngle = Double.MAX_VALUE;
        float bestYaw = 0, bestPitch = 0;

        for (Entity e : mc.world.getEntities()) {
            if (!Targets.valid(mc, e)) continue;
            int k = Targets.kind(e);
            if (k == 0 && !c.aimPlayers) continue;
            if (k != 0 && !c.aimMobs) continue;
            if (mc.player.distanceTo(e) > c.aimRange) continue;

            Vec3d target = c.aimHead
                ? e.getEyePos()
                : new Vec3d(e.getX(), e.getY() + e.getHeight() * 0.5, e.getZ());

            double dx = target.x - eye.x, dy = target.y - eye.y, dz = target.z - eye.z;
            float ty = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float tp = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));

            float dyaw = MathHelper.wrapDegrees(ty - curYaw);
            float dpitch = tp - curPitch;
            double angle = Math.hypot(dyaw, dpitch);
            if (angle > c.aimFov / 2.0) continue;

            if (c.aimVisibleOnly) {
                var hit = mc.world.raycast(new RaycastContext(eye, target,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
                if (hit.getType() != HitResult.Type.MISS) continue;
            }

            if (angle < bestAngle) {
                bestAngle = angle; best = e; bestYaw = ty; bestPitch = tp;
            }
        }

        if (best == null) return;

        float s = (float) MathHelper.clamp(c.aimSmooth, 0.02, 1.0);
        float newYaw = curYaw + MathHelper.wrapDegrees(bestYaw - curYaw) * s;
        float newPitch = MathHelper.clamp(curPitch + (bestPitch - curPitch) * s, -90f, 90f);
        mc.player.setYaw(newYaw);
        mc.player.setPitch(newPitch);
    }
                }
