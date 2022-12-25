package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import javax.annotation.Nullable;
import java.util.List;

public class ShadowCrystalItem extends Item {
    public ShadowCrystalItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1).rarity(Rarity.RARE));
    }
    @Override public boolean isFoil(ItemStack stack) {
        if(stack.getTag() != null && stack.getTag().contains("InShadow"))
            return stack.getTag().getBoolean("InShadow");
        return false;
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                          List<ITextComponent> text, ITooltipFlag flag) {
        text.add(new TranslationTextComponent("item.shadow_crystal.info"));
        if(stack.getTag() != null && stack.getTag().getBoolean("InShadow"))
            text.add(new TranslationTextComponent("item.shadow_crystal.active"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public void inventoryTick(ItemStack stack, World world, Entity entity,
                                        int tick, boolean flag) {
        if (!world.isClientSide) {
            if (stack.getTag() == null) {
                CompoundNBT tag = new CompoundNBT();
                tag.putBoolean("InShadow", false);
                stack.setTag(tag);
            }
            boolean isInShadow = world.isDay() ?
                    world.getLightEngine().getRawBrightness(entity.blockPosition(), 0) < 7 :
                    world.getLightEngine().getRawBrightness(entity.blockPosition(), 15) < 7;
            stack.getTag().putBoolean("InShadow",
                    isInShadow);
            if (isInShadow) {
                if (entity instanceof LivingEntity) {
                    LivingEntity living = ((LivingEntity) entity);
                    if (!living.hasEffect(Effects.INVISIBILITY))
                        living.addEffect(new EffectInstance(Effects.INVISIBILITY, 100, 0));
                    else if(living.getEffect(Effects.INVISIBILITY).getDuration() < 20)
                        living.addEffect(new EffectInstance(Effects.INVISIBILITY, 100, 0));
                }
            }
        }
    }
}
