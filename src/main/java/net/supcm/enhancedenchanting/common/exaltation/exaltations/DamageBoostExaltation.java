package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.supcm.enhancedenchanting.common.exaltation.BoostExaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;

public class DamageBoostExaltation extends BoostExaltation {
    public DamageBoostExaltation() {
        super("damage_boost", 0.5f, 0);
    }
    @Override public void boostExaltation(Exaltation ex) {
        if(ex.isAbility()) {
            ex.setRange(ex.getRange() + getRange());
            ex.setDamage(ex.getDamage() + getRange());
        }
    }
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float damage, CompoundNBT tag) {}
    @Override public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {}
    @Override public void onUse(PlayerEntity player, ItemUseContext ctx, CompoundNBT tag) {
        if(!ctx.getLevel().isClientSide && !player.hasEffect(Effects.DAMAGE_BOOST)) {
            player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, (int)(getDamage() + getRange()) * 20,
                    (int) getDamage()));
        }
    }
}
