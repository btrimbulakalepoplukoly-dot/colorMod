package com.colormod.mixin;

import com.colormod.client.gui.ColorPickerScreen;
import com.colormod.config.ColorConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * After every screen renders, paint a semi-transparent colour overlay
 * so the GUI tint is visible not just in the HUD but in menus too.
 * Skips our own ColorPickerScreen so you can judge the colour cleanly.
 */
@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void colormod$afterRender(DrawContext context,
                                      int mouseX, int mouseY,
                                      float delta, CallbackInfo ci) {
        Screen self = (Screen)(Object) this;

        // Don't tint our own picker — you want to see the colour selector clearly
        if (self instanceof ColorPickerScreen) return;

        ColorConfig cfg = ColorConfig.get();
        if (cfg.opacity < 0.005f) return; // essentially zero — skip

        context.fill(0, 0, self.width, self.height, cfg.toARGB());
    }
}
