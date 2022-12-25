package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;
import net.supcm.enhancedenchanting.common.init.EnchantmentRegister;
import net.supcm.enhancedenchanting.common.network.PacketHandler;
import net.supcm.enhancedenchanting.common.network.packets.CodexScreenPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CodexItem extends Item {
    private final int type;
    public CodexItem() {
        this(0);
    }
    protected CodexItem(int type) {
        super(new Item.Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1).rarity(Rarity.EPIC));
        this.type = type;
    }
    @Override public boolean isFoil(ItemStack stack) {return true;}
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                List<ITextComponent> list, ITooltipFlag flag) {
        list.add(1, new TranslationTextComponent("item.codex.info1", 0));
        list.add(2, new TranslationTextComponent("item.codex.info2", 0));
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
                list.add(StringNBT.valueOf(getGlyphsFor(
                        EnchantmentRegister.UNSTABILITY.get().getRegistryName().toString()) +
                        "'" + EnchantmentRegister.UNSTABILITY.get().getRegistryName().toString()));
                tag.put("Revealed", list);
            }
        }
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(!world.isClientSide) {
            CompoundNBT tag = player.getItemInHand(hand).getOrCreateTag();
            PacketHandler.CHANNEL.sendTo(new CodexScreenPacket(tag),
                    ((ServerPlayerEntity) player).connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT);
        }
        return ActionResult.pass(player.getItemInHand(hand));
    }
    public static String getGlyphsFor(String resourceLocation) {
        for (Map.Entry<String, Enchantment> entry : EnchantmentsList.T1_MAP.entrySet())
            if(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(resourceLocation))
                    .equals(entry.getValue()))
                return entry.getKey();
        for (Map.Entry<String, Enchantment> entry : EnchantmentsList.T2_MAP.entrySet())
            if(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(resourceLocation))
                    .equals(entry.getValue()))
                return entry.getKey();
        for (Map.Entry<String, Enchantment> entry : EnchantmentsList.T3_MAP.entrySet())
            if(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(resourceLocation))
                    .equals(entry.getValue()))
                return entry.getKey();
        return "exception";
    }
}
