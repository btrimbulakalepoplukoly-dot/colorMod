package com.colormod.client.gui;

import com.colormod.config.ColorConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ColorPickerScreen extends Screen {

    // ── Layout ──────────────────────────────────────────────────────────────
    private static final int PANEL_W  = 260;
    private static final int PANEL_H  = 240;
    private static final int SLIDER_W = 210;
    private static final int SLIDER_H = 20;
    private static final int BTN_W    = 98;
    private static final int BTN_H    = 20;

    // ── UI Colors (Minecraft palette) ───────────────────────────────────────
    private static final int COL_BACKDROP  = 0xC0000000;
    private static final int COL_PANEL_BG  = 0xFF2D2D2D;
    private static final int COL_PANEL_BD  = 0xFF6E6E6E;
    private static final int COL_HEADER    = 0xFF3A3A3A;
    private static final int COL_DIVIDER   = 0xFF505050;
    private static final int COL_TITLE     = 0xFFFFFFFF;
    private static final int COL_LABEL     = 0xFFCCCCCC;
    private static final int COL_CHECKER_A = 0xFFAAAAAA;
    private static final int COL_CHECKER_B = 0xFF888888;

    private final Screen parent;

    // Working state — committed only on Save
    private float red, green, blue, opacity;

    public ColorPickerScreen(Screen parent) {
        super(Text.translatable("colormod.screen.title"));
        this.parent = parent;
        ColorConfig c = ColorConfig.get();
        red     = c.red;
        green   = c.green;
        blue    = c.blue;
        opacity = c.opacity;
    }

    // ── Widget init ─────────────────────────────────────────────────────────
    @Override
    protected void init() {
        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;
        int sx = px + (PANEL_W - SLIDER_W) / 2;
        int sy = py + 50;
        int gap = 28;

        // ── Red slider ────────────────────────────────────────────────────
        addDrawableChild(new SliderWidget(sx, sy, SLIDER_W, SLIDER_H,
                Text.translatable("colormod.color.red"), red) {
            @Override protected void updateMessage() {
                setMessage(Text.literal(
                    Text.translatable("colormod.color.red").getString()
                    + ": " + (int)(value * 255)));
            }
            @Override protected void applyValue() { red = (float) value; }
        });

        // ── Green slider ──────────────────────────────────────────────────
        addDrawableChild(new SliderWidget(sx, sy + gap, SLIDER_W, SLIDER_H,
                Text.translatable("colormod.color.green"), green) {
            @Override protected void updateMessage() {
                setMessage(Text.literal(
                    Text.translatable("colormod.color.green").getString()
                    + ": " + (int)(value * 255)));
            }
            @Override protected void applyValue() { green = (float) value; }
        });

        // ── Blue slider ───────────────────────────────────────────────────
        addDrawableChild(new SliderWidget(sx, sy + gap * 2, SLIDER_W, SLIDER_H,
                Text.translatable("colormod.color.blue"), blue) {
            @Override protected void updateMessage() {
                setMessage(Text.literal(
                    Text.translatable("colormod.color.blue").getString()
                    + ": " + (int)(value * 255)));
            }
            @Override protected void applyValue() { blue = (float) value; }
        });

        // ── Opacity slider ────────────────────────────────────────────────
        addDrawableChild(new SliderWidget(sx, sy + gap * 3, SLIDER_W, SLIDER_H,
                Text.translatable("colormod.color.opacity"), opacity) {
            @Override protected void updateMessage() {
                setMessage(Text.literal(
                    Text.translatable("colormod.color.opacity").getString()
                    + ": " + (int)(value * 100) + "%"));
            }
            @Override protected void applyValue() { opacity = (float) value; }
        });

        // ── Buttons ───────────────────────────────────────────────────────
        int bY = py + PANEL_H - BTN_H - 14;
        int bX = px + (PANEL_W - BTN_W * 2 - 8) / 2;

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormod.button.save"), btn -> {
                    ColorConfig cfg = ColorConfig.get();
                    cfg.red     = red;
                    cfg.green   = green;
                    cfg.blue    = blue;
                    cfg.opacity = opacity;
                    ColorConfig.save();
                    if (client != null) client.setScreen(parent);
                })
                .dimensions(bX, bY, BTN_W, BTN_H)
                .build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormod.button.exit"),
                btn -> { if (client != null) client.setScreen(parent); })
                .dimensions(bX + BTN_W + 8, bY, BTN_W, BTN_H)
                .build());
    }

    // ── Rendering ────────────────────────────────────────────────────────────
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;

        // Full-screen dim
        ctx.fill(0, 0, this.width, this.height, COL_BACKDROP);

        // Drop shadow
        ctx.fill(px + 4, py + 4, px + PANEL_W + 4, py + PANEL_H + 4, 0x55000000);

        // Panel body
        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, COL_PANEL_BG);
        drawBorder(ctx, px, py, PANEL_W, PANEL_H, COL_PANEL_BD);

        // Title bar
        ctx.fill(px + 1, py + 1, px + PANEL_W - 1, py + 25, COL_HEADER);
        ctx.fill(px + 1, py + 25, px + PANEL_W - 1, py + 26, COL_DIVIDER);
        ctx.drawCenteredTextWithShadow(textRenderer, title,
                this.width / 2, py + 9, COL_TITLE);

        // ── Color preview swatches ─────────────────────────────────────────
        int swY  = py + PANEL_H - 74;
        int swSz = 30;
        int oldX = px + PANEL_W / 2 - swSz - 10;
        int newX = px + PANEL_W / 2 + 10;

        // Labels above swatches
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("colormod.label.current"),
                oldX + swSz / 2, swY - 10, COL_LABEL);
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.translatable("colormod.label.new"),
                newX + swSz / 2, swY - 10, COL_LABEL);

        // Checker backgrounds (simulate transparency)
        drawChecker(ctx, oldX, swY, swSz);
        drawChecker(ctx, newX, swY, swSz);

        // Saved colour swatch
        ColorConfig cfg = ColorConfig.get();
        ctx.fill(oldX, swY, oldX + swSz, swY + swSz, cfg.toARGB(255));

        // Live preview swatch (always opaque so you can see the hue)
        int newColor = 0xFF000000
                | ((int)(red   * 255) << 16)
                | ((int)(green * 255) << 8)
                |  (int)(blue  * 255);
        ctx.fill(newX, swY, newX + swSz, swY + swSz, newColor);

        // Swatch borders
        drawBorder(ctx, oldX, swY, swSz, swSz, COL_PANEL_BD);
        drawBorder(ctx, newX, swY, swSz, swSz, COL_PANEL_BD);

        // Hex code for the new colour
        String hex = String.format("#%02X%02X%02X",
                (int)(red * 255), (int)(green * 255), (int)(blue * 255));
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal(hex),
                this.width / 2, swY + swSz + 4, COL_LABEL);

        super.render(ctx, mouseX, mouseY, delta);
    }

    // ── Util ─────────────────────────────────────────────────────────────────
    private static void drawBorder(DrawContext ctx,
                                   int x, int y, int w, int h, int color) {
        ctx.fill(x,         y,         x + w,     y + 1,     color);
        ctx.fill(x,         y + h - 1, x + w,     y + h,     color);
        ctx.fill(x,         y,         x + 1,     y + h,     color);
        ctx.fill(x + w - 1, y,         x + w,     y + h,     color);
    }

    private static void drawChecker(DrawContext ctx, int x, int y, int size) {
        int cell = 5;
        for (int cx = x; cx < x + size; cx += cell) {
            for (int cy = y; cy < y + size; cy += cell) {
                int x2  = Math.min(cx + cell, x + size);
                int y2  = Math.min(cy + cell, y + size);
                boolean even = (((cx - x) / cell) + ((cy - y) / cell)) % 2 == 0;
                ctx.fill(cx, cy, x2, y2, even ? COL_CHECKER_A : COL_CHECKER_B);
            }
        }
    }

    @Override
    public boolean shouldPause() { return false; }
}
