package net.supcm.enhancedenchanting.common.world.ore;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.supcm.enhancedenchanting.common.init.BlockRegister;

public enum OreType {
    LAVA_CRYSTAL(Lazy.of(BlockRegister.LAVA_CRYSTAL_ORE_BLOCK), 8, 4, 64);
    private final Lazy<Block> block;
    private final int maxVeinSize;
    private final int minHeight;
    private final int maxHeight;

    OreType(Lazy<Block> block, int maxVeinSize, int minHeight, int maxHeight) {
        this.block = block;
        this.maxVeinSize = maxVeinSize;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
    public int getMaxHeight() { return maxHeight; }
    public int getMaxVeinSize() { return maxVeinSize; }
    public int getMinHeight() { return minHeight; }
    public Block getBlock() { return block.get(); }
    public static OreType get(Block block) {
        for(OreType ore : values())
            if(block == ore.block)
                return ore;
        return null;
    }
}
