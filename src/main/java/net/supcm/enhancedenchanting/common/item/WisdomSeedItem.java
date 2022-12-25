package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;

import javax.annotation.Nullable;
import java.util.List;

public class WisdomSeedItem extends Item {
    public WisdomSeedItem() {
        super(new Item.Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1).rarity(Rarity.UNCOMMON));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                          List<ITextComponent> list, ITooltipFlag flag) {
        list.add(new TranslationTextComponent("item.wisdom_seed.info").withStyle(TextFormatting.BLUE));
        if(stack.getTag() != null && !stack.getTag().isEmpty() && stack.getTag().contains("Mode")) {
            TranslationTextComponent text = new TranslationTextComponent("item.wisdom_seed.giving_mode");
            if(stack.getTag().getBoolean("Mode"))
                text = new TranslationTextComponent("item.wisdom_seed.taking_mode");
            list.add(text);
            StringTextComponent stored = (StringTextComponent)new StringTextComponent("")
                    .append(new TranslationTextComponent("item.wisdom_seed.stored"))
                    .append(": " + stack.getTag().getInt("Stored"));
            list.add(stored);
        }
    }
    @Override public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if(!entity.level.isClientSide) {
            stack.getTag().putBoolean("Mode", !stack.getTag().getBoolean("Mode"));
            if(entity instanceof PlayerEntity){
                TranslationTextComponent text = new TranslationTextComponent("item.wisdom_seed.giving_mode");
                if (stack.getTag().getBoolean("Mode"))
                    text = new TranslationTextComponent("item.wisdom_seed.taking_mode");
                ((PlayerEntity) entity).displayClientMessage(text, true);
            }
        }
        return super.onEntitySwing(stack, entity);
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(!world.isClientSide && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getTag().getBoolean("Mode")) { // Taking mode
                if(player.experienceLevel > 0) {
                    int store = 1;
                    if (player.isShiftKeyDown())
                        store = player.experienceLevel;
                    stack.getTag().putInt("Stored", stack.getTag().getInt("Stored") + store);
                    player.onEnchantmentPerformed(stack, store);
                    ActionResult.success(stack);
                } else {
                    player.displayClientMessage(new TranslationTextComponent("item.wisdom_seed.not_enough_player"), true);
                    ActionResult.fail(stack);
                }
            } else { // Giving Mode
                if(stack.getTag().getInt("Stored") > 0) {
                    int stored = 1;
                    if (player.isShiftKeyDown() && stack.getTag().getInt("Stored") > 1)
                        stored = stack.getTag().getInt("Stored");
                    stack.getTag().putInt("Stored", stack.getTag().getInt("Stored") - stored);
                    player.onEnchantmentPerformed(stack, -stored);
                    ActionResult.success(stack);
                } else {
                    player.displayClientMessage(new TranslationTextComponent("item.wisdom_seed.not_enough_seed"), true);
                    ActionResult.fail(stack);
                }
            }
        }
        return ActionResult.pass(player.getItemInHand(hand));
    }
    @Override public void inventoryTick(ItemStack stack, World world, Entity entity, int integer, boolean flag) {
        if(stack.getTag() == null || stack.getTag().isEmpty() || !stack.getTag().contains("Mode")) {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean("Mode", true);
            tag.putInt("Stored", 0);
            stack.setTag(tag);
        }
    }
}
