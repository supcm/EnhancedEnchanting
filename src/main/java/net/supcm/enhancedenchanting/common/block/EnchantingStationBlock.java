package net.supcm.enhancedenchanting.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.common.block.entity.EnchantingStationTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class EnchantingStationBlock extends ContainerBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 1, 16),
            Block.box(-2, 9, -2, 18, 11, 18),
            Block.box(0, 1, 7, 16, 9, 9),
            Block.box(7, 1, 0, 9, 9, 16),
            Block.box(6, 11, 6, 10, 15, 10)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public EnchantingStationBlock() {
        super(Properties.of(Material.HEAVY_METAL).strength(5.0f, 5.0f));
    }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader world) { return new EnchantingStationTile(); }
    @Override public BlockRenderType getRenderShape(BlockState p_149645_1_) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
                                         ISelectionContext p_220053_4_) { return SHAPE; }
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof EnchantingStationTile) {
            EnchantingStationTile te = (EnchantingStationTile)world.getBlockEntity(pos);
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide) {
            if(hand == Hand.MAIN_HAND) {
                if(world.getBlockEntity(pos) instanceof EnchantingStationTile) {
                    EnchantingStationTile tile = (EnchantingStationTile)world.getBlockEntity(pos);
                    double hitLoc = hit.getLocation().y;
                    boolean up = hitLoc-(int)hitLoc >= 0.45d || (int)hitLoc > pos.getY();
                    if(!tile.doCraft){
                        if (up) tile.insertOrExtractItem(player, 0);
                        else tile.insertOrExtractItem(player, 1);
                        world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        } else {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 1.25D;
            double z = pos.getZ() + 0.5D;
            world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 0.025D,
                    0.0D);

        }
        return ActionResultType.PASS;
    }

    @Override public void animateTick(BlockState state, World world,
                            BlockPos pos, Random rand) {
        if(rand.nextInt(9) < 3) return;
        EnchantingStationTile te;
        if(world.getBlockEntity(pos) instanceof EnchantingStationTile)
            te = (EnchantingStationTile) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if(te.doCraft) {
            for(int i = 0; i < (rand.nextInt(4) + 1); i++) {
                world.addParticle(ParticleTypes.SOUL,
                        x + randomA, y + 0.65 + randomA - randomB, z + randomB,
                        0.0D, -0.025D, 0.0D);
                if(te.handler.getStackInSlot(1).getItem() == ItemRegister.FIR.get())
                    world.addParticle(ParticleTypes.CRIT,
                            x + randomA, y + 0.65 + randomA - randomB, z + randomB,
                            0.0D, -0.025D, 0.0D);
            }
        }
    }
}
