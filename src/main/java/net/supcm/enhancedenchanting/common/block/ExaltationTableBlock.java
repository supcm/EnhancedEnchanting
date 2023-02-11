package net.supcm.enhancedenchanting.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.supcm.enhancedenchanting.common.block.entity.ExaltationTableTile;
import net.supcm.enhancedenchanting.common.inventory.container.ExaltationTableContainer;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ExaltationTableBlock extends ContainerBlock {
    public static final VoxelShape SHAPE = Stream.of(
            Block.box(7, 10, 7, 9, 12, 9),
            Block.box(0, 0, 7, 16, 8, 9),
            Block.box(7, 0, 0, 9, 8, 16),
            Block.box(0, 8, 0, 16, 10, 16),
            Block.box(0, 10, 0, 2, 14, 2),
            Block.box(0, 10, 14, 2, 14, 16),
            Block.box(14, 10, 14, 16, 14, 16),
            Block.box(14, 10, 0, 16, 14, 2)
            ).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
    public ExaltationTableBlock() {
        super(Properties.of(Material.STONE).harvestLevel(2).harvestTool(ToolType.PICKAXE)
                .strength(18.0f, 9.0f));
    }
    @Override public BlockRenderType getRenderShape(BlockState state) { return BlockRenderType.MODEL; }
    @Override public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos,
                                         ISelectionContext ctx) { return SHAPE; }
    @Nullable @Override public TileEntity newBlockEntity(IBlockReader reader) { return new ExaltationTableTile(); }
    @Override public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                          Hand hand, BlockRayTraceResult ray) {
        if(!world.isClientSide && world.getBlockEntity(pos) instanceof ExaltationTableTile
            && !player.isCrouching())
            NetworkHooks.openGui((ServerPlayerEntity) player,
                    getMenuProvider(state, world, pos), pos);
        return ActionResultType.SUCCESS;
    }
    @Override public INamedContainerProvider getMenuProvider(BlockState state, World world,
                                                             BlockPos pos) {
        return new INamedContainerProvider() {
            @Override public ITextComponent getDisplayName() {
                return new TranslationTextComponent("block.enhancedenchanting.exaltation_table");
            }
            @Override public Container createMenu(int index, PlayerInventory inv,
                                                  PlayerEntity player) {
                return new ExaltationTableContainer(index, inv, ExaltationTableContainer.getTileAtPos(pos, world));
            }
        };
    }
}
