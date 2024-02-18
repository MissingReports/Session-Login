package missing.mods.sessionlogin.screens;

import missing.mods.sessionlogin.util.AuthUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class LoginScreen extends Screen {
    private final Screen parent;
    TextFieldWidget textField;


    public LoginScreen(Screen parent) {
        super(Text.literal("Login"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int textFieldWidth = 195;
        int buttonWidth = 97;
        int buttonHeight = 20;
        int textFieldX = this.width / 2 - textFieldWidth / 2;
        int textFieldY = this.height / 2 - 30;
        textField = new TextFieldWidget(
                this.textRenderer,
                textFieldX,
                textFieldY,
                textFieldWidth,
                buttonHeight,
                Text.literal("")
        );
        textField.setMaxLength(800);
        this.setInitialFocus(textField);
        int cancelButtonY = textFieldY + 31;
        int loginButtonX = textFieldX + textFieldWidth - buttonWidth + 2;
        int loginButtonY = textFieldY + 31;
        ButtonWidget cancelButton = ButtonWidget.builder(Text.of(Formatting.RED + "" + Formatting.BOLD + "Cancel"), button -> this.close())
                .dimensions(textFieldX - 2, cancelButtonY, buttonWidth, buttonHeight)
                .build();
        ButtonWidget loginButton = ButtonWidget.builder(Text.of(Formatting.GREEN + "" + Formatting.BOLD + "Login"), button -> {
            String token = textField.getText();
            if (token.isEmpty()) {
                SystemToast.add(
                        client.getToastManager(),
                        SystemToast.Type.PERIODIC_NOTIFICATION,
                        Text.literal(Formatting.RED + "Invalid access token"),
                        Text.literal(Formatting.GRAY + "This access token is invalid")
                );
                this.close();
                return;
            }
            String[] info = AuthUtils.authenticate(token);
            if (info != null && client != null) {
                SystemToast.add(
                        client.getToastManager(),
                        SystemToast.Type.PERIODIC_NOTIFICATION,
                        Text.literal("Logged in as " + info[0] + "!"),
                        null
                );
            } else if (info == null && client != null) {
                SystemToast.add(
                        client.getToastManager(),
                        SystemToast.Type.PERIODIC_NOTIFICATION,
                        Text.literal( Formatting.RED + "Invalid access token"),
                        Text.literal(Formatting.GRAY + "This access token is invalid")
                );
            }
            this.close();
        }).dimensions(loginButtonX, loginButtonY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(textField);
        this.addDrawableChild(cancelButton);
        this.addDrawableChild(loginButton);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Enter Access Token"), this.width / 2 - 197 / 2, this.height / 2 - 45, 10526880);
        if (this.client != null && this.client.getSession() != null) {
            String username = this.client.getSession().getUsername();
            int usernameWidth = this.textRenderer.getWidth(Text.of("Logged in as: " + username));
            int usernameX = this.width - usernameWidth - 7;
            int usernameY = 7;
            context.drawTextWithShadow(this.textRenderer, Text.literal("Logged in as: " + username), usernameX, usernameY, 0xFFFF00);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        super.tick();
        this.textField.tick();
    }
}
