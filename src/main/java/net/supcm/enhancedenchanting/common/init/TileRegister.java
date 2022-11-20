package net.supcm.enhancedenchanting.common.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.block.entity.*;

public class TileRegister {
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES,
            EnhancedEnchanting.MODID);
    public static final RegistryObject<TileEntityType<EnchantedTableTile>> ENCHANTED_TABLE_TILE_TYPE =
            TILES.register("enchanted_table", () -> TileEntityType.Builder.of(EnchantedTableTile::new,
                    BlockRegister.ENCHANTED_TABLE.get()).build(null));
    public static final RegistryObject<TileEntityType<WordMachineTile>> WORD_MACHINE_TILE_TYPE =
            TILES.register("word_machine", () -> TileEntityType.Builder.of(WordMachineTile::new,
                    BlockRegister.WORD_MACHINE.get()).build(null));
    public static final RegistryObject<TileEntityType<WordForgeTile>> WORD_FORGE_TILE_TYPE =
            TILES.register("word_forge", () -> TileEntityType.Builder.of(WordForgeTile::new,
                    BlockRegister.WORD_FORGE.get()).build(null));
    public static final RegistryObject<TileEntityType<MatrixTile>> MATRIX_TILE_TYPE =
            TILES.register("matrix", () -> TileEntityType.Builder.of(MatrixTile::new,
                    BlockRegister.MATRIX.get()).build(null));
    public static final RegistryObject<TileEntityType<EnchantingStationTile>> ENCHANTING_STATION_TILE_TYPE =
            TILES.register("enchanting_station", () -> TileEntityType.Builder.of(EnchantingStationTile::new,
                    BlockRegister.ENCHANTING_STATION.get()).build(null));
    public static final RegistryObject<TileEntityType<ThoughtLoomTile>> THOUGHT_WEAVER_TILE_TYPE =
            TILES.register("thought_loom", () -> TileEntityType.Builder.of(ThoughtLoomTile::new,
                    BlockRegister.THOUGHT_LOOM.get()).build(null));
    public static final RegistryObject<TileEntityType<ReassessmentTableTile>> REASSESSMENT_TABLE_TILE_TYPE =
            TILES.register("reassessment_table", () -> TileEntityType.Builder.of(ReassessmentTableTile::new,
                    BlockRegister.REASSESSMENT_TABLE.get()).build(null));
    public static final RegistryObject<TileEntityType<ReassessmentPillarTile>> REASSESSMENT_PILLAR_TILE_TYPE =
            TILES.register("reassessment_pillar", () -> TileEntityType.Builder.of(ReassessmentPillarTile::new,
                    BlockRegister.REASSESSMENT_PILLAR.get()).build(null));
    public static void reg(IEventBus bus) {
        TILES.register(bus);
    }
}
