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
import net.supcm.enhancedenchanting.common.block.entity.WordMachineTile;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.Stream;

public class WordMachineBlock extends ContainerBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(2, 12, 2, 14, 18, 14),
            Block.box(0, 12, 0, 2, 19, 2),
            Block.box(14, 12, 14, 16, 19, 16),
            Block.box(14, 12, 0, 16, 19, 2),
            Block.box(0, 12, 14, 2, 19, 16)
    ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public WordMachineBlock() {
        super(Properties.of(Material.STONE)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE)
                .requiresCorrectToolForDrops()
                .strength(7.0f, 15.0f));
    }
    @Override public BlockRenderType getRenderShape(BlockState p_149645_1_) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_,
                               ISelectionContext p_220053_4_) { return SHAPE; }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader reader) {
        return new WordMachineTile();
    }
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState nextState, boolean bool) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof WordMachineTile) {
            WordMachineTile te = (WordMachineTile)world.getBlockEntity(pos);
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(0)));
            world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(),
                    te.handler.getStackInSlot(1)));
        }
        super.onRemove(state, world, pos, nextState, bool);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos,
                                PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide) {
            if(hand == Hand.MAIN_HAND) {
                WordMachineTile tile = (WordMachineTile)world.getBlockEntity(pos);
                ItemStack handItem = player.getItemInHand(hand);
                double hitLoc = hit.getLocation().y;
                boolean up = hitLoc-(int)hitLoc >= 0.75d || (int)hitLoc > pos.getY();
                if(handItem.isEmpty() || handItem.getItem() instanceof ItemRegister.GlyphItem){
                    if(up) tile.insertOrExtractItem(player, 0);
                    else tile.insertOrExtractItem(player, 1);
                    world.playSound(null, pos, SoundEvents.END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1f, 1f);
                    boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                            && !tile.handler.getStackInSlot(1).isEmpty();
                    if(isTier2) tile.getEnchLevel(1);
                    else tile.getEnchLevel(0);
                } else {
                    if(handItem.getItem() == Items.BOOK) {
                        boolean isTier2 = !tile.handler.getStackInSlot(0).isEmpty()
                                && !tile.handler.getStackInSlot(1).isEmpty();
                        if(isTier2)
                            return tile.enchantBook(player, handItem, 1);
                        else {
                            if(!tile.handler.getStackInSlot(0).isEmpty() ||
                                    !tile.handler.getStackInSlot(0).isEmpty())
                                return tile.enchantBook(player, handItem,  0);
                        }
                    } else
                        return tile.enchantItem(player, handItem);
                }
            }
        } else {
            WordMachineTile tile = (WordMachineTile)world.getBlockEntity(pos);
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.95D;
            double z = pos.getZ() + 0.5D;
            player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
            if(!tile.handler.getStackInSlot(0).isEmpty() || !tile.handler.getStackInSlot(1).isEmpty())
                world.addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0.0D, 0.025D, 0.0D);
        }
        return ActionResultType.PASS;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(rand.nextInt(9) < 3) return;
        WordMachineTile te;
        if(world.getBlockEntity(pos) instanceof WordMachineTile)
            te = (WordMachineTile) world.getBlockEntity(pos);
        else return;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;
        double randomA = rand.nextDouble() - 0.4;
        double randomB = rand.nextDouble() - 0.3;
        if((!te.handler.getStackInSlot(0).isEmpty() || !te.handler.getStackInSlot(1).isEmpty()) &&
        !(!te.handler.getStackInSlot(0).isEmpty() && !te.handler.getStackInSlot(1).isEmpty()))
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        if(!te.handler.getStackInSlot(1).isEmpty() && !te.handler.getStackInSlot(0).isEmpty())
            world.addParticle(ParticleTypes.SOUL,
                    x + randomA, y + 0.45 + randomA-randomB, z + randomB,
                    0.0D, -0.025D, 0.0D);
        world.addParticle(ParticleTypes.ENCHANT, x + randomA, y + randomA-randomB,
                    z + randomB, 0.0D, 0.015D, 0.0D);
    }
}
