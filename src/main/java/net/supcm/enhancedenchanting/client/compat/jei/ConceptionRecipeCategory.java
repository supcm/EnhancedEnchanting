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
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.data.recipes.ConceptionRecipe;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import java.util.ArrayList;
import java.util.List;

public class ConceptionRecipeCategory implements IRecipeCategory<ConceptionRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnhancedEnchanting.MODID, "conception");
    public static final ResourceLocation TEXTURE
            = new ResourceLocation(EnhancedEnchanting.MODID, "textures/gui/conception_jei.png");
    private final IDrawable BACKGROUND;
    private final IDrawable ICON;
    public ConceptionRecipeCategory(IGuiHelper helper) {
        BACKGROUND = helper.createDrawable(TEXTURE, 0, 0, 128, 70);
        ICON = helper.createDrawableIngredient(new ItemStack(BlockRegister.THOUGHT_LOOM.get()));
    }
    @Override public ResourceLocation getUid() { return UID; }
    @Override public Class<? extends ConceptionRecipe> getRecipeClass() { return ConceptionRecipe.class; }
    @Override public String getTitle() { return new TranslationTextComponent("jei.conception").getString(); }
    @Override public IDrawable getBackground() { return BACKGROUND; }
    @Override public IDrawable getIcon() { return ICON; }
    @Override public void setIngredients(ConceptionRecipe recipe, IIngredients ingredients) {
        List<Ingredient> list = new ArrayList<>(recipe.getIngredients());
        list.add(Ingredient.of(new ItemStack(ItemRegister.CONCEPTION_BASE.get())));
        ingredients.setInputIngredients(list);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }
    @Override public void setRecipe(IRecipeLayout recipeLayout, ConceptionRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 5, 26);
        recipeLayout.getItemStacks().init(1, true, 55, 4);
        recipeLayout.getItemStacks().init(2, true, 55, 26);
        recipeLayout.getItemStacks().init(3, true, 55, 48);
        recipeLayout.getItemStacks().init(4, false, 105, 26);
        recipeLayout.getItemStacks().init(5, true, 82, 34);
        recipeLayout.getItemStacks().set(5, new ItemStack(ItemRegister.CONCEPTION_BASE.get()));
        recipeLayout.getItemStacks().set(ingredients);
    }
    @Override public void draw(ConceptionRecipe recipe, MatrixStack ms, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, ms, mouseX, mouseY);
        ms.pushPose();
        if(recipe.getLevel() != -1)
            Minecraft.getInstance().font.draw(ms, Integer.toString(recipe.getLevel()),
                    88, 20, 0x14d924);
        ms.popPose();
    }
}
