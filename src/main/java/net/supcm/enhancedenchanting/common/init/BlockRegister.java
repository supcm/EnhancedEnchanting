package net.supcm.enhancedenchanting.common.init;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BlockRegister {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
            EnhancedEnchanting.MODID);
    public static final RegistryObject<Block> ENCHANTED_TABLE = createBlock("enchanted_table",
            EnchantedTableBlock::new);
    public static final RegistryObject<Block> WORD_MACHINE = createBlock("word_machine",
            WordMachineBlock::new);
    public static final RegistryObject<Block> WORD_FORGE = createBlock("word_forge",
            WordForgeBlock::new);
    public static final RegistryObject<Block> LAVA_CRYSTAL_ORE_BLOCK = createBlock("lava_crystal_ore",
            LavaCrystalOreBlock::new);
    public static final RegistryObject<Block> THOUGHT_LOOM = createBlock("thought_loom",
            ThoughtLoomBlock::new);
    public static final RegistryObject<Block> WORD_ERASER = createBlock("word_eraser",
            WordEraserBlock::new);
    public static final RegistryObject<Block> EXALTATION_TABLE = createBlock("exaltation_table",
            ExaltationTableBlock::new);
    public static final RegistryObject<Block> REASSESSMENT_TABLE = BLOCKS.register("reassessment_table",
            ReassessmentTableBlock::new);
    public static final RegistryObject<Item> REASSESSMENT_TABLE_ITEM = ItemRegister.ITEMS.register("reassessment_table",
            () -> new BlockItem(REASSESSMENT_TABLE.get(), new Item.Properties()
                    .tab(EnhancedEnchanting.EETAB)
                    .rarity(Rarity.UNCOMMON)) {
                @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                                      List<ITextComponent> text, ITooltipFlag flag) {
                    text.add(1, new TranslationTextComponent("block.reassessment_table.info"));
                    text.add(2, new TranslationTextComponent("block.reassessment.info"));
                }
            });
    public static final RegistryObject<Block> REASSESSMENT_PILLAR = BLOCKS.register("reassessment_pillar",
            ReassessmentPillarBlock::new);
    public static final RegistryObject<Item> REASSESSMENT_PILLAR_ITEM = ItemRegister.ITEMS.register("reassessment_pillar",
            () -> new BlockItem(REASSESSMENT_PILLAR.get(), new Item.Properties()
                    .tab(EnhancedEnchanting.EETAB)
                    .rarity(Rarity.UNCOMMON)) {
                @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                                      List<ITextComponent> text, ITooltipFlag flag) {
                    text.add(1, new TranslationTextComponent("block.reassessment_pillar.info"));
                    text.add(2, new TranslationTextComponent("block.reassessment.info"));
                }
            });
    public static final RegistryObject<Block> MATRIX = BLOCKS.register("matrix", MatrixBlock::new);
    public static final RegistryObject<Item> MATRIX_ITEM = ItemRegister.ITEMS.register("matrix",
            () -> new BlockItem(MATRIX.get(), new Item.Properties()
                    .tab(EnhancedEnchanting.EETAB)
                    .rarity(Rarity.RARE)) {
                @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                                      List<ITextComponent> text, ITooltipFlag flag) {
                    text.add(1, new TranslationTextComponent("block.matrix.info"));
                    text.add(2, new TranslationTextComponent("block.matrix.info1"));
                }
            });
    public static final RegistryObject<Block> ENCHANTING_STATION = BLOCKS.register("enchanting_station",
            EnchantingStationBlock::new);
    public static final RegistryObject<Item> ENCHANTING_STATION_ITEM = ItemRegister.ITEMS.register("enchanting_station",
            () -> new BlockItem(ENCHANTING_STATION.get(), new Item.Properties()
                    .tab(EnhancedEnchanting.EETAB)
                    .rarity(Rarity.RARE)) {
                @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                                      List<ITextComponent> text, ITooltipFlag flag) {
                    text.add(1, new TranslationTextComponent("block.enchanting_station.info"));
                }
            });
    private static <T extends Block>RegistryObject<T> createBlock(String name, Supplier<T> supplier) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        ItemRegister.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties()
                        .tab(EnhancedEnchanting.EETAB)
                        .rarity(Rarity.RARE)));
        return block;
    }
    public static void reg(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
