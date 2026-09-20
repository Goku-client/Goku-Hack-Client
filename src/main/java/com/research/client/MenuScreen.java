package com.research.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Supplier;

public class MenuScreen extends Screen {
    private static final int W = 150, H = 20, GAP = 24;
    private static final String[] TABS = {"ESP", "Aim", "Combat", "Move"};
    private static int tab = 0;
    private int slot, left, right, top;

    public MenuScreen() { super(Text.literal("Research Client")); }

    @Override
    protected void init() {
        Config c = Config.I;
        int tw = 72;
        int startX = width / 2 - (TABS.length * tw + (TABS.length - 1) * 4) / 2;
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            addDrawableChild(ButtonWidget.builder(Text.literal((tab == i ? "> " : "") + TABS[i]), b -> { tab = idx; clearAndInit(); })
                .dimensions(startX + i * (tw + 4), 24, tw, H).build());
        }
        left = width / 2 - W - 6;
        right = width / 2 + 6;
        top = 52;
        slot = 0;

        if (tab == 0) {
            toggle("ESP", () -> c.esp, v -> c.esp = v);
            toggle("Tracers", () -> c.tracers, v -> c.tracers = v);
            toggle("Boxes", () -> c.boxes, v -> c.boxes = v);
            toggle("Names", () -> c.names, v -> c.names = v);
            toggle("Tracer from center", () -> c.tracerFromCenter, v -> c.tracerFromCenter = v);
            toggle("Players", () -> c.espPlayers, v -> c.espPlayers = v);
            toggle("Hostile mobs", () -> c.espHostile, v -> c.espHostile = v);
            toggle("Passive mobs", () -> c.espPassive, v -> c.espPassive = v);
            slider("ESP range", 8, 128, 1, () -> c.espRange, v -> c.espRange = v, "m");
            addDrawableChild(ButtonWidget.builder(Text.literal("ESP colors..."), b -> client.setScreen(new ColorScreen(this)))
                .dimensions(nx(), ny(), W, H).build());
            slot++;
        } else if (tab == 1) {
            toggle("Auto aim", () -> c.aim, v -> c.aim = v);
            toggle("Aim at players", () -> c.aimPlayers, v -> c.aimPlayers = v);
            toggle("Aim at mobs", () -> c.aimMobs, v -> c.aimMobs = v);
            toggle("Aim at head", () -> c.aimHead, v -> c.aimHead = v);
            toggle("Visible only", () -> c.aimVisibleOnly, v -> c.aimVisibleOnly = v);
            slider("Aim range", 3, 64, 1, () -> c.aimRange, v -> c.aimRange = v, "m");
            slider("Aim FOV", 10, 360, 5, () -> c.aimFov, v -> c.aimFov = v, "°");
            slider("Smoothing", 0.02, 1.0, 0, () -> c.aimSmooth, v -> c.aimSmooth = v, "");
        } else if (tab == 2) {
            toggle("Auto attack", () -> c.autoAttack, v -> c.autoAttack = v);
            toggle("Attack players", () -> c.attackPlayers, v -> c.attackPlayers = v);
            toggle("Attack mobs", () -> c.attackMobs, v -> c.attackMobs = v);
            toggle("Auto totem", () -> c.totemAuto, v -> c.totemAuto = v);
            toggle("Totem on hover", () -> c.totemHover, v -> c.totemHover = v);
            slider("Totem delay", 0, 20, 1, () -> c.totemDelay, v -> c.totemDelay = v, " ticks");
        } else {
            toggle("Speed", () -> c.speed, v -> c.speed = v);
            slider("Speed level", 1, 10, 1, () -> c.speedLevel, v -> c.speedLevel = v, "");
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
            .dimensions(width / 2 - 50, height - 28, 100, H).build());
    }

    private int nx() { return slot % 2 == 0 ? left : right; }
    private int ny() { return top + (slot / 2) * GAP; }

    private void toggle(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
        int x = nx(), y = ny();
        slot++;
        addDrawableChild(ButtonWidget.builder(label(name, get.get()), b -> {
            boolean nv = !get.get();
            set.accept(nv);
            b.setMessage(label(name, nv));
        }).dimensions(x, y, W, H).build());
    }

    private static Text label(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    private void slider(String name, double min, double max, double step,
                        Supplier<Double> get, DoubleConsumer set, String unit) {
        int x = nx(), y = ny();
        slot++;
        addDrawableChild(new SliderWidget(x, y, W, H, Text.empty(), (get.get() - min) / (max - min)) {
            { updateMessage(); }
            private double val() {
                double v = min + value * (max - min);
                if (step > 0) v = Math.round(v / step) * step;
                return v;
            }
            @Override protected void updateMessage() {
                setMessage(Text.literal(name + ": " + String.format("%.2f", val()).replaceAll("\\.?0+$", "") + unit));
            }
            @Override protected void applyValue() { set.accept(val()); }
        });
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "Research Client  (Left Alt or /hack)", width / 2, 8, 0xFFFFFFFF);
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void close() {
        Config.save();
        super.close();
    }
                   }
