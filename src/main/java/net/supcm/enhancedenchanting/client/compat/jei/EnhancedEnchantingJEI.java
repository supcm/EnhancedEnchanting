package net.supcm.enhancedenchanting.client.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.data.recipes.ConceptionRecipe;
import net.supcm.enhancedenchanting.common.data.recipes.ReassessmentRecipe;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.data.recipes.EnchantingRecipe;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import net.supcm.enhancedenchanting.common.init.RecipeRegister;

import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
public class EnhancedEnchantingJEI implements IModPlugin {
    @Override public ResourceLocation getPluginUid() {
        return new ResourceLocation(EnhancedEnchanting.MODID, "jei_plugin");
    }

    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnchantingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ConceptionRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ReassessmentRecipeCategory(registration.getJeiHelpers().getGuiHelper()));

    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(recipeManager.getAllRecipesFor(RecipeRegister.ENCHANTING_RECIPE_TYPE).stream()
                .filter(Objects::nonNull).collect(Collectors.toList()),
                EnchantingRecipe.ID);
        registration.addRecipes(recipeManager.getAllRecipesFor(RecipeRegister.CONCEPTION_RECIPE_TYPE).stream()
                .filter((Objects::nonNull)).collect(Collectors.toList()),
                ConceptionRecipe.ID);
        registration.addRecipes(recipeManager.getAllRecipesFor(RecipeRegister.REASSESSMENT_RECIPE_TYPE).stream()
                        .filter((Objects::nonNull)).collect(Collectors.toList()),
                ReassessmentRecipe.ID);
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockRegister.ENCHANTED_TABLE.get()),
                EnchantingRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(BlockRegister.WORD_MACHINE.get()),
                EnchantingRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(BlockRegister.WORD_FORGE.get()),
                EnchantingRecipeCategory.UID);

        registration.addRecipeCatalyst(new ItemStack(BlockRegister.THOUGHT_LOOM.get()),
                ConceptionRecipeCategory.UID);

        registration.addRecipeCatalyst(new ItemStack(BlockRegister.REASSESSMENT_TABLE.get()),
                ReassessmentRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(BlockRegister.REASSESSMENT_PILLAR.get()),
                ReassessmentRecipeCategory.UID);
    }
}
