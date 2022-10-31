package net.supcm.enhancedenchanting.common.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.entity.GuardianEntity;

public class EntityTypeRegister {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.ENTITIES, EnhancedEnchanting.MODID);

    public static final RegistryObject<EntityType<GuardianEntity>> GUARDIAN = ENTITY_TYPES.register("guardian",
            () -> EntityType.Builder.<GuardianEntity>of(GuardianEntity::new, EntityClassification.CREATURE)
                    .fireImmune().canSpawnFarFromPlayer().sized(1.35f, 1.35f)
                    .build(new ResourceLocation(EnhancedEnchanting.MODID, "guardian").toString()));

    public static void reg(IEventBus bus) { ENTITY_TYPES.register(bus); }
}
