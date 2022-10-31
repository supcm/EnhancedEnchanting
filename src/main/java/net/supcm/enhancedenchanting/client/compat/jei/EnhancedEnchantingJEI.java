package net.supcm.enhancedenchanting.client.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.data.recipes.EnchantingRecipe;
import net.supcm.enhancedenchanting.common.init.RecipeRegister;

import java.util.stream.Collectors;

@JeiPlugin
public class EnhancedEnchantingJEI implements IModPlugin {
    @Override public ResourceLocation getPluginUid() {
        return new ResourceLocation(EnhancedEnchanting.MODID, "jei_plugin");
    }

    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnchantingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(recipeManager.getAllRecipesFor(RecipeRegister.ENCHANTING_RECIPE_TYPE).stream()
                .filter(recipe -> recipe instanceof EnchantingRecipe).collect(Collectors.toList()),
                EnchantingRecipe.ID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockRegister.ENCHANTED_TABLE.get()),
                EnchantingRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(BlockRegister.WORD_MACHINE.get()),
                EnchantingRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(BlockRegister.WORD_FORGE.get()),
                EnchantingRecipeCategory.UID);
    }
}
