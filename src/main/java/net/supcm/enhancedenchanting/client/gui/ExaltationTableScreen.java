package net.supcm.enhancedenchanting.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.inventory.container.ExaltationTableContainer;

public class ExaltationTableScreen extends ContainerScreen<ExaltationTableContainer> {
    private final ResourceLocation TEXTURE = new ResourceLocation(EnhancedEnchanting.MODID,
            "textures/gui/exaltation_table.png");
    public ExaltationTableScreen(ExaltationTableContainer container, PlayerInventory player,
                                 ITextComponent title) {
        super(container, player, title);
    }
    @Override
    public void render(MatrixStack ms, int x, int y, float ticks) {
        renderBackground(ms);
        super.render(ms, x, y, ticks);
        renderTooltip(ms, x, y);
    }
    @Override protected void renderBg(MatrixStack ms, float ticks, int mouseX, int mouseY) {
        ms.pushPose();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getInstance().getTextureManager().bind(TEXTURE);
        blit(ms, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());
        if(menu.getSlot(37).hasItem()) {
            blit(ms, getGuiLeft() + 74, getGuiTop() + 30,
                    menu.getSlot(37).getItem().getTag().getBoolean("IsAbility") ? 176 : 182,
                    0, 6, 6);
        }
        if(menu.getSlot(39).hasItem()) {
            blit(ms, getGuiLeft() + 96, getGuiTop() + 30,
                    menu.getSlot(39).getItem().getTag().getBoolean("IsAbility") ? 176 : 182,
                    6, 6, 6);
        }
        if(menu.getSlot(38).hasItem()) {
            blit(ms, getGuiLeft() + 74, getGuiTop() + 52,
                    menu.getSlot(38).getItem().getTag().getBoolean("IsAbility") ? 176 : 182,
                    12, 6, 6);
        }
        if(menu.getSlot(40).hasItem()) {
            blit(ms, getGuiLeft() + 96, getGuiTop() + 52,
                    menu.getSlot(40).getItem().getTag().getBoolean("IsAbility") ? 176 : 182,
                    18, 6, 6);
        }
        ms.popPose();
    }
}
