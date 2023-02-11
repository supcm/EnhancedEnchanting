package net.supcm.enhancedenchanting.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.init.EnchantmentRegister;

public class GravityBombItem extends Item {
    public GravityBombItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.RARE).setNoRepair().tab(EnhancedEnchanting.EETAB));
    }
    @Override public void inventoryTick(ItemStack stack, World world, Entity entity,
                                        int tick, boolean flag) {
        if(!world.isClientSide() && !stack.isEnchanted())
            stack.enchant(EnchantmentRegister.GRAVITY_CORE.get(), 20);
    }
}
