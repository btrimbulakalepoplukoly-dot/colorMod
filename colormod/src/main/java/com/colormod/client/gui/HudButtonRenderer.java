package com.colormod.client.gui;

import com.colormod.config.ColorConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Renders the small "Color" button in the bottom-left corner of the HUD.
 * Actual click detection is handled by MouseMixin.
 */
public class HudButtonRenderer {

    // ── Button geometry ──────────────────────────────────────────────────────
    public static final int BTN_X = 6;
    public static final int BTN_Y_FROM_BOTTOM = 26; // distance from screen bottom
    public static final int BTN_W = 46;
    public static final int BTN_H = 16;

    /** Returns the button's top-left Y in GUI coordinates for a given screen height. */
    public static int getBtnY(int screenHeight) {
        return screenHeight - BTN_Y_FROM_BOTTOM;
    }

    /** Returns true when (mx, my) is inside the button. */
    public static boolean isHovered(double mx, double my, int screenHeight) {
        int by = getBtnY(screenHeight);
        return mx >= BTN_X && mx <= BTN_X + BTN_W
                && my >= by && my <= by + BTN_H;
    }

    // ── Colors ───────────────────────────────────────────────────────────────
    private static final int COL_BTN_NORMAL  = 0xFF555555;
    private static final int COL_BTN_HOVER   = 0xFF777777;
    private static final int COL_BTN_BORDER  = 0xFF999999;
    private static final int COL_BTN_SHADOW  = 0xFF333333;
    private static final int COL_TEXT        = 0xFFFFFFFF;
    private static final int COL_DOT_BORDER  = 0xFF222222;

    // ── Registration ─────────────────────────────────────────────────────────
    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            // Only show when no screen is open
            if (client.currentScreen != null) return;

            int screenH = client.getWindow().getScaledHeight();
            int screenW = client.getWindow().getScaledWidth();

            double mouseX = client.mouse.getX()
                    * screenW / client.getWindow().getWidth();
            double mouseY = client.mouse.getY()
                    * screenH / client.getWindow().getHeight();

            boolean hovered = isHovered(mouseX, mouseY, screenH);

            renderButton(drawContext, screenH, hovered);

            // Subtle full-screen colour tint while in-game
            ColorConfig cfg = ColorConfig.get();
            if (cfg.opacity > 0.005f) {
                drawContext.fill(0, 0, screenW, screenH, cfg.toARGB());
            }
        });
    }

    // ── Drawing ──────────────────────────────────────────────────────────────
    private static void renderButton(DrawContext ctx, int screenH, boolean hovered) {
        MinecraftClient client = MinecraftClient.getInstance();
        int by = getBtnY(screenH);

        // Drop shadow
        ctx.fill(BTN_X + 2, by + 2, BTN_X + BTN_W + 2, by + BTN_H + 2, 0x55000000);

        // Button fill
        int fill = hovered ? COL_BTN_HOVER : COL_BTN_NORMAL;
        ctx.fill(BTN_X, by, BTN_X + BTN_W, by + BTN_H, fill);

        // Highlight top edge (bevel effect)
        ctx.fill(BTN_X, by, BTN_X + BTN_W, by + 1,
                hovered ? 0xFFAAAAAA : 0xFF888888);

        // Border
        drawBorder(ctx, BTN_X, by, BTN_W, BTN_H, COL_BTN_BORDER);
        // Inner dark bottom edge
        ctx.fill(BTN_X + 1, by + BTN_H - 2, BTN_X + BTN_W - 1, by + BTN_H - 1, COL_BTN_SHADOW);

        // Colour dot (live preview of current colour)
        ColorConfig cfg = ColorConfig.get();
        int dotColor = 0xFF000000
                | ((int)(cfg.red   * 255) << 16)
                | ((int)(cfg.green * 255) << 8)
                |  (int)(cfg.blue  * 255);
        int dotX = BTN_X + 5;
        int dotY = by + (BTN_H - 8) / 2;
        ctx.fill(dotX, dotY, dotX + 8, dotY + 8, COL_DOT_BORDER);
        ctx.fill(dotX + 1, dotY + 1, dotX + 7, dotY + 7, dotColor);

        // Label text
        Text label = Text.translatable("colormod.button.open");
        int textX = BTN_X + 15;
        int textY = by + (BTN_H - client.textRenderer.fontHeight) / 2;
        ctx.drawTextWithShadow(client.textRenderer, label, textX, textY, COL_TEXT);
    }

    private static void drawBorder(DrawContext ctx,
                                   int x, int y, int w, int h, int color) {
        ctx.fill(x,         y,         x + w,     y + 1,     color);
        ctx.fill(x,         y + h - 1, x + w,     y + h,     color);
        ctx.fill(x,         y,         x + 1,     y + h,     color);
        ctx.fill(x + w - 1, y,         x + w,     y + h,     color);
    }
}
