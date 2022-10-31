package net.supcm.enhancedenchanting.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.init.RecipeRegister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnchantingRecipe implements IRecipe<IInventory> {
    public static ResourceLocation ID = new ResourceLocation(EnhancedEnchanting.MODID, "enchanting");
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> inputs;
    private final int level;
    private final short tier;
    public EnchantingRecipe(ResourceLocation id, ItemStack output,
                            NonNullList<Ingredient> inputs, int level, short tier) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.level = level;
        this.tier = tier;   
    }
    @Override public boolean matches(IInventory inventory, World world) { return true; }
    @Override public ItemStack assemble(IInventory inventory) {  return output; }
    @Override public boolean canCraftInDimensions(int x, int y) { return true; }
    @Override public NonNullList<Ingredient> getIngredients() { return inputs; }
    @Override public boolean isSpecial() { return true; }
    @Override public ItemStack getResultItem() { return output.copy(); }
    public int getLevel() { return level; }
    public short getTier() { return tier; }
    @Override public ResourceLocation getId() { return id; }
    @Override public IRecipeSerializer<?> getSerializer() {
        return RecipeRegister.ENCHANTING_SERIALIZER.get();
    }
    @Override public IRecipeType<?> getType() { return Registry.RECIPE_TYPE.getOptional(ID).get(); }
    public static class EnchantingRecipeType implements IRecipeType<EnchantingRecipe> {
        @Override public String toString() { return ID.toString(); }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements
            IRecipeSerializer<EnchantingRecipe> {
        @Override public EnchantingRecipe fromJson(@Nonnull ResourceLocation recipe, @Nonnull JsonObject json) {
            int level = JSONUtils.getAsInt(json, "level");
            short tier = (short)JSONUtils.getAsInt(json, "tier");
            Ingredient input = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "ingredient"));
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, input);
            ItemStack output = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            return new EnchantingRecipe(recipe, output, inputs, level, tier);
        }

        @Nullable @Override public EnchantingRecipe fromNetwork(@Nonnull ResourceLocation recipe,
                                                                @Nonnull PacketBuffer buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            int level = buffer.readInt();
            short tier = buffer.readShort();
            return new EnchantingRecipe(recipe, output, NonNullList.withSize(1, input), level, tier);
        }

        @Override public void toNetwork(@Nonnull PacketBuffer buffer, @Nonnull EnchantingRecipe recipe) {
            recipe.getIngredients().get(0).toNetwork(buffer);
            buffer.writeItemStack(recipe.getResultItem(), false);
            buffer.writeInt(recipe.getLevel());
            buffer.writeShort(recipe.getTier());
        }
    }
}
