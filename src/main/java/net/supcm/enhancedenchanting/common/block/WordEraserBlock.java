package net.supcm.enhancedenchanting.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
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
import net.supcm.enhancedenchanting.common.block.entity.WordEraserTile;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

public class WordEraserBlock extends ContainerBlock {
    VoxelShape SHAPE = Stream.of(
            Block.box(0, 16, 14, 2, 17, 16),
            Block.box(-2, 0, -2, 18, 2, 18),
            Block.box(0, 2, 0, 2, 10, 2),
            Block.box(14, 2, 0, 16, 10, 2),
            Block.box(14, 2, 14, 16, 10, 16),
            Block.box(0, 2, 14, 2, 10, 16),
            Block.box(0, 10, 0, 16, 16, 16),
            Block.box(0, 16, 0, 2, 17, 2),
            Block.box(14, 16, 0, 16, 17, 2),
            Block.box(14, 16, 14, 16, 17, 16)
            ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public WordEraserBlock() {
        super(Properties.of(Material.STONE)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .strength(12.0f, 21.0f));
    }
    @Override public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordEraserTile) {
            WordEraserTile te = (WordEraserTile)world.getBlockEntity(pos);
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }
    @Override public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos,
                                         ISelectionContext ctx) { return SHAPE; }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader reader) {
        return new WordEraserTile();
    }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordEraserTile) {
            WordEraserTile tile = (WordEraserTile)world.getBlockEntity(pos);
            ItemStack handItem = player.getItemInHand(Hand.MAIN_HAND);
            if(hand == Hand.MAIN_HAND) {
                if(handItem.getItem() != Items.BOOK) {
                    tile.insertOrExtractItem(player, 0);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
                    return ActionResultType.CONSUME;
                } else {
                    if(!tile.handler.getStackInSlot(0).isEmpty() && tile.handler.getStackInSlot(0).isEnchanted()){
                        tile.proceedErasing();
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        } else
            return ActionResultType.CONSUME;
        return ActionResultType.PASS;
    }
}
