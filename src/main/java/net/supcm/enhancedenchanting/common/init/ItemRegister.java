package net.supcm.enhancedenchanting.common.init;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.item.*;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRegister {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            EnhancedEnchanting.MODID);

    public static Rarity FORBIDDEN = Rarity.create("Forbidden", TextFormatting.DARK_RED);
    public static Rarity CONCEPTION = Rarity.create("Conception", TextFormatting.GOLD);

    public static final RegistryObject<Item> STAR = ITEMS.register("star", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ENCHANTED_STAR = ITEMS.register("enchanted_star", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.EPIC)) {
                @Override public boolean isFoil(ItemStack stack) {
                    return true;
                }
            });
    public static final RegistryObject<Item> CRYSTAL_EMPTY = ITEMS.register("xp_crystal_empty",
            () -> new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB)));
    public static final RegistryObject<Item> CORE = ITEMS.register("core", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ENCHANTED_CORE = ITEMS.register("enchanted_core", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.EPIC)) {
                @Override public boolean isFoil(ItemStack stack) { return true; }
            });
    public static final RegistryObject<Item> ENCHANTED_HEART = ITEMS.register("enchanted_heart", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.EPIC)) {
                @Override public boolean isFoil(ItemStack stack) { return true; }
            });
    public static final RegistryObject<Item> STABILIZER = ITEMS.register("stabilizer", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ENHANCED_STABILIZER = ITEMS.register("enhanced_stabilizer", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> OVERLOADING_MECH = ITEMS.register("overloading_mech", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> LAVA_CRYSTAL = ITEMS.register("lava_crystal", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB)));
    public static final RegistryObject<Item> CLAY_PLATE = ITEMS.register("clay_plate", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB)));
    public static final RegistryObject<Item> PLATE = ITEMS.register("plate", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB)));
    public static final RegistryObject<Item> CRYSTAL = ITEMS.register("xp_crystal", XpCrystalItem::new);
    public static final RegistryObject<Item> CASKET = ITEMS.register("glyphs_casket", GlyphsCasketItem::new);
    public static final RegistryObject<Item> CODEX = ITEMS.register("codex", CodexItem::new);
    public static final RegistryObject<Item> ARCHIVE = ITEMS.register("archive", ArchiveItem::new);
    public static final RegistryObject<Item> CONCEPTION_BASE = ITEMS.register("conception_base", () ->
            new Item(new Item.Properties().tab(EnhancedEnchanting.EETAB)));
    public static final RegistryObject<Item> CONCEPTION_SOUL = createConception("soul", new float[]{0.4f, 0.4f, 1f});
    public static final RegistryObject<Item> CONCEPTION_BEAUTY = createConception("beauty", new float[]{1f, 0.5f, 0.5f});
    public static final RegistryObject<Item> CONCEPTION_ART = createConception("art", new float[]{0.6f, 0.6f, 0.6f});
    public static final RegistryObject<Item> CONCEPTION_CREATION = createConception("creation", new float[]{0.6f, 1f, 0.6f});
    public static final RegistryObject<Item> CONCEPTION_TRUTH = createConception("truth", new float[]{0.9f, 0.9f, 0.9f});
    public static final RegistryObject<Item> CONCEPTION_LIES = createConception("lies", new float[]{1f, 0.3f, 0.3f});
    public static final RegistryObject<Item> OKU = createSymbol("oku");
    public static final RegistryObject<Item> GEO = createSymbol("geo");
    public static final RegistryObject<Item> ARA = createSymbol("ara");
    public static final RegistryObject<Item> YUE = createSymbol("yue");
    public static final RegistryObject<Item> QOU = createSymbol("qou");
    public static final RegistryObject<Item> RIA = createSymbol("ria");
    public static final RegistryObject<Item> LUA = createSymbol("lua");
    public static final RegistryObject<Item> DOR = createSymbol("dor");
    public static final RegistryObject<Item> ZET = createSymbol("zet");
    public static final RegistryObject<Item> IRO = createUnstableSymbol("iro");
    public static final RegistryObject<Item> NOY = createUnstableSymbol("noy");
    public static final RegistryObject<Item> SAT = createUnstableSymbol("sat");
    public static final RegistryObject<Item> BAL = createUnstableSymbol("bal");
    public static final RegistryObject<Item> WOY = createUnstableSymbol("woy");
    public static final RegistryObject<Item> VER = createUnstableSymbol("ver");
    public static final RegistryObject<Item> FIR = ITEMS.register("fir", () ->
            new Item(new Item.Properties().rarity(FORBIDDEN).fireResistant().tab(EnhancedEnchanting.SYMBOLSTAB)
                    .stacksTo(1)){
                @Override public boolean isFoil(ItemStack stack) { return true; }

                @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                                      List<ITextComponent> list, ITooltipFlag flag) {
                    list.add(new TranslationTextComponent("item.fir.info").withStyle(TextFormatting.GRAY));
                }
            });

    private static RegistryObject<Item> createSymbol(String name) {
            return ITEMS.register(name, GlyphItem::new);
    }
    private static RegistryObject<Item> createUnstableSymbol(String name) { return ITEMS.register(name, UnstableGlyphItem::new); }
    private static RegistryObject<Item> createConception(String name, float[] color) { return ITEMS.register("conception_"+name, () -> new ConceptionItem(color)); }

    public static void reg(IEventBus bus) { ITEMS.register(bus); }
    public static class UnstableGlyphItem extends Item {
        public UnstableGlyphItem() {
            super(new Properties().tab(EnhancedEnchanting.SYMBOLSTAB).stacksTo(8).rarity(Rarity.RARE));
        }
        @Override public boolean hasContainerItem(ItemStack stack) { return true; }
        @Override public ItemStack getContainerItem(ItemStack itemStack) { return itemStack.copy(); }
    }
    public static class ConceptionItem extends Item {
        float[] color;
        public ConceptionItem(float[] color) {
            super(new Properties().tab(EnhancedEnchanting.EETAB).rarity(CONCEPTION).stacksTo(16));
            setColor(color);
        }
        @Override public boolean isFoil(ItemStack stack) { return true; }
        public float[] getColor() { return color; }
        void setColor(float[] color) { this.color = color;}
    }
    public static class GlyphItem extends Item {
        public GlyphItem() { super(new Properties().tab(EnhancedEnchanting.SYMBOLSTAB).stacksTo(16)); }
        @Override public boolean isFoil(ItemStack stack) { return true; }
        @Override public boolean hasContainerItem(ItemStack stack) { return true; }
        @Override public ItemStack getContainerItem(ItemStack itemStack) { return itemStack.copy(); }
    }
}
