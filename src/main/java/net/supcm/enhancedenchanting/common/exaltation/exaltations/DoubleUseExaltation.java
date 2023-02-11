package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.supcm.enhancedenchanting.common.exaltation.BoostExaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;

public class DoubleUseExaltation extends BoostExaltation {
    public DoubleUseExaltation() {
        super("double_use", 0.0f, 0.0f, false);
    }
    @Override public void boostExaltation(Exaltation ex) {}
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float damage, CompoundNBT tag) {
        if(tag != null) {
            for(int i = 0; i < 4; i++) {
                if(i == getThisExaltationSlot(tag)) continue;
                if(tag.contains("" + i)) {
                    Exaltation ex = Exaltation.deserialize(tag.getCompound("" + i));
                    if(ex.isAbility())
                        ex.onAttack(attacker, attacked, damage, tag);
                }
            }
        }
    }
    @Override public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {
        if(tag != null) {
            for(int i = 0; i < 4; i++) {
                if(i == getThisExaltationSlot(tag)) continue;
                if(tag.contains("" + i)) {
                    Exaltation ex = Exaltation.deserialize(tag.getCompound("" + i));
                    if(ex.isAbility())
                        ex.onDig(digger, pos, tag);
                }
            }
        }
    }
    int getThisExaltationSlot(CompoundNBT tag) {
        int slot = -1;
        for(int i = 0; i < 3; i++) {
            if(tag.contains("" + i)) {
                Exaltation ex = Exaltation.deserialize(tag.getCompound("" + i));
                if(ex instanceof DoubleUseExaltation) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }
}
