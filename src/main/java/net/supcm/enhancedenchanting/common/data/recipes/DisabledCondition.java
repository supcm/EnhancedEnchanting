package net.supcm.enhancedenchanting.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.EnhancedEnchantingConfig;

public class DisabledCondition implements ICondition {
    public static final ResourceLocation ID = new ResourceLocation(EnhancedEnchanting.MODID, "disabled");
    public static final DisabledCondition INSTANCE = new DisabledCondition();
    private DisabledCondition() {}
    @Override public ResourceLocation getID() { return ID; }
    @Override public boolean test() { return !EnhancedEnchantingConfig.DISABLE_VANILLA.get(); }
    public static class Serializer implements IConditionSerializer<DisabledCondition> {
        public static final Serializer INSTANCE = new Serializer();
        @Override public void write(JsonObject json, DisabledCondition value) {}
        @Override public DisabledCondition read(JsonObject json) {
            return DisabledCondition.INSTANCE;
        }
        @Override public ResourceLocation getID() { return DisabledCondition.ID; }
    }
}
