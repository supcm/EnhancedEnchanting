package net.supcm.enhancedenchanting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.supcm.enhancedenchanting.client.block.entity.renderer.*;
import net.supcm.enhancedenchanting.common.init.*;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;
import net.supcm.enhancedenchanting.common.entity.GuardianEntity;
import net.supcm.enhancedenchanting.client.entity.renderer.GuardianRenderer;
import net.supcm.enhancedenchanting.common.network.PacketHandler;
import net.supcm.enhancedenchanting.common.network.packets.T2ListPacket;
import net.supcm.enhancedenchanting.common.network.packets.T3ListPacket;
import java.util.*;

@Mod.EventBusSubscriber(modid = EnhancedEnchanting.MODID)
public class EventHandler {
    @SubscribeEvent public static void onLootTableLoad(LootTableLoadEvent e) {
        if(e.getTable().getLootTableId().getPath().startsWith("chest") &&
                !e.getTable().getLootTableId().getPath().contains("village"))
            e.getTable().addPool(LootPool.lootPool()
                    .name("casket_pool")
                    .add(TableLootEntry.lootTableReference(new ResourceLocation(EnhancedEnchanting.MODID,
                            "casket_pool")))
                    .build());
        else if(e.getTable().getLootTableId().getPath().startsWith("entities"))
            e.getTable().addPool(LootPool.lootPool()
                    .name("unstable_glyphs")
                    .add(TableLootEntry.lootTableReference(new ResourceLocation(EnhancedEnchanting.MODID,
                            "unstable_glyphs")))
                    .build());
    }
    @SubscribeEvent public static void onWorldLoaded(WorldEvent.Load e) {
        if(e.getWorld() instanceof ServerWorld) {
            long seed = ((ServerWorld)e.getWorld()).getSeed();
                randomizeFirstList(new Random(seed));
                createSecondList(new Random(seed));
                createThirdList(new Random(seed));
        }
    }
    @SubscribeEvent public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        if(!e.getPlayer().level.isClientSide){
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new T2ListPacket(createSecondList(new Random(((ServerWorld)e.getPlayer().level).getSeed()))));
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(),
                    new T3ListPacket(createThirdList(new Random(((ServerWorld)e.getPlayer().level).getSeed()))));
        }
    }

    private static Map<String, Enchantment> createThirdMap(Random rand) {
        List<String> words = new ArrayList<>();
        for (String symbol : EnchantmentsList.SYMBOLS_LIST)
            for (String symbol1 : EnchantmentsList.SYMBOLS_LIST)
                for (String symbol2 : EnchantmentsList.SYMBOLS_LIST)
                    words.add(symbol + "_" + symbol1
                            + "_" + symbol2);
        Collections.shuffle(words, rand);
        for(int i = 0; i < EnchantmentsList.T3_LIST.size(); i++)
            if(!EnchantmentsList.T3_MAP.containsValue(EnchantmentsList.T3_LIST.get(i)))
                EnchantmentsList.T3_MAP.put(words.get(i), EnchantmentsList.T3_LIST.get(i));
        return EnchantmentsList.T3_MAP;
    }

    private static Map<String, Enchantment> createSecondMap(Random rand) {
        List<String> words = new ArrayList<>();
        for (String symbol : EnchantmentsList.SYMBOLS_LIST)
            for (String symbol1 : EnchantmentsList.SYMBOLS_LIST)
                words.add(symbol + "_" + symbol1);
        Collections.shuffle(words, rand);
        for(int i = 0; i < EnchantmentsList.T2_LIST.size(); i++)
            if(!EnchantmentsList.T2_MAP.containsValue(EnchantmentsList.T2_LIST.get(i)))
                EnchantmentsList.T2_MAP.put(words.get(i), EnchantmentsList.T2_LIST.get(i));
        return EnchantmentsList.T2_MAP;
    }

    private static Map<String, Enchantment> createFirstMap(Random rand) {
        Collections.shuffle(EnchantmentsList.SYMBOLS_LIST, rand);
        for(int i = 0; i < EnchantmentsList.T1_LIST.size(); i++)
            EnchantmentsList.T1_MAP.put(EnchantmentsList.SYMBOLS_LIST.get(i),
                    EnchantmentsList.T1_LIST.get(i));
        return EnchantmentsList.T1_MAP;
    }

    public static void randomizeFirstList(Random rand) {
        Collections.shuffle(EnchantmentsList.SYMBOLS_LIST, rand);
        for(int i = 0; i < EnchantmentsList.T1_LIST.size(); i++)
            EnchantmentsList.T1_MAP.put(EnchantmentsList.SYMBOLS_LIST.get(i),
                    EnchantmentsList.T1_LIST.get(i));
    }
    public static List<String> createSecondList(Random rand) {
        List<String> words = new ArrayList<>();
        for (String symbol : EnchantmentsList.SYMBOLS_LIST)
            for (String symbol1 : EnchantmentsList.SYMBOLS_LIST)
                words.add(symbol + "_" + symbol1);
        Collections.shuffle(words, rand);
        for(int i = 0; i < EnchantmentsList.T2_LIST.size(); i++)
            if(!EnchantmentsList.T2_MAP.containsValue(EnchantmentsList.T2_LIST.get(i)))
                EnchantmentsList.T2_MAP.put(words.get(i), EnchantmentsList.T2_LIST.get(i));
        return new ArrayList<>(EnchantmentsList.T2_MAP.keySet());
    }
    public static List<String> createThirdList(Random rand) {
        List<String> words = new ArrayList<>();
        for (String symbol : EnchantmentsList.SYMBOLS_LIST)
            for (String symbol1 : EnchantmentsList.SYMBOLS_LIST)
                for (String symbol2 : EnchantmentsList.SYMBOLS_LIST)
                    words.add(symbol + "_" + symbol1
                            + "_" + symbol2);
        Collections.shuffle(words, rand);
        for(int i = 0; i < EnchantmentsList.T3_LIST.size(); i++)
            if(!EnchantmentsList.T3_MAP.containsValue(EnchantmentsList.T3_LIST.get(i)))
                EnchantmentsList.T3_MAP.put(words.get(i), EnchantmentsList.T3_LIST.get(i));
        return new ArrayList<>(EnchantmentsList.T3_MAP.keySet());
    }

    @SubscribeEvent public static void xpDrop(BlockEvent.BreakEvent e) {
        if(e.getPlayer() != null && e.getPlayer().getMainHandItem().isEnchanted()) {
            Map<Enchantment, Integer> data =
                    EnchantmentHelper.getEnchantments(e.getPlayer().getMainHandItem());
            if(data.containsKey(EnchantmentRegister.XP_BOOST.get()))
                e.setExpToDrop(e.getExpToDrop() *
                        data.get(EnchantmentRegister.XP_BOOST.get()));
        }
    }
    @SubscribeEvent public static void xpDrop(LivingExperienceDropEvent e) {
        if(e.getAttackingPlayer() != null && e.getAttackingPlayer().getMainHandItem().isEnchanted()) {
            Map<Enchantment, Integer> data =
                    EnchantmentHelper.getEnchantments(e.getAttackingPlayer().getMainHandItem());
            if(data.containsKey(EnchantmentRegister.XP_BOOST.get()))
                e.setDroppedExperience(e.getDroppedExperience() *
                        data.get(EnchantmentRegister.XP_BOOST.get()));
        }
    }
    @SubscribeEvent public static void itemDrop(LivingDropsEvent e) {
        if(e.isRecentlyHit() && e.getSource() instanceof EntityDamageSource) {
            EntityDamageSource source = (EntityDamageSource)e.getSource();
            if(source.getMsgId().equals("player")) {
                PlayerEntity player = (PlayerEntity)source.getEntity();
                if(player != null && player.getMainHandItem().isEnchanted()) {
                    Map<Enchantment, Integer> data =
                            EnchantmentHelper.getEnchantments(player.getMainHandItem());
                    if(data.containsKey(EnchantmentRegister.UNSTABILITY.get()) &&
                    e.getEntity().level.getRandom().nextInt(3) == 0) {
                        e.getDrops().clear();
                        e.getDrops().add(createDropsList(e.getEntity())
                                .get(e.getEntity().level.getRandom().nextInt(6)));
                    }
                    if(data.containsKey(EnchantmentRegister.XP_BOOST.get()))
                        e.setCanceled(true);
                }
            }
        }
    }
    private static List<ItemEntity> createDropsList(Entity entity) {
        List<ItemEntity> entities = new ArrayList<>();
        ItemStack[] stack = new ItemStack[] {
                new ItemStack(ItemRegister.IRO.get()),
                new ItemStack(ItemRegister.NOY.get()),
                new ItemStack(ItemRegister.SAT.get()),
                new ItemStack(ItemRegister.BAL.get()),
                new ItemStack(ItemRegister.WOY.get()),
                new ItemStack(ItemRegister.VER.get())
        };
        for(int i = 0; i < 6; i++) {
            ItemEntity item = new ItemEntity(entity.level,
                    entity.blockPosition().getX(),
                    entity.blockPosition().getY(),
                    entity.blockPosition().getZ(),
                    stack[i]);
            entities.add(item);
        }
        return entities;
    }
    @SubscribeEvent public static void onRightClicked(PlayerInteractEvent.RightClickItem e) {
        if(!e.getWorld().isClientSide){
            if (e.getItemStack().isEnchanted()) {
                Map<Enchantment, Integer> data =
                        EnchantmentHelper.getEnchantments(e.getItemStack());
                if (data.containsKey(EnchantmentRegister.GRAVITY_CORE.get())) {
                    List<ItemEntity> entities = e.getWorld().getEntitiesOfClass(ItemEntity.class,
                            new AxisAlignedBB(
                                    e.getPlayer().blockPosition().getX() - (4*data.get(EnchantmentRegister.GRAVITY_CORE.get())),
                                    e.getPlayer().blockPosition().getY() - (2*data.get(EnchantmentRegister.GRAVITY_CORE.get())),
                                    e.getPlayer().blockPosition().getZ() - (4*data.get(EnchantmentRegister.GRAVITY_CORE.get())),
                                    e.getPlayer().blockPosition().getX() + (4*data.get(EnchantmentRegister.GRAVITY_CORE.get())),
                                    e.getPlayer().blockPosition().getY() + (2*data.get(EnchantmentRegister.GRAVITY_CORE.get())),
                                    e.getPlayer().blockPosition().getZ() + (4*data.get(EnchantmentRegister.GRAVITY_CORE.get()))));
                    if(entities.size() > 0 && !e.getPlayer().isCreative()) {
                        if(e.getItemStack().isDamageableItem())
                            e.getItemStack().setDamageValue(e.getItemStack().getDamageValue() + 1);
                        else
                            e.getItemStack().shrink(1);
                    }
                    for (ItemEntity entity : entities) {
                        if(e.getPlayer().isCreative() && e.getPlayer().inventory.getFreeSlot() == -1) break;
                        entity.moveTo(e.getPos().getX() + 0.5,
                                e.getPos().getY() + 0.5,
                                e.getPos().getZ() + 0.5);
                    }
                }
            }
        }
    }
    @Mod.EventBusSubscriber(modid = EnhancedEnchanting.MODID,
            bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusSubscriber {
        @SubscribeEvent public static void list(FMLLoadCompleteEvent e) {
            e.enqueueWork(EnchantmentsList::initAllLists);
        }
        @SubscribeEvent public static void addEntityAttributes(EntityAttributeCreationEvent e) {
            e.put(EntityTypeRegister.GUARDIAN.get(), GuardianEntity.createGuardAttributes().build());
        }
    }
    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EnhancedEnchanting.MODID,
            bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEventBusSubscriber {
        @SubscribeEvent public static void setupClientStuff(FMLClientSetupEvent e) {
            e.enqueueWork(() -> {
                ClientRegistry.bindTileEntityRenderer(TileRegister.ENCHANTED_TABLE_TILE_TYPE.get(),
                        EnchantedTableTileRenderer::new);
                ClientRegistry.bindTileEntityRenderer(TileRegister.WORD_MACHINE_TILE_TYPE.get(),
                        WordMachineTileRenderer::new);
                ClientRegistry.bindTileEntityRenderer(TileRegister.WORD_FORGE_TILE_TYPE.get(),
                        WordForgeTileRenderer::new);
                ClientRegistry.bindTileEntityRenderer(TileRegister.MATRIX_TILE_TYPE.get(),
                        MatrixTileRenderer::new);
                ClientRegistry.bindTileEntityRenderer(TileRegister.ENCHANTING_STATION_TILE_TYPE.get(),
                        EnchantingStationTileRenderer::new);
                ClientRegistry.bindTileEntityRenderer(TileRegister.THOUGHT_WEAVER_TILE_TYPE.get(),
                        ThoughtLoomTileRenderer::new);
                ClientRegistry.bindTileEntityRenderer(TileRegister.REASSESSMENT_TABLE_TILE_TYPE.get(),
                        ReassessmentTableTileRenderer::new);
                ClientRegistry.bindTileEntityRenderer(TileRegister.REASSESSMENT_PILLAR_TILE_TYPE.get(),
                        ReassessmentPillarTileRenderer::new);
            });
            RenderingRegistry.registerEntityRenderingHandler(EntityTypeRegister.GUARDIAN.get(),
                    GuardianRenderer::new);
        }
        @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EnhancedEnchanting.MODID)
        public static class ClientEventBus {
            @SubscribeEvent public static void onOpenGui(GuiOpenEvent e) {
                if(e.getGui() instanceof EnchantmentScreen && EnhancedEnchantingConfig.DISABLE_VANILLA.get()) {
                    Minecraft.getInstance().player.closeContainer();
                    Minecraft.getInstance().player.sendMessage(
                            new TranslationTextComponent("enchanting_table.message", 0),
                            Minecraft.getInstance().player.getUUID());
                    e.setCanceled(true);
                }
            }
        }

    }
}
