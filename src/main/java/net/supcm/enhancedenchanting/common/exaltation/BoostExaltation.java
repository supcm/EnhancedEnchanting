package net.supcm.enhancedenchanting.common.exaltation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public abstract class BoostExaltation extends Exaltation {
    public BoostExaltation(String name, float baseDamage, float baseRange) {
        this(name, baseDamage, baseRange, true);
    }
    public BoostExaltation(String name, float baseDamage, float baseRange, boolean haveRand) {
        super(name, baseDamage, baseRange, false, haveRand);
    }
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float damage, CompoundNBT tag) {}
    @Override public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {}
    public abstract void boostExaltation(Exaltation ex);
}
