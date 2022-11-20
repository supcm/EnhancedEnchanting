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
import net.supcm.enhancedenchanting.common.block.entity.MatrixTile;
import net.supcm.enhancedenchanting.common.block.entity.ThoughtLoomTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class ThoughtLoomBlock extends ContainerBlock {
    VoxelShape SHAPE = Stream.of(
            Block.box(0, 15, 0, 16, 16, 16),
            Block.box(0, 0, 0, 16, 3, 16),
            Block.box(1, 3, 1, 15, 15, 15)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public ThoughtLoomBlock() {
        super(Properties.of(Material.WOOD).lightLevel(state -> 2).noOcclusion()
                .strength(7.0f,7.0f) .requiresCorrectToolForDrops()
                .harvestLevel(0).harvestTool(ToolType.AXE));
    }
    @Override public BlockRenderType getRenderShape(BlockState state) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos,
                                         ISelectionContext ctx) { return SHAPE; }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader reader) { return new ThoughtLoomTile(); }
    @Override public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof ThoughtLoomTile) {
            ThoughtLoomTile te = (ThoughtLoomTile)world.getBlockEntity(pos);
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(2)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(3)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos,
                                          PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide && hand == Hand.MAIN_HAND) {
            if(world.getBlockEntity(pos) instanceof ThoughtLoomTile) {
                ThoughtLoomTile tile = (ThoughtLoomTile)world.getBlockEntity(pos);
                ItemStack handItem = player.getItemInHand(hand);
                if(handItem.getItem() instanceof ItemRegister.UnstableGlyphItem ||
                        (handItem.isEmpty() && !tile.handler.getStackInSlot(0).isEmpty() && !player.isCrouching())) {
                    tile.insertOrExtractItem(player, 0);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS,
                            1.0f, 1.0f);
                }
                else if(handItem.getItem() == ItemRegister.CONCEPTION_BASE.get())
                    return tile.createConception(player, handItem);
                else {
                    if(!player.isCrouching()) {
                        if(tile.handler.getStackInSlot(1).isEmpty())
                            tile.insertOrExtractItem(player, 1);
                        else if(tile.handler.getStackInSlot(2).isEmpty())
                            tile.insertOrExtractItem(player, 2);
                        else
                            tile.insertOrExtractItem(player, 3);
                        world.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS,
                                1.0f, 1.0f);
                        return ActionResultType.CONSUME;
                    } else {
                        if(!tile.handler.getStackInSlot(3).isEmpty())
                            tile.insertOrExtractItem(player, 3);
                        else if(!tile.handler.getStackInSlot(2).isEmpty())
                            tile.insertOrExtractItem(player, 2);
                        else
                            tile.insertOrExtractItem(player, 1);
                        world.playSound(null, pos, SoundEvents.TRIPWIRE_ATTACH, SoundCategory.BLOCKS,
                                1.0f, 1.0f);
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(rand.nextInt(9) < 3) return;
        ThoughtLoomTile te = null;
        if(world.getBlockEntity(pos) instanceof MatrixTile)
            te = (ThoughtLoomTile) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if(!te.handler.getStackInSlot(0).isEmpty())
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        if(!te.handler.getStackInSlot(1).isEmpty() ||
                !te.handler.getStackInSlot(2).isEmpty() ||
                !te.handler.getStackInSlot(3).isEmpty())
            world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
    }
}
