package net.supcm.enhancedenchanting.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.EnhancedEnchantingConfig;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class CodexScreen extends Screen {
    //TODO: add archive gui texture
    private final ResourceLocation TEXTURE = new ResourceLocation(EnhancedEnchanting.MODID,
            "textures/gui/codex_gui.png");
    int currentPage = 0;
    private ChangePageButton forwardButton;
    private ChangePageButton backButton;
    private final Map<String, Enchantment> toDisplay = new HashMap<>();
    List<Page> pages = new ArrayList<>();
    public CodexScreen(List<INBT> enchantments) {
        super(NarratorChatListener.NO_TITLE);
        for(INBT tag : enchantments) {
            String glyphs = tag.getAsString().split("'")[0];
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(
                    tag.getAsString().split("'")[1]));
            toDisplay.put(glyphs, ench);
        }
        List<Map.Entry<String, Enchantment>> list = new ArrayList<>(toDisplay.entrySet());
        int i = 0;
        for(int l = 0; l <= getPages(); l++) {
            Page page = new Page();
            for(int j = 0; j < 4; j++) {
                if(i < list.size()) {
                    page.add(list.get(i));
                    i++;
                }
            }
            pages.add(page);
        }

    }
    int getPages() { return (int)Math.ceil(toDisplay.size()/4.0); }
    @Override protected void init() {
        createButtons();
    }
    void createButtons() {
        buttons.clear();
        forwardButton = addButton(new ChangePageButton((width - 192) / 2 + 174, 159, true,
                (action) -> pageForward(), true));
        backButton = addButton(new ChangePageButton((width - 192) / 2 - 14, 159, false,
                (action) -> pageBack(), true));
        addButton(new Button((width - 192) / 2 + 2, 196, 200, 20,
                DialogTexts.GUI_DONE, (action) -> minecraft.setScreen((Screen)null)));
        updateButtonVisibility();
    }
    private void updateButtonVisibility() {
        forwardButton.visible = currentPage < getPages() - 1;
        backButton.visible = currentPage > 0;
    }
    @Override public boolean keyPressed(int key, int x, int y) {
        if (super.keyPressed(key, x, y))
            return true;
        else
            switch(key) {
                case 266:
                    this.backButton.onPress();
                    return true;
                case 267:
                    this.forwardButton.onPress();
                    return true;
                default:
                    return false;
            }
    }
    protected void pageBack() {
        if (currentPage > 0)
            --currentPage;
        updateButtonVisibility();
    }
    protected void pageForward() {
        if (currentPage < getPages() - 1)
            ++currentPage;
        updateButtonVisibility();
    }
    @Override public void render(MatrixStack ms, int x, int y, float tick) {
        renderBackground(ms);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(TEXTURE);
        blit(ms, (width - 192) / 2 - 36, 2, 0, 0, 256, 192);
        int i = 0;
        for(Map.Entry<String, Enchantment> entry : pages.get(currentPage).getEntries()){
            if(EnhancedEnchantingConfig.ENABLE_DESCRIPTIONS_IN_CODEX.get() ||
                    ModList.get().isLoaded("enchdesc")){
                String desc = new TranslationTextComponent("enchantment." +
                        entry.getValue().getRegistryName().toString().replace(':', '.') + ".desc").getString();
                IFormattableTextComponent name = new TranslationTextComponent("enchantment." +
                        entry.getValue().getRegistryName().toString().replace(':', '.'))
                        .withStyle(Style.EMPTY.withColor(Color.fromRgb(entry.getValue().isCurse() ? TextFormatting.RED.getColor() : 0x561185)));
                IFormattableTextComponent text = new StringTextComponent(desc);
                if (isMouseAt(new Rectangle2d((width - 192) / 2 - 20, 30 + i * 28,
                                font.width(desc) / 2, font.lineHeight + 3), x, y))
                    renderComponentTooltip(ms, Lists.newArrayList(name, text), x, y);
            }
            if (!entry.getKey().contains("_")) {
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(EnhancedEnchanting.MODID, entry.getKey())));
                minecraft.getItemRenderer().renderAndDecorateFakeItem(stack,
                        (width - 192) / 2 + 110, 28 + i*28);
            } else {
                String[] glyphs = entry.getKey().split("_");
                ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(EnhancedEnchanting.MODID, glyphs[0])));
                ItemStack stack1 = new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(EnhancedEnchanting.MODID, glyphs[1])));
                minecraft.getItemRenderer().renderAndDecorateFakeItem(stack,
                        (width - 192) / 2 + 110, 28 + i*28);
                minecraft.getItemRenderer().renderAndDecorateFakeItem(stack1,
                        (width - 192) / 2 + 134, 28 + i*28);
                if (glyphs.length == 3) {
                    ItemStack stack2 = new ItemStack(ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation(EnhancedEnchanting.MODID, glyphs[2])));
                    minecraft.getItemRenderer().renderAndDecorateFakeItem(stack2,
                            (width - 192) / 2 + 158, 28 + i*28);
                }
            }
            minecraft.font.draw(ms, new TranslationTextComponent("enchantment."
                            + entry.getValue().getRegistryName().toString().replace(':', '.')),
                    (width - 192) / 2 - 20, 32 + i*28, entry.getValue().isCurse() ? TextFormatting.RED.getColor() : 0x561185);
            i++;
            if(i == 4) i = 0;
        }
        minecraft.font.draw(ms, new StringTextComponent((currentPage + 1) + "/" + getPages()),
                (width - 192) / 2 + 62, 159, 0);
        super.render(ms, x, y, tick);
    }
    public boolean isMouseAt(Rectangle2d rect, double mouseX, double mouseY) {
        if (minecraft != null && minecraft.screen == this)
            return rect.contains((int) mouseX, (int) mouseY);
        return false;
    }
    private static class Page {
        public List<Map.Entry<String, Enchantment>> entries = new ArrayList<>();
        public List<Map.Entry<String, Enchantment>> getEntries() { return entries; }
        Page add(Map.Entry<String, Enchantment> entry) {
            entries.add(entry);
            return this;
        }
    }
}
