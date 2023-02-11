package net.supcm.enhancedenchanting.common.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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
import java.util.Arrays;

public class ReassessmentRecipe implements IRecipe<IInventory> {
    public static ResourceLocation ID = new ResourceLocation(EnhancedEnchanting.MODID, "reassessment");
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> inputs;
    private final int[] conceptions;

    public ReassessmentRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputs,
                              int[] conceptions) {
        this.id = id;
        this.output = output;
        this.inputs = inputs;
        this.conceptions = conceptions;
    }

    @Override public boolean matches(IInventory inventory, World world) { return true; }
    @Override public ItemStack assemble(IInventory inventory) {  return output; }
    @Override public boolean canCraftInDimensions(int x, int y) { return true; }
    @Override public NonNullList<Ingredient> getIngredients() { return inputs; }
    @Override public boolean isSpecial() { return true; }
    @Override public ItemStack getResultItem() { return output.copy(); }
    @Override public ResourceLocation getId() { return id; }
    public int[] getConceptions() { return conceptions; }
    @Override public IRecipeSerializer<?> getSerializer() {
        return RecipeRegister.REASSESSMENT_SERIALIZER.get();
    }
    @Override public IRecipeType<?> getType() { return Registry.RECIPE_TYPE.getOptional(ID).get(); }
    public static class AssertionRecipeType implements IRecipeType<ReassessmentRecipe> {
        @Override public String toString() { return ID.toString(); }
    }
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements
            IRecipeSerializer<ReassessmentRecipe> {
        @Override public ReassessmentRecipe fromJson(ResourceLocation rs, JsonObject json) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);
            inputs.set(0, Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "input")));
            ItemStack output = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "output"));
            JsonArray array = JSONUtils.getAsJsonArray(json, "conceptions");
            int[] conceptions = new int[6];
            int i = 0;
            for(JsonElement el : array) {
                conceptions[i] = el.getAsInt();
                i++;
            }
            if(Arrays.stream(conceptions).anyMatch(x -> x > 16) || Arrays.stream(conceptions).anyMatch(x -> x < 0))
                throw new JsonParseException("Each Concept count must be in range [0;16]!");
            return new ReassessmentRecipe(rs, output, inputs, conceptions);
        }
        @Nullable @Override public ReassessmentRecipe fromNetwork(ResourceLocation rs, PacketBuffer buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);
            inputs.set(0, Ingredient.fromNetwork(buffer));
            ItemStack output = buffer.readItem();
            int[] conceptions = new int[6];
            for(int i = 0; i < conceptions.length; i++)
                conceptions[i] = buffer.readInt();
            return new ReassessmentRecipe(rs, output, inputs, conceptions);
        }
        @Override
        public void toNetwork(PacketBuffer buffer, ReassessmentRecipe recipe) {
            for(Ingredient ing : recipe.getIngredients())
                ing.toNetwork(buffer);
            buffer.writeItem(recipe.getResultItem());
            for(int i = 0; i < recipe.getConceptions().length; i++)
                buffer.writeInt(recipe.getConceptions()[i]);
        }
    }
}
