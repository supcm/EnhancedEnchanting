package net.supcm.enhancedenchanting.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.supcm.enhancedenchanting.common.block.entity.ReassessmentTableTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class ReassessmentTableBlock extends ContainerBlock {
    VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(2, 2, 2, 14, 6, 14),
            Block.box(0, 6, 0, 16, 14, 16),
            Block.box(0, 14, 0, 1, 18, 1),
            Block.box(15, 14, 0, 16, 18, 1),
            Block.box(15, 14, 15, 16, 18, 16),
            Block.box(0, 14, 15, 1, 18, 16)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public ReassessmentTableBlock() {
        super(Properties.of(Material.WOOD).strength(2.0f, 2.0f).harvestTool(ToolType.AXE)
                .lightLevel(l -> 5));
    }
    @Override public BlockRenderType getRenderShape(BlockState state) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return SHAPE;
    }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader reader) {
        return new ReassessmentTableTile();
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(rand.nextInt(9) < 3) return;
        if(world.getBlockEntity(pos) instanceof ReassessmentTableTile) {
            ReassessmentTableTile tile = (ReassessmentTableTile)world.getBlockEntity(pos);
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 1.0D;
            double z = pos.getZ() + 0.5D;
            double randomA = rand.nextInt(2) - 0.4;
            double randomB = rand.nextInt(2) - 0.3;
            if(tile.isValid){
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                        0.0D, 0.025D, 0.0D);
            }
        }
    }

    @Override public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof ReassessmentTableTile) {
            ReassessmentTableTile te = (ReassessmentTableTile)world.getBlockEntity(pos);
            te.invalidatePillars();
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                          Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide) {
            if(hand == Hand.MAIN_HAND && world.getBlockEntity(pos) instanceof ReassessmentTableTile) {
                ReassessmentTableTile tile = ((ReassessmentTableTile) world.getBlockEntity(pos));
                ItemStack handItem = player.getItemInHand(Hand.MAIN_HAND);
                if(!tile.isValid) {
                    player.displayClientMessage(
                            new TranslationTextComponent("tile.enhancedenchanting.reassessment_table.not_valid"),
                            true);
                    return ActionResultType.FAIL;
                }
                else {
                    tile.updateRecipe();
                    if(handItem.getItem() != ItemRegister.CRYSTAL.get()) {
                        tile.insertOrExtractItem(player, 0);
                    } else if(tile.getRecipe() != null){
                        tile.createResult();
                        if(!player.isCreative())
                            handItem.shrink(1);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }
}
