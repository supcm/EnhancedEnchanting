package net.supcm.enhancedenchanting.common.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class LavaCrystalOreBlock extends OreBlock {
    public LavaCrystalOreBlock() {
        super(Properties.of(Material.STONE).strength(2.0f, 2.0f)
                .requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).harvestLevel(1)
                        .lightLevel(light -> 9));
    }
    @Override public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if(random.nextInt(4) == 0){
            for (Direction direction : Direction.values()) {
                BlockPos relpos = pos.relative(direction);
                if (!world.getBlockState(relpos).isSolidRender(world, relpos)) {
                    Direction.Axis axis = direction.getAxis();
                    double d1 = axis == Direction.Axis.X ?
                            0.5D + 0.5625D * (double) direction.getStepX() : (double) random.nextFloat();
                    double d2 = axis == Direction.Axis.Y ?
                            0.5D + 0.5625D * (double) direction.getStepY() : (double) random.nextFloat();
                    double d3 = axis == Direction.Axis.Z ?
                            0.5D + 0.5625D * (double) direction.getStepZ() : (double) random.nextFloat();
                    world.addParticle(ParticleTypes.DRIPPING_LAVA,
                            (double) pos.getX() + d1,
                            (double) pos.getY() + d2,
                            (double) pos.getZ() + d3,
                            0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
    @Override public BlockRenderType getRenderShape(BlockState state) { return BlockRenderType.MODEL; }
    @Override protected int xpOnDrop(Random random) { return 1+random.nextInt( 7); }
}
