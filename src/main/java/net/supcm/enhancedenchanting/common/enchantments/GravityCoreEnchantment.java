package net.supcm.enhancedenchanting.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class GravityCoreEnchantment extends Enchantment {
    public GravityCoreEnchantment() { this(EquipmentSlotType.MAINHAND); }
    protected GravityCoreEnchantment(EquipmentSlotType... p_i46731_3_) {
        super(Rarity.RARE, EnchantmentType.DIGGER, p_i46731_3_);
    }
    @Override public boolean canEnchant(ItemStack stack) { return true; }
    @Override public int getMaxLevel() { return 4; }
}
