package net.supcm.enhancedenchanting.client.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.data.recipes.EnchantingRecipe;

public class EnchantingRecipeCategory implements IRecipeCategory<EnchantingRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnhancedEnchanting.MODID, "enchanting");
    public static final ResourceLocation TEXTURE
            = new ResourceLocation(EnhancedEnchanting.MODID, "textures/gui/enchanting_jei.png");
    private final IDrawable BACKGROUND;
    private final IDrawable ICON;

    public EnchantingRecipeCategory(IGuiHelper helper) {
        BACKGROUND = helper.createDrawable(TEXTURE, 0, 0, 100, 40);
        ICON = helper.createDrawableIngredient(new ItemStack(BlockRegister.ENCHANTED_TABLE.get()));
    }
    @Override public ResourceLocation getUid() { return UID; }
    @Override public Class getRecipeClass() { return EnchantingRecipe.class; }
    @Override public String getTitle() { return new TranslationTextComponent("jei.enchanting").getString(); }
    @Override public IDrawable getBackground() { return BACKGROUND; }
    @Override public IDrawable getIcon() { return ICON; }
    @Override public void setIngredients(EnchantingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }
    @Override public void setRecipe(IRecipeLayout recipeLayout, EnchantingRecipe recipe,
                                    IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 3, 11);
        recipeLayout.getItemStacks().init(1, false, 79, 11);
        recipeLayout.getItemStacks().set(ingredients);
    }
    @Override public void draw(EnchantingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        matrixStack.pushPose();
        if(recipe.getLevel() != -1) {
            Minecraft.getInstance().font.draw(matrixStack, Integer.toString(recipe.getLevel()),
                    44, 6, 0x14d924);
        }
        matrixStack.scale(0.9f, 0.9f, 0.9f);
        int x = 20, y = 34;
        if(recipe.getTier() == 0) {
            Minecraft.getInstance().font.draw(matrixStack,
                    new TranslationTextComponent("block.enhancedenchanting.enchanted_table").getString(),
                    x-3, y, 0xb81c11);
        } else if(recipe.getTier() == 1) {
            Minecraft.getInstance().font.draw(matrixStack,
                    new TranslationTextComponent("block.enhancedenchanting.word_machine").getString(),
                    x+5, y, 0xb81c11);
        } else {
            Minecraft.getInstance().font.draw(matrixStack,
                    new TranslationTextComponent("block.enhancedenchanting.word_forge").getString(),
                    x+8, y, 0xb81c11);
        }
        matrixStack.popPose();
    }
}
