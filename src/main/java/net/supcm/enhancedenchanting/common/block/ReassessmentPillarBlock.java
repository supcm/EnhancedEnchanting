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
import net.minecraftforge.common.ToolType;
import net.supcm.enhancedenchanting.common.block.entity.ReassessmentPillarTile;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ReassessmentPillarBlock extends ContainerBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(2, 2, 2, 14, 14, 14),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 14, 0, 16, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public ReassessmentPillarBlock() {
        super(Properties.of(Material.WOOD).strength(2.0f, 2.0f).harvestTool(ToolType.AXE));
    }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader world) {
        return new ReassessmentPillarTile();
    }
    @Override public BlockRenderType getRenderShape(BlockState state) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }
    @Override public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof ReassessmentPillarTile) {
            ReassessmentPillarTile te = (ReassessmentPillarTile)world.getBlockEntity(pos);
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos,
                                          PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide) {
            if(hand == Hand.MAIN_HAND) {
                if(world.getBlockEntity(pos) instanceof ReassessmentPillarTile) {
                    ReassessmentPillarTile tile = ((ReassessmentPillarTile) world.getBlockEntity(pos));
                    tile.insertOrExtractItem(player, 0);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
            }
        } else {
            if(world.getBlockEntity(pos) instanceof ReassessmentPillarTile) {
                ReassessmentPillarTile tile = ((ReassessmentPillarTile) world.getBlockEntity(pos));
                if(!tile.handler.getStackInSlot(0).isEmpty()){
                    double x = pos.getX() + 0.5D;
                    double y = pos.getY() + 1.25D;
                    double z = pos.getZ() + 0.5D;
                    world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 0.025D,
                            0.0D);
                }
            }
        }
        return ActionResultType.PASS;
    }
}
