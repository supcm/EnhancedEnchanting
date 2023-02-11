package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;
import net.supcm.enhancedenchanting.common.init.EnchantmentRegister;

import java.util.List;

public class EnderlichExaltation extends Exaltation {
    public EnderlichExaltation() {
        super("enderlich", 0, 0, true);
    }
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float attack, CompoundNBT tag) {
        if (attacker.distanceTo(attacked) >= 2.15f) {
            BlockPos attackerPos = attacker.blockPosition();
            if (!attacker.isCrouching())
                attacker.teleportToWithTicket(attacked.blockPosition().getX(), attacked.blockPosition().getY(),
                        attacked.blockPosition().getZ());
            else {
                if ((getRange() + getDamage()) >= 0.15f) {
                    List<Entity> entities = attacked.level.getEntities(null,
                            new AxisAlignedBB(attacked.blockPosition().getX() - (getRange() + +getDamage()),
                                    attacked.blockPosition().getY() - (getRange() + +getDamage()),
                                    attacked.blockPosition().getZ() - (getRange() + +getDamage()),
                                    attacked.blockPosition().getX() + (getRange() + +getDamage()),
                                    attacked.blockPosition().getY() + (getRange() + +getDamage()),
                                    attacked.blockPosition().getZ() + (getRange() + +getDamage())));
                    for (Entity entity : entities) {
                        if (entity.isAttackable() && attacker != entity) {
                            entity.teleportToWithTicket(attackerPos.getX(), attackerPos.getY(), attackerPos.getZ());
                            super.onAttack(attacker, attacked, attack, tag);
                        }
                    }
                }
                attacked.teleportToWithTicket(attackerPos.getX(), attackerPos.getY(), attackerPos.getZ());
            }
        }
        super.onAttack(attacker, attacked, attack, tag);
    }
    @Override public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {
        List<ItemEntity> entities = digger.level.getEntitiesOfClass(ItemEntity.class,
                new AxisAlignedBB(
                        pos.getX() - (4 * (getRange() + getDamage())),
                        pos.getY() - (4 * (getRange() + getDamage())),
                        pos.getZ() - (4 * (getRange() + getDamage())),
                        pos.getX() + (4 * (getRange() + getDamage())),
                        pos.getY() + (4 * (getRange() + getDamage())),
                        pos.getZ() + (4 * (getRange() + getDamage()))));
        for (ItemEntity entity : entities) {
            entity.moveTo(pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5);
            if(digger instanceof PlayerEntity &&
                    !((PlayerEntity) digger).isCreative())
                digger.getMainHandItem().setDamageValue(digger.getMainHandItem().getDamageValue() + 1);
        }
        super.onDig(digger, pos, tag);
    }
}
