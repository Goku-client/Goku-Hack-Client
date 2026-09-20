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

    public MenuScreen() { super(Text.literal("Research Client")); }

    @Override
    protected void init() {
        Config c = Config.I;
        int left = width / 2 - W - 6;
        int right = width / 2 + 6;
        int top = 34;
        int y = top;

        // ---- Left column: ESP ----
        toggle(left, y, "ESP", () -> c.esp, v -> c.esp = v); y += GAP;
        toggle(left, y, "Tracers", () -> c.tracers, v -> c.tracers = v); y += GAP;
        toggle(left, y, "Boxes", () -> c.boxes, v -> c.boxes = v); y += GAP;
        toggle(left, y, "Names", () -> c.names, v -> c.names = v); y += GAP;
        toggle(left, y, "Tracer from center", () -> c.tracerFromCenter, v -> c.tracerFromCenter = v); y += GAP;
        toggle(left, y, "Players", () -> c.espPlayers, v -> c.espPlayers = v); y += GAP;
        toggle(left, y, "Hostile mobs", () -> c.espHostile, v -> c.espHostile = v); y += GAP;
        toggle(left, y, "Passive mobs", () -> c.espPassive, v -> c.espPassive = v); y += GAP;
        slider(left, y, "ESP range", 8, 128, () -> c.espRange, v -> c.espRange = v, "m");

        // ---- Right column: Aim ----
        y = top;
        toggle(right, y, "Auto aim", () -> c.aim, v -> c.aim = v); y += GAP;
        toggle(right, y, "Aim at players", () -> c.aimPlayers, v -> c.aimPlayers = v); y += GAP;
        toggle(right, y, "Aim at mobs", () -> c.aimMobs, v -> c.aimMobs = v); y += GAP;
        toggle(right, y, "Aim at head", () -> c.aimHead, v -> c.aimHead = v); y += GAP;
        toggle(right, y, "Visible only", () -> c.aimVisibleOnly, v -> c.aimVisibleOnly = v); y += GAP;
        slider(right, y, "Aim range", 3, 64, () -> c.aimRange, v -> c.aimRange = v, "m"); y += GAP;
        slider(right, y, "Aim FOV", 10, 360, () -> c.aimFov, v -> c.aimFov = v, "°"); y += GAP;
        slider(right, y, "Smoothing", 0.02, 1.0, () -> c.aimSmooth, v -> c.aimSmooth = v, ""); y += GAP;

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
            .dimensions(width / 2 - 50, height - 28, 100, H).build());
    }

    private void toggle(int x, int y, String name, Supplier<Boolean> get, Consumer<Boolean> set) {
        addDrawableChild(ButtonWidget.builder(label(name, get.get()), b -> {
            boolean nv = !get.get();
            set.accept(nv);
            b.setMessage(label(name, nv));
        }).dimensions(x, y, W, H).build());
    }

    private static Text label(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    private void slider(int x, int y, String name, double min, double max,
                        Supplier<Double> get, DoubleConsumer set, String unit) {
        addDrawableChild(new SliderWidget(x, y, W, H, Text.empty(), (get.get() - min) / (max - min)) {
            { updateMessage(); }
            private double val() { return min + value * (max - min); }
            @Override protected void updateMessage() {
                setMessage(Text.literal(name + ": " + String.format("%.2f", val()).replaceAll("\\.?0+$", "") + unit));
            }
            @Override protected void applyValue() { set.accept(val()); }
        });
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "Research Client  (Left Alt or /hack to toggle)", width / 2, 12, 0xFFFFFFFF);
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void close() {
        Config.save();
        super.close();
    }
               }
