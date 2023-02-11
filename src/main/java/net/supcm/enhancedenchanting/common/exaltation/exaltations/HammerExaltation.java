package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.common.exaltation.BoostExaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;
public class HammerExaltation extends BoostExaltation {
    public HammerExaltation() {
        super("hammer", 0, 1.45f);
    }
    @Override public void boostExaltation(Exaltation ex) {
        if(ex.isAbility()) {
            ex.setRange(ex.getRange() + getRange());
            ex.setDamage(ex.getDamage() + getRange());
        }
    }
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float damage, CompoundNBT tag) {}
    @Override public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {
        if(!digger.isCrouching())
            for(int x = (int) -getRange() + 1; x < getRange(); x++)
                for(int y = (int) -getRange() + 1; y < getRange(); y++)
                    for(int z = (int) -getRange() + 1; z < getRange(); z++) {
                        if(digger.level.getBlockState(pos.north(z).west(x).above(y)).getBlock() == Blocks.AIR ||
                            z == 0 && x == 0 && y == 0) continue;
                        if(digger instanceof PlayerEntity) {
                            BlockPos dig = pos.north(z).west(x).above(y);
                            if (digger.level.getBlockState(pos).getBlock() ==
                                    digger.level.getBlockState(dig).getBlock()) {
                                Block.dropResources(digger.level.getBlockState(dig), digger.level,
                                        dig, digger.level.getBlockEntity(dig),
                                        digger, digger.getMainHandItem());
                                digger.level.destroyBlock(dig, false);
                                if(!((PlayerEntity) digger).isCreative())
                                    digger.getMainHandItem()
                                            .setDamageValue(digger.getMainHandItem().getDamageValue() + 1);
                            }
                        }
                }
    }
}
