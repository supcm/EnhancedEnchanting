package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;

import java.util.List;

public class LightningExaltation extends Exaltation {
    public LightningExaltation() {
        super("lightning", 2.0f, 0, true);
    }

    @Override public void onAttack(LivingEntity attacker, Entity attacked, float attack, CompoundNBT tag) {
        spawnLightning(attacked);
        if (getRange() >= 0.15f) {
            List<Entity> entities = attacked.level.getEntities(null,
                    new AxisAlignedBB(attacked.blockPosition().getX() - getRange(),
                            attacked.blockPosition().getY() - getRange(),
                            attacked.blockPosition().getZ() - getRange(),
                            attacked.blockPosition().getX() + getRange(),
                            attacked.blockPosition().getY() + getRange(),
                            attacked.blockPosition().getZ() + getRange()));
            for (Entity entity : entities) {
                if (entity.isAttackable() && entity instanceof LivingEntity &&
                        attacker != entity && attacked != entity) {
                    spawnLightning(entity);
                    super.onAttack(attacker, attacked, attack, tag);
                }
            }

        }
        super.onAttack(attacker, attacked, attack, tag);
    }

    @Override public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {
        digger.addEffect(new EffectInstance(Effects.NIGHT_VISION, (int)(20 * getDamage() * (getRange() + 1)), 0));
        super.onDig(digger, pos, tag);
    }

    void spawnLightning(Entity entity) {
        LightningBoltEntity lightningBolt = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, entity.level);
        lightningBolt.setDamage(getDamage());
        lightningBolt.setVisualOnly(true);
        lightningBolt.setPos(entity.blockPosition().getX(), entity.blockPosition().getY(),
                entity.blockPosition().getZ());
        entity.hurt(DamageSource.LIGHTNING_BOLT, getDamage());
        entity.thunderHit((ServerWorld)entity.level, lightningBolt);
        entity.level.addFreshEntity(lightningBolt);
    }
}
