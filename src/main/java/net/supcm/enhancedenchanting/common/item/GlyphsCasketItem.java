package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class GlyphsCasketItem extends Item {
    public GlyphsCasketItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.RARE).stacksTo(8));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                List<ITextComponent> text, ITooltipFlag flag) {
        text.add(1, new TranslationTextComponent("item.glyphs_casket.info"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(!world.isClientSide) {
            if(player.experienceLevel >= 2 || player.isCreative()) {
                Random r = world.getRandom();
                player.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation(EnhancedEnchanting.MODID, EnchantmentsList.SYMBOLS_LIST
                                .get(r.nextInt(EnchantmentsList.SYMBOLS_LIST.size())))),
                        r.nextInt(2)+1));
                if(!player.isCreative())
                    player.getItemInHand(hand).shrink(1);
                world.playSound(null, player.blockPosition(), SoundEvents.ENDER_CHEST_OPEN, SoundCategory.PLAYERS, 1f, 1f);
                return ActionResult.success(player.getItemInHand(hand));
            } else {
                player.displayClientMessage(new TranslationTextComponent("glyphs_casket.notenoughxp"), true);
                return ActionResult.pass(player.getItemInHand(hand));
            }
        }
        return ActionResult.pass(player.getItemInHand(hand));
    }
}
