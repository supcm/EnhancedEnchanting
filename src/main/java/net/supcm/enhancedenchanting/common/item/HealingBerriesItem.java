package net.supcm.enhancedenchanting.common.item;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.supcm.enhancedenchanting.EnhancedEnchanting;

public class HealingBerriesItem extends Item {
    public static final Food HEALING_BERRIES_FOOD = new Food.Builder()
            .nutrition(2)
            .saturationMod(0.1F)
            .alwaysEat()
            .fast()
            .effect(new EffectInstance(Effects.HEAL, 1, 1), 1.0f)
            .effect(new EffectInstance(Effects.REGENERATION, 200, 0), 0.8f)
            .build();
    public HealingBerriesItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(16).food(HEALING_BERRIES_FOOD));
    }
    @Override public boolean isFoil(ItemStack stack) { return true; }
}
