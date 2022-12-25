package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import javax.annotation.Nullable;
import java.util.List;

public class CatsEyeItem extends Item {
    public CatsEyeItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1).durability(240)
                .defaultDurability(240).rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                          List<ITextComponent> text, ITooltipFlag flag) {
        text.add(new TranslationTextComponent("item.cats_eye.info"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public void inventoryTick(ItemStack stack, World world, Entity entity,
                                        int tick, boolean flag) {
        if (!world.isClientSide) {
            if (stack.getTag() == null) {
                CompoundNBT tag = new CompoundNBT();
                tag.putBoolean("Activated", false);
                stack.setTag(tag);
            }
            if(stack.getTag().getBoolean("Activated")) {
                if(world.getGameTime() % 20 == 0) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) entity;
                        living.addEffect(new EffectInstance(Effects.NIGHT_VISION, 220, 0));
                        stack.hurtAndBreak(1, living, e -> e.broadcastBreakEvent(living.swingingArm));
                    }
                }
            }
        }
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(!world.isClientSide) {
            if(hand == Hand.MAIN_HAND && player.getItemInHand(hand).getItem() == this)
                changeActivity(player.getItemInHand(hand));
            else if(hand == Hand.OFF_HAND && player.getItemInHand(hand).getItem() == this)
                changeActivity(player.getItemInHand(hand));
        }
        return ActionResult.pass(player.getItemInHand(hand));
    }
    private ActionResult<ItemStack> changeActivity(ItemStack stack) {
        if(stack.getTag() != null) {
            stack.getTag().putBoolean("Activated", !stack.getTag().getBoolean("Activated"));
            return ActionResult.success(stack);
        }
        return ActionResult.pass(stack);
    }
}