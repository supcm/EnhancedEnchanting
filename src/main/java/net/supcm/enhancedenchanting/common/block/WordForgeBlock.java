package net.supcm.enhancedenchanting.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.supcm.enhancedenchanting.common.block.entity.WordForgeTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class WordForgeBlock extends ContainerBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(1, 14, 1, 15, 18, 15),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(-2, 8, 4, 0, 10, 12),
            Block.box(16, 8, 4, 18, 10, 12),
            Block.box(4, 8, -2, 12, 10, 0),
            Block.box(4, 8, 16, 12, 10, 18),
            Block.box(3, 10, 3, 13, 14, 13),
            Block.box(0, 10, 14, 2, 11, 16),
            Block.box(14, 10, 0, 16, 11, 2),
            Block.box(0, 10, 0, 2, 11, 2),
            Block.box(14, 10, 14, 16, 11, 16)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

    public WordForgeBlock() {
        super(Properties.of(Material.STONE)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .strength(12.0f, 21.0f)
                /*.noOcclusion().lightLevel(l->1)*/); }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return new WordForgeTile();
    }
    @Override public BlockRenderType getRenderShape(BlockState p_149645_1_) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
                               ISelectionContext p_220053_4_) { return SHAPE; }
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordForgeTile) {
            WordForgeTile te = (WordForgeTile)world.getBlockEntity(pos);
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(2)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide && hand == Hand.MAIN_HAND) {
            if(world.getBlockEntity(pos) instanceof WordForgeTile) {
                WordForgeTile tile = (WordForgeTile)world.getBlockEntity(pos);
                ItemStack handItem = player.getItemInHand(hand);
                double hitLoc = hit.getLocation().y;
                boolean up = hitLoc-(int)hitLoc >= 0.9D || (int)hitLoc > pos.getY();
                boolean low = hitLoc-(int)hitLoc <= 0.45D && (int)hitLoc < pos.getY()+1;
                if(handItem.isEmpty() || handItem.getItem() instanceof ItemRegister.ItemSymbol) {
                    if(up) tile.insertOrExtractItem(player, 0);
                    else if(low) tile.insertOrExtractItem(player, 2);
                    else tile.insertOrExtractItem(player, 1);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
                    boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                            && !tile.handler.getStackInSlot(1).isEmpty();
                    boolean isTier3 = !tile.handler.getStackInSlot(0).isEmpty() &&
                            !tile.handler.getStackInSlot(1).isEmpty() &&
                            !tile.handler.getStackInSlot(2).isEmpty();
                    if(isTier3) {
                        tile.getEnchLevel(2);
                    } else if(isTier2) {
                        tile.getEnchLevel(1);;
                    } else {
                        tile.getEnchLevel(0);
                    }
                } else {
                        if (handItem.getItem() == Items.BOOK) {
                            boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                                    && !tile.handler.getStackInSlot(1).isEmpty() &&
                                    tile.handler.getStackInSlot(2).isEmpty();
                            boolean isTier3 = !tile.handler.getStackInSlot(0).isEmpty() &&
                                    !tile.handler.getStackInSlot(1).isEmpty() &&
                                    !tile.handler.getStackInSlot(2).isEmpty();
                            if (isTier3) {
                                return tile.enchantBook(player, handItem, 2);
                            } else if (isTier2) {
                                return tile.enchantBook(player, handItem, 1);
                            } else {
                                if(!tile.handler.getStackInSlot(0).isEmpty()
                                        && tile.handler.getStackInSlot(1).isEmpty()
                                        && tile.handler.getStackInSlot(2).isEmpty()) {
                                    return tile.enchantBook(player, handItem, 0);
                                } else if(!tile.handler.getStackInSlot(1).isEmpty()
                                        && tile.handler.getStackInSlot(0).isEmpty()
                                        && tile.handler.getStackInSlot(2).isEmpty()){
                                    return tile.enchantBook(player, handItem, 0);
                                } else if(!tile.handler.getStackInSlot(2).isEmpty()
                                        && tile.handler.getStackInSlot(1).isEmpty()
                                        && tile.handler.getStackInSlot(0).isEmpty()) {
                                    return tile.enchantBook(player, handItem, 0);
                                }
                            }
                        } else
                            tile.enchantItem(player, handItem);
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(rand.nextInt(9) < 3) return;
        WordForgeTile te = null;
        if(world.getBlockEntity(pos) instanceof WordForgeTile)
            te = (WordForgeTile) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.35D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if((!te.handler.getStackInSlot(0).isEmpty() || !te.handler.getStackInSlot(1).isEmpty() ||
                !te.handler.getStackInSlot(2).isEmpty()) &&
                !(!te.handler.getStackInSlot(0).isEmpty() && !te.handler.getStackInSlot(1).isEmpty() &&
                        !te.handler.getStackInSlot(2).isEmpty()))
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        if(!te.handler.getStackInSlot(1).isEmpty() && !te.handler.getStackInSlot(0).isEmpty() &&
        !te.handler.getStackInSlot(2).isEmpty())
            world.addParticle(ParticleTypes.SOUL,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        world.addParticle(ParticleTypes.ENCHANT, x + randomA, y + randomA-randomB,
                z + randomB, 0.0D, 0.015D, 0.0D);
    }
}
