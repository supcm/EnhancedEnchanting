package net.supcm.enhancedenchanting.common.data.recipes;

import com.google.gson.JsonElement;
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
import javax.annotation.Nullable;

public class ConceptionRecipe implements IRecipe<IInventory> {
    public static ResourceLocation ID = new ResourceLocation(EnhancedEnchanting.MODID, "conception");
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> inputs;
    private final int level;
    public ConceptionRecipe(ResourceLocation id, ItemStack output,
                            NonNullList<Ingredient> inputs, int level) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.level = level;
    }
    @Override public boolean matches(IInventory inventory, World world) { return true; }
    @Override public ItemStack assemble(IInventory inventory) {  return output; }
    @Override public boolean canCraftInDimensions(int x, int y) { return true; }
    @Override public NonNullList<Ingredient> getIngredients() { return inputs; }
    @Override public boolean isSpecial() { return true; }
    @Override public ItemStack getResultItem() { return output.copy(); }
    public int getLevel() { return level; }
    @Override public ResourceLocation getId() { return id; }
    @Override public IRecipeSerializer<?> getSerializer() {
        return RecipeRegister.CONCEPTION_SERIALIZER.get();
    }
    @Override public IRecipeType<?> getType() { return Registry.RECIPE_TYPE.getOptional(ID).get(); }
    public static class ConceptionRecipeType implements IRecipeType<ConceptionRecipe> {
        @Override public String toString() { return ID.toString(); }
    }
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements
            IRecipeSerializer<ConceptionRecipe> {

        @Override public ConceptionRecipe fromJson(ResourceLocation rs, JsonObject json) {
            int level = JSONUtils.getAsInt(json, "level");
            NonNullList<Ingredient> inputs = NonNullList.withSize(4, Ingredient.EMPTY);
            int i = 0;
            for(JsonElement el : JSONUtils.getAsJsonArray(json, "inputs")){
                inputs.set(i, Ingredient.fromJson(el));
                i++;
            }
            ItemStack output = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "output"));
            return new ConceptionRecipe(rs, output, inputs, level);
        }

        @Nullable @Override public ConceptionRecipe fromNetwork(ResourceLocation rs, PacketBuffer buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(4, Ingredient.EMPTY);
            for(int i = 0; i < inputs.size(); i++)
                inputs.set(i, Ingredient.fromNetwork(buffer));
            ItemStack output = buffer.readItem();
            int level = buffer.readInt();
            return new ConceptionRecipe(rs, output, inputs, level);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, ConceptionRecipe recipe) {
            for(Ingredient ingredient : recipe.getIngredients())
                ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());
            buffer.writeInt(recipe.getLevel());
        }
    }
}
