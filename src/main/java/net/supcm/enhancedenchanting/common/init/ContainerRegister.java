package net.supcm.enhancedenchanting.common.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.inventory.container.ExaltationTableContainer;

public class ContainerRegister {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS,
            EnhancedEnchanting.MODID);
    public static final RegistryObject<ContainerType<ExaltationTableContainer>> EXALTATION_TABLE_CONTAINER =
            CONTAINERS.register("exaltation_table", () -> IForgeContainerType.create(ExaltationTableContainer::new));
    public static void reg(IEventBus bus) {
        CONTAINERS.register(bus);
    }
}
