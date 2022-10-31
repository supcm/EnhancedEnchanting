package net.supcm.enhancedenchanting.common.world.ore;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.supcm.enhancedenchanting.EnhancedEnchanting;

@Mod.EventBusSubscriber(modid = EnhancedEnchanting.MODID)
public class OreWorldGen {
    @SubscribeEvent public static void generate(final BiomeLoadingEvent e) {
        for(OreType ore : OreType.values()){
            OreFeatureConfig oreFeatureConfig = new OreFeatureConfig(
                    OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                    ore.getBlock().defaultBlockState(),
                    ore.getMaxVeinSize());
            ConfiguredPlacement<TopSolidRangeConfig> configuredPlacement =
                    Placement.RANGE.configured(new TopSolidRangeConfig(ore.getMinHeight(),
                            ore.getMinHeight(), ore.getMaxHeight()));
            ConfiguredFeature<?,?> feature = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                    ore.getBlock().getRegistryName(),
                    Feature.ORE.configured(oreFeatureConfig).decorated(configuredPlacement).
                            squared().count(6));
            e.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, feature);
        }
    }
}
