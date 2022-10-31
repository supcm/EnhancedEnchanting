package net.supcm.enhancedenchanting.common.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import java.util.Random;

public class LavaCrystalOreBlock extends OreBlock {
    public LavaCrystalOreBlock() {
        super(Properties.of(Material.STONE).strength(2.0f, 2.0f)
                .requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).harvestLevel(1)
                        .lightLevel(light -> 2));
    }

    @Override public BlockRenderType getRenderShape(BlockState state) { return BlockRenderType.MODEL; }

    @Override protected int xpOnDrop(Random random) { return 1+random.nextInt( 7); }
}
