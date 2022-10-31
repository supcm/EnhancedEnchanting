package net.supcm.enhancedenchanting.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.EnhancedEnchantingConfig;

public class UsingConfigCondition implements ICondition {
    public static final ResourceLocation ID = new ResourceLocation(EnhancedEnchanting.MODID, "using_config");
    public static final UsingConfigCondition INSTANCE = new UsingConfigCondition();
    private UsingConfigCondition() {}
    @Override public ResourceLocation getID() { return ID; }
    @Override public boolean test() { return !EnhancedEnchantingConfig.DISABLE_VANILLA.get(); }
    public static class Serializer implements IConditionSerializer<UsingConfigCondition> {
        public static final Serializer INSTANCE = new Serializer();
        @Override public void write(JsonObject json, UsingConfigCondition value) {}
        @Override public UsingConfigCondition read(JsonObject json) {
            return UsingConfigCondition.INSTANCE;
        }
        @Override public ResourceLocation getID() { return UsingConfigCondition.ID; }
    }
}
