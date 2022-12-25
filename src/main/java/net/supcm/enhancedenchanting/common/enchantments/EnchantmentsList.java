package net.supcm.enhancedenchanting.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchantingConfig;
import net.supcm.enhancedenchanting.common.init.EnchantmentRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentsList {
    public EnchantmentsList() {
        EnchantmentsList.initAllLists();
    }
    public static void initAllLists() {
        EnchantmentsList.initSymbolsList();
        EnchantmentsList.initFirstList();
        EnchantmentsList.initSecondList();
        EnchantmentsList.initThirdList();
        for(String rl : EnhancedEnchantingConfig.T2_LIST.get()) {
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(rl));
            if(ench != null) T2_LIST.add(ench);
            else System.out.println("Can't add enchantment with ResourceLocation " + rl);
        }
        for(int i = 0; i < ForgeRegistries.ENCHANTMENTS.getValues().toArray().length; i++) {
            boolean contains = T1_LIST.contains(ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]) ||
                    T2_LIST.contains(ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]) ||
                    T3_LIST.contains(ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]);
            if(!contains)
                T3_LIST.add((Enchantment)ForgeRegistries.ENCHANTMENTS.getValues().toArray()[i]);
        }
    }
    public static final List<String> SYMBOLS_LIST = new ArrayList<>();
    public static List<Enchantment> T1_LIST = new ArrayList<>();
    public static List<Enchantment> T2_LIST = new ArrayList<>();
    public static List<Enchantment> T3_LIST = new ArrayList<>();
    public static Map<String, Enchantment> T1_MAP = new HashMap<String, Enchantment>();
    public static Map<String, Enchantment> T2_MAP = new HashMap<String, Enchantment>();
    public static Map<String, Enchantment> T3_MAP = new HashMap<String, Enchantment>();
    public static void initSymbolsList() {
        SYMBOLS_LIST.add("ara");
        SYMBOLS_LIST.add("geo");
        SYMBOLS_LIST.add("oku");
        SYMBOLS_LIST.add("yue");
        SYMBOLS_LIST.add("qou");
        SYMBOLS_LIST.add("ria");
        SYMBOLS_LIST.add("lua");
        SYMBOLS_LIST.add("dor");
        SYMBOLS_LIST.add("zet");

    }
    public static void initFirstList() {
        T1_LIST.add(Enchantments.RESPIRATION);
        T1_LIST.add(Enchantments.SMITE);
        T1_LIST.add(Enchantments.BANE_OF_ARTHROPODS);
        T1_LIST.add(Enchantments.KNOCKBACK);
        T1_LIST.add(Enchantments.PUNCH_ARROWS);
        T1_LIST.add(Enchantments.CHANNELING);
        T1_LIST.add(Enchantments.PIERCING);
        T1_LIST.add(Enchantments.THORNS);
        T1_LIST.add(Enchantments.UNBREAKING);
    }

    public static void initSecondList() {
        T2_LIST.add(Enchantments.MOB_LOOTING);
        T2_LIST.add(Enchantments.BLOCK_FORTUNE);
        T2_LIST.add(Enchantments.FLAMING_ARROWS);
        T2_LIST.add(Enchantments.FIRE_ASPECT);
        T2_LIST.add(Enchantments.SWEEPING_EDGE);
        T2_LIST.add(Enchantments.IMPALING);
        T2_LIST.add(Enchantments.LOYALTY);
        T2_LIST.add(Enchantments.QUICK_CHARGE);
        T2_LIST.add(Enchantments.SOUL_SPEED);
        T2_LIST.add(Enchantments.FROST_WALKER);
        T2_LIST.add(Enchantments.AQUA_AFFINITY);
        T2_LIST.add(Enchantments.FALL_PROTECTION);
        T2_LIST.add(Enchantments.FIRE_PROTECTION);
        T2_LIST.add(Enchantments.BLAST_PROTECTION);
        T2_LIST.add(Enchantments.PROJECTILE_PROTECTION);
        T2_LIST.add(Enchantments.FISHING_LUCK);
        T2_LIST.add(Enchantments.FISHING_SPEED);
        T2_LIST.add(EnchantmentRegister.XP_BOOST.get());
        T2_LIST.add(EnchantmentRegister.UNSTABILITY.get());
    }

    public static void initThirdList() {
        T3_LIST.add(Enchantments.ALL_DAMAGE_PROTECTION);
        T3_LIST.add(Enchantments.MENDING);
        T3_LIST.add(Enchantments.INFINITY_ARROWS);
        T3_LIST.add(Enchantments.POWER_ARROWS);
        T3_LIST.add(Enchantments.SHARPNESS);
        T3_LIST.add(Enchantments.MULTISHOT);
        T3_LIST.add(Enchantments.DEPTH_STRIDER);
        T3_LIST.add(Enchantments.BLOCK_EFFICIENCY);
        T3_LIST.add(Enchantments.SILK_TOUCH);
        T3_LIST.add(Enchantments.RIPTIDE);
    }
}
