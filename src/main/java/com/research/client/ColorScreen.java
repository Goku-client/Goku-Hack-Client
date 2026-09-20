package com.research.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ColorScreen extends Screen {
    private static final String[] NAMES = {"Players", "Hostile mobs", "Passive mobs"};
    private final Screen parent;
    private int cat = 0;

    public ColorScreen(Screen parent) {
        super(Text.literal("ESP colors"));
        this.parent = parent;
    }

    private int get() {
        Config c = Config.I;
        return cat == 0 ? c.colPlayer : cat == 1 ? c.colHostile : c.colPassive;
    }

    private void set(int rgb) {
        Config c = Config.I;
        rgb &= 0xFFFFFF;
        if (cat == 0) c.colPlayer = rgb; else if (cat == 1) c.colHostile = rgb; else c.colPassive = rgb;
    }

    @Override
    protected void init() {
        int x = width / 2 - 75;
        addDrawableChild(ButtonWidget.builder(Text.literal("Editing: " + NAMES[cat] + " (tap to switch)"),
            b -> { cat = (cat + 1) % 3; clearAndInit(); }).dimensions(x - 25, 30, 200, 20).build());
        channel("Red", 16, x, 60);
        channel("Green", 8, x, 84);
        channel("Blue", 0, x, 108);
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close())
            .dimensions(width / 2 - 50, height - 28, 100, 20).build());
    }

    private void channel(String name, int shift, int x, int y) {
        addDrawableChild(new SliderWidget(x, y, 150, 20, Text.empty(), ((get() >> shift) & 0xFF) / 255.0) {
            { updateMessage(); }
            @Override protected void updateMessage() {
                setMessage(Text.literal(name + ": " + (int) Math.round(value * 255)));
            }
            @Override protected void applyValue() {
                int v = (int) Math.round(value * 255);
                set((get() & ~(0xFF << shift)) | (v << shift));
            }
        });
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.fill(width / 2 - 75, 136, width / 2 + 75, 160, 0xFF000000 | get());
        ctx.drawCenteredTextWithShadow(textRenderer, "Preview", width / 2, 166, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        Config.save();
        client.setScreen(parent);
    }
  }
