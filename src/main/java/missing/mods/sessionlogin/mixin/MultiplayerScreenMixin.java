package missing.mods.sessionlogin.mixin;

import missing.mods.sessionlogin.screens.LoginScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        int screenWidth = this.client.getWindow().getScaledWidth();
        int buttonWidth = 100;
        int buttonX = screenWidth - buttonWidth - 6;
        int buttonY = 7;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Session Login"), (button) -> {
            if (this.client != null) {
                this.client.setScreen(new LoginScreen(this));
            }
        }).width(100).position(buttonX, buttonY).build());
    }
}
