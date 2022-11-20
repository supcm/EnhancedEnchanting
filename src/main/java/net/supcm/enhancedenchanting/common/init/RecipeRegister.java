package net.supcm.enhancedenchanting.common.init;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.data.recipes.ReassessmentRecipe;
import net.supcm.enhancedenchanting.common.data.recipes.ConceptionRecipe;
import net.supcm.enhancedenchanting.common.data.recipes.EnchantingRecipe;

public class RecipeRegister {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS =
          DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnhancedEnchanting.MODID);
    public static final RegistryObject<EnchantingRecipe.Serializer> ENCHANTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("enchanting", EnchantingRecipe.Serializer::new);
    public static final RegistryObject<ConceptionRecipe.Serializer> CONCEPTION_SERIALIZER =
            RECIPE_SERIALIZERS.register("conception", ConceptionRecipe.Serializer::new);
    public static final RegistryObject<ReassessmentRecipe.Serializer> REASSESSMENT_SERIALIZER =
            RECIPE_SERIALIZERS.register("reassessment", ReassessmentRecipe.Serializer::new);
    public static final IRecipeType<EnchantingRecipe> ENCHANTING_RECIPE_TYPE = new
            EnchantingRecipe.EnchantingRecipeType();
    public static final IRecipeType<ConceptionRecipe> CONCEPTION_RECIPE_TYPE = new
            ConceptionRecipe.ConceptionRecipeType();
    public static final IRecipeType<ReassessmentRecipe> REASSESSMENT_RECIPE_TYPE = new
            ReassessmentRecipe.AssertionRecipeType();
    public static void reg(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
        Registry.register(Registry.RECIPE_TYPE, EnchantingRecipe.ID, ENCHANTING_RECIPE_TYPE);
        Registry.register(Registry.RECIPE_TYPE, ConceptionRecipe.ID, CONCEPTION_RECIPE_TYPE);
        Registry.register(Registry.RECIPE_TYPE, ReassessmentRecipe.ID, REASSESSMENT_RECIPE_TYPE);
    }
}
