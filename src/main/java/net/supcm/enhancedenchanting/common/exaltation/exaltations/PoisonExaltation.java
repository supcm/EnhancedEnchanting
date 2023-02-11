package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.supcm.enhancedenchanting.common.exaltation.BoostExaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltations;

import java.util.List;

public class PoisonExaltation extends Exaltation {
    public PoisonExaltation() {
        super("poison", 1.0f, 0, true);
    }
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float attack, CompoundNBT tag) {
        if(attacked instanceof LivingEntity) {
            ((LivingEntity) attacked).addEffect(new EffectInstance(Effects.POISON, (int) ((getDamage() * 2) * 20),
                    (int) (getDamage() - 1)));
            if(getRange() >= 0.15f) {
                List<Entity> entities = attacked.level.getEntities(null,
                        new AxisAlignedBB(attacked.blockPosition().getX() - getRange(),
                                attacked.blockPosition().getY() - getRange(),
                                attacked.blockPosition().getZ() - getRange(),
                                attacked.blockPosition().getX() + getRange(),
                                attacked.blockPosition().getY() + getRange(),
                                attacked.blockPosition().getZ() + getRange()));
                for(Entity entity : entities) {
                    if(entity.isAttackable() && entity instanceof LivingEntity &&
                        attacker != entity && attacked != entity) {
                        ((LivingEntity) entity).addEffect(new EffectInstance(Effects.POISON, (int) ((getDamage() + 1) * 20),
                                (int) (getDamage() - 1)));
                        super.onAttack(attacker, attacked, attack, tag);
                    }
                }
            }
            super.onAttack(attacker, attacked, attack, tag);
        }
    }
}
