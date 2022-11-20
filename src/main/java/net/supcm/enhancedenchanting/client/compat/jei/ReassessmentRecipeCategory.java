package net.supcm.enhancedenchanting.client.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.data.recipes.ReassessmentRecipe;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import java.util.ArrayList;
import java.util.List;

public class ReassessmentRecipeCategory implements IRecipeCategory<ReassessmentRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(EnhancedEnchanting.MODID, "reassessment");
    public static final ResourceLocation TEXTURE
            = new ResourceLocation(EnhancedEnchanting.MODID, "textures/gui/reassessment_jei.png");
    private final IDrawable BACKGROUND;
    private final IDrawable ICON;
    public ReassessmentRecipeCategory(IGuiHelper helper) {
        BACKGROUND = helper.createDrawable(TEXTURE, 0, 0, 139, 78);
        ICON = helper.createDrawableIngredient(new ItemStack(BlockRegister.REASSESSMENT_TABLE.get()));
    }
    @Override public ResourceLocation getUid() { return UID; }
    @Override public Class<? extends ReassessmentRecipe> getRecipeClass() { return ReassessmentRecipe.class; }
    @Override public String getTitle() { return new TranslationTextComponent("jei.reassessment").getString(); }
    @Override public IDrawable getBackground() { return BACKGROUND; }
    @Override public IDrawable getIcon() { return ICON; }
    @Override public void setIngredients(ReassessmentRecipe recipe, IIngredients ingredients) {
        List<Ingredient> list = new ArrayList<>(recipe.getIngredients());
        list.add(Ingredient.of(new ItemStack(ItemRegister.CRYSTAL.get())));
        ingredients.setInputIngredients(list);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }
    @Override public void setRecipe(IRecipeLayout recipeLayout, ReassessmentRecipe recipe,
                                     IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 29, 30);
        recipeLayout.getItemStacks().init(1, true, 78, 30);
        recipeLayout.getItemStacks().init(2, true, 29, 2);
        recipeLayout.getItemStacks().init(3, true, 2, 18);
        recipeLayout.getItemStacks().init(4, true, 2, 42);
        recipeLayout.getItemStacks().init(5, true, 56, 18);
        recipeLayout.getItemStacks().init(6, true, 56, 42);
        recipeLayout.getItemStacks().init(7, true, 29, 58);
        recipeLayout.getItemStacks().init(8, false, 119, 30);
        if(recipe.getConceptions()[0] != 0)
            recipeLayout.getItemStacks().set(2, new ItemStack(ItemRegister.CONCEPTION_BEAUTY.get(),
                    recipe.getConceptions()[0]));
        if(recipe.getConceptions()[1] != 0)
            recipeLayout.getItemStacks().set(3, new ItemStack(ItemRegister.CONCEPTION_CREATION.get(),
                    recipe.getConceptions()[1]));
        if(recipe.getConceptions()[2] != 0)
            recipeLayout.getItemStacks().set(4, new ItemStack(ItemRegister.CONCEPTION_ART.get(),
                    recipe.getConceptions()[2]));
        if(recipe.getConceptions()[3] != 0)
            recipeLayout.getItemStacks().set(5, new ItemStack(ItemRegister.CONCEPTION_TRUTH.get(),
                    recipe.getConceptions()[3]));
        if(recipe.getConceptions()[4] != 0)
            recipeLayout.getItemStacks().set(6, new ItemStack(ItemRegister.CONCEPTION_SOUL.get(),
                    recipe.getConceptions()[4]));
        if(recipe.getConceptions()[5] != 0)
            recipeLayout.getItemStacks().set(7, new ItemStack(ItemRegister.CONCEPTION_LIES.get(),
                    recipe.getConceptions()[5]));
        recipeLayout.getItemStacks().set(ingredients);
    }
}
