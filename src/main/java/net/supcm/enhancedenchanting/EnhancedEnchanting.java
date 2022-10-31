package net.supcm.enhancedenchanting;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.supcm.enhancedenchanting.common.data.recipes.UsingConfigCondition;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.init.TileRegister;
import net.supcm.enhancedenchanting.common.init.RecipeRegister;
import net.supcm.enhancedenchanting.common.init.EnchantmentRegister;
import net.supcm.enhancedenchanting.common.init.EntityTypeRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import net.supcm.enhancedenchanting.common.network.PacketHandler;

@Mod(value=EnhancedEnchanting.MODID)
public class EnhancedEnchanting {
    public static final String MODID = "enhancedenchanting";
    public static final ItemGroup EETAB = new ItemGroup("enhancedenchantingtab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BlockRegister.ENCHANTED_TABLE.get());
        }
    };
    public static final ItemGroup SYMBOLSTAB = new ItemGroup("symbolstab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegister.OKU.get());
        }
    };
    public EnhancedEnchanting() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.register(this);
        ItemRegister.reg(bus);
        BlockRegister.reg(bus);
        TileRegister.reg(bus);
        EnchantmentRegister.reg(bus);
        RecipeRegister.reg(bus);
        EntityTypeRegister.reg(bus);
        ItemTags.createOptional(new ResourceLocation(MODID, "symbols"));
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EnhancedEnchantingConfig.SPEC,
                MODID + "-common.toml");
    }
    public void commonSetup(FMLCommonSetupEvent e) { PacketHandler.init(); }
    @SubscribeEvent
    public void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> e) {
        CraftingHelper.register(UsingConfigCondition.Serializer.INSTANCE);
    }
}
