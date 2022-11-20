package net.supcm.enhancedenchanting.common.init;

import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.enchantments.GravityCoreEnchantment;
import net.supcm.enhancedenchanting.common.enchantments.UnstabilityEnchantment;
import net.supcm.enhancedenchanting.common.enchantments.XpBoostEnchantment;

public class EnchantmentRegister {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, EnhancedEnchanting.MODID);

    public static RegistryObject<Enchantment> XP_BOOST = ENCHANTMENTS.register("xp_boost",
            XpBoostEnchantment::new);
    public static RegistryObject<Enchantment> GRAVITY_CORE = ENCHANTMENTS.register("gravity_core",
            GravityCoreEnchantment::new);
    public static RegistryObject<Enchantment> UNSTABILITY = ENCHANTMENTS.register("unstability",
            UnstabilityEnchantment::new);
    public static void reg(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}
