package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;

import javax.annotation.Nullable;
import java.util.List;

public class EnchantedFeatherItem extends Item {
    public EnchantedFeatherItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1)
                .rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list,
                                          ITooltipFlag flag) {
        list.add(new TranslationTextComponent("item.enchanted_feather.info"));
    }
    @Override public void inventoryTick(ItemStack stack, World world, Entity entity, int ticks, boolean flag) {
        if(!world.isClientSide && entity.fallDistance >= 3.25f) {
            if(entity instanceof LivingEntity) {
                LivingEntity livingEntity = ((LivingEntity) entity);
                livingEntity.addEffect(new EffectInstance(Effects.SLOW_FALLING, 20, 1));
            }
        }
    }
    @Override public boolean isFoil(ItemStack stack) { return true; }
}
