package com.research.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Esp {
    private static final int COL_PLAYER = 0xFF33CCFF;
    private static final int COL_HOSTILE = 0xFFFF3333;
    private static final int COL_PASSIVE = 0xFF55FF55;

    // camera basis for the current frame
    private static Vec3d eye, fwd, right, up;
    private static double tanHalf, aspect;
    private static int sw, sh;

    public static void render(DrawContext ctx, RenderTickCounter counter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Config c = Config.I;
        if (!c.esp || mc.player == null || mc.world == null || mc.options.hudHidden) return;

        float delta = counter.getTickProgress(true);
        sw = ctx.getScaledWindowWidth();
        sh = ctx.getScaledWindowHeight();
        setupCamera(mc, delta);

        for (Entity e : mc.world.getEntities()) {
            if (!Targets.valid(mc, e)) continue;
            int k = Targets.kind(e);
            if (k == 0 && !c.espPlayers) continue;
            if (k == 1 && !c.espHostile) continue;
            if (k == 2 && !c.espPassive) continue;
            if (mc.player.distanceTo(e) > c.espRange) continue;

            int color = k == 0 ? COL_PLAYER : k == 1 ? COL_HOSTILE : COL_PASSIVE;
            Vec3d p = e.getLerpedPos(delta);
            double w = e.getWidth() / 2.0, h = e.getHeight();

            if (c.tracers) {
                double[] s = project(new Vec3d(p.x, p.y + h * 0.5, p.z), true);
                if (s != null) {
                    int ox = sw / 2;
                    int oy = c.tracerFromCenter ? sh / 2 : sh;
                    line(ctx, ox, oy, (int) s[0], (int) s[1], color);
                }
            }

            if (c.boxes) {
                double minX = 1e9, minY = 1e9, maxX = -1e9, maxY = -1e9;
                boolean ok = true;
                for (int i = 0; i < 8 && ok; i++) {
                    Vec3d corner = new Vec3d(
                        p.x + ((i & 1) == 0 ? -w : w),
                        p.y + ((i & 2) == 0 ? 0 : h),
                        p.z + ((i & 4) == 0 ? -w : w));
                    double[] s = project(corner, false);
                    if (s == null) { ok = false; break; }
                    minX = Math.min(minX, s[0]); maxX = Math.max(maxX, s[0]);
                    minY = Math.min(minY, s[1]); maxY = Math.max(maxY, s[1]);
                }
                if (ok) {
                    int x1 = (int) minX, y1 = (int) minY, x2 = (int) maxX, y2 = (int) maxY;
                    ctx.fill(x1, y1, x2, y1 + 1, color);
                    ctx.fill(x1, y2 - 1, x2, y2, color);
                    ctx.fill(x1, y1, x1 + 1, y2, color);
                    ctx.fill(x2 - 1, y1, x2, y2, color);
                    if (c.names) {
                        String label = e.getName().getString() + " " + (int) mc.player.distanceTo(e) + "m";
                        ctx.drawTextWithShadow(mc.textRenderer, label, x1, y1 - 10, color);
                    }
                }
            }
        }
    }

    private static void setupCamera(MinecraftClient mc, float delta) {
        var pl = mc.player;
        Vec3d pos = pl.getLerpedPos(delta);
        eye = new Vec3d(pos.x, pos.y + pl.getEyeHeight(pl.getPose()), pos.z);

        double yaw = Math.toRadians(pl.getYaw(delta));
        double pitch = Math.toRadians(pl.getPitch(delta));
        fwd = new Vec3d(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
        right = new Vec3d(-Math.cos(yaw), 0, -Math.sin(yaw));
        up = right.crossProduct(fwd);

        double fov = mc.options.getFov().getValue();
        tanHalf = Math.tan(Math.toRadians(fov) / 2.0);
        aspect = (double) sw / (double) sh;
    }

    /** Returns {x, y} screen coords, or null if behind camera (unless allowBehind, then flipped to the edge). */
    private static double[] project(Vec3d world, boolean allowBehind) {
        Vec3d d = world.subtract(eye);
        double x = d.dotProduct(right), y = d.dotProduct(up), z = d.dotProduct(fwd);
        if (z < 0.05) {
            if (!allowBehind) return null;
            x = -x; y = -y; z = 0.05; // push toward screen edge
            double len = Math.max(Math.abs(x), Math.abs(y));
            if (len > 0) { x = x / len * 1000; y = y / len * 1000; }
        }
        double nx = x / (z * tanHalf * aspect);
        double ny = y / (z * tanHalf);
        double sx = sw / 2.0 * (1 + nx);
        double sy = sh / 2.0 * (1 - ny);
        sx = Math.max(-2000, Math.min(2000, sx));
        sy = Math.max(-2000, Math.min(2000, sy));
        return new double[]{sx, sy};
    }

    private static void line(DrawContext ctx, int x0, int y0, int x1, int y1, int color) {
        int dx = x1 - x0, dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return;
        steps = Math.min(steps, 3000);
        for (int i = 0; i <= steps; i++) {
            int x = x0 + dx * i / steps;
            int y = y0 + dy * i / steps;
            ctx.fill(x, y, x + 1, y + 1, color);
        }
    }
                                              }
