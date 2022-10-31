package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;

import javax.annotation.Nullable;
import java.util.List;

public class ArchiveItem extends CodexItem {
    public ArchiveItem() { super(1); }
    @Override public Rarity getRarity(ItemStack stack) { return Rarity.UNCOMMON; }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                          List<ITextComponent> list, ITooltipFlag flag) {
        list.add(1, new TranslationTextComponent("item.archive.info", 0));
    }
    @Override public void inventoryTick(ItemStack stack, World world, Entity entity,
                                        int slot, boolean flag) {
        if(!world.isClientSide) {
            CompoundNBT tag = stack.getOrCreateTag();
            if(!tag.contains("Revealed")) {
                ListNBT list = new ListNBT();
                EnchantmentsList.T1_LIST.forEach(str ->
                        list.add(StringNBT.valueOf(getGlyphsFor(str.getRegistryName().toString()) +
                                "'"+ str.getRegistryName().toString())));
                EnchantmentsList.T2_LIST.forEach(str ->
                        list.add(StringNBT.valueOf(getGlyphsFor(str.getRegistryName().toString()) +
                                "'"+ str.getRegistryName().toString())));
                EnchantmentsList.T3_LIST.forEach(str ->
                        list.add(StringNBT.valueOf(getGlyphsFor(str.getRegistryName().toString()) +
                                "'"+ str.getRegistryName().toString())));
                tag.put("Revealed", list);
            }
        }
    }
}
