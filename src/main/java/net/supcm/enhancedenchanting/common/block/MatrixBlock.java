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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.common.block.entity.MatrixTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class MatrixBlock extends ContainerBlock{
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(4, 14, 4, 12, 18, 12),
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 10, 0, 16, 12, 16),
            Block.box(-2, 12, -2, 18, 14, 18),
            Block.box(1, 2, 1, 15, 10, 15)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public MatrixBlock() { super(Properties.of(Material.STONE).strength(7.0f, 7.0f)); }
    @Override public BlockRenderType getRenderShape(BlockState p_149645_1_) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
                                         ISelectionContext p_220053_4_) { return SHAPE; }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader world) { return new MatrixTile(); }
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof MatrixTile) {
            MatrixTile te = (MatrixTile)world.getBlockEntity(pos);
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide && hand == Hand.MAIN_HAND) {
            if(world.getBlockEntity(pos) instanceof MatrixTile) {
                MatrixTile tile = (MatrixTile)world.getBlockEntity(pos);
                ItemStack handItem = player.getItemInHand(hand);
                double hitLoc = hit.getLocation().y;
                boolean up = hitLoc-(int)hitLoc >= 0.75d || (int)hitLoc > pos.getY();
                if(!tile.doCraft){
                    if (up && (handItem.isEmpty() || handItem.getItem() == ItemRegister.PLATE.get())) {
                        tile.insertOrExtractItem(player, 0);
                        world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
                    } else if (handItem.isEmpty() || (handItem.getItem() instanceof ItemRegister.GlyphItem
                        || handItem.getItem() instanceof ItemRegister.UnstableGlyphItem)) {
                        tile.insertOrExtractItem(player, 1);
                        world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
                    } else if (handItem.getItem() == ItemRegister.LAVA_CRYSTAL.get()) {
                        if (!tile.handler.getStackInSlot(0).isEmpty()) {
                            if (!player.isCreative()) {
                                if (player.experienceLevel < 3 && !player.isCreative()) {
                                    player.sendMessage(new TranslationTextComponent("enchanting.notenoughxp",
                                            0), player.getUUID());
                                    return ActionResultType.PASS;
                                }
                                handItem.shrink(1);
                                player.onEnchantmentPerformed(handItem, 3);
                            }
                            world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL,
                                    SoundCategory.BLOCKS, 1f, 1f);
                            tile.setDoCraft(true);
                        }
                    }
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(rand.nextInt(9) < 3) return;
        MatrixTile te = null;
        if(world.getBlockEntity(pos) instanceof MatrixTile)
            te = (MatrixTile) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if(te.doCraft)
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        world.addParticle(ParticleTypes.ENCHANT, x + randomA, y + randomA-randomB,
                z + randomB, 0.0D, 0.015D, 0.0D);
        world.playSound(null, pos, SoundEvents.SOUL_ESCAPE,
                SoundCategory.BLOCKS,1.0F, 1.0F);
    }
}
