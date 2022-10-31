package net.supcm.enhancedenchanting.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;

public class XpBoostEnchantment extends Enchantment {

    public XpBoostEnchantment() { this(EquipmentSlotType.MAINHAND); }
    protected XpBoostEnchantment(EquipmentSlotType... slots) { super(Rarity.RARE, EnchantmentType.WEAPON, slots); }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ToolItem ||
                stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof BowItem ||
                stack.getItem() instanceof TridentItem;
    }

    @Override public int getMaxLevel() { return 5; }
}
