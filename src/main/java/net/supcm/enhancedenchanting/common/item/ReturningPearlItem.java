package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;

import javax.annotation.Nullable;
import java.util.List;

public class ReturningPearlItem extends Item {
    public ReturningPearlItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1).rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                          List<ITextComponent> text, ITooltipFlag flag) {
        text.add(new TranslationTextComponent("item.returning_pearl.info"));
        text.add(new TranslationTextComponent("item.returning_pearl.info1"));
        super.appendHoverText(stack, world, text, flag);
    }
    @Override public boolean isFoil(ItemStack stack) {
        if(stack.getTag() != null && stack.getTag().contains("x"))
            return true;
        return false;
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(!world.isClientSide && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if(!player.isCrouching()) {
                if (stack.getTag() != null) {
                    if (stack.getTag().getString("dim").equals(world.dimension().location().toString())) {
                        player.fallDistance = 0.0f;
                        player.teleportTo(stack.getTag().getInt("x"), stack.getTag().getInt("y"),
                                stack.getTag().getInt("z"));
                        if (!player.isCreative())
                            stack.shrink(1);
                        world.playSound(null, player.blockPosition(),
                                SoundEvents.GLASS_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        return ActionResult.success(player.getItemInHand(hand));
                    } else
                        player.displayClientMessage(
                                new TranslationTextComponent("item.returning_pearl.not_the_same_dim"),
                                true);
                } else
                    player.displayClientMessage(new TranslationTextComponent("item.returning_pearl.no_pos"),
                            true);
            } else {
                if(stack.getTag() == null) {
                    CompoundNBT tag = new CompoundNBT();
                    tag.putInt("x", player.blockPosition().getX());
                    tag.putInt("y", player.blockPosition().getY());
                    tag.putInt("z", player.blockPosition().getZ());
                    tag.putString("dim", world.dimension().location().toString());
                    stack.setTag(tag);
                } else {
                    stack.getTag().putInt("x", player.blockPosition().getX());
                    stack.getTag().putInt("y", player.blockPosition().getY());
                    stack.getTag().putInt("z", player.blockPosition().getZ());
                    stack.getTag().putString("dim",
                            world.dimension().location().toString());
                    player.displayClientMessage(new TranslationTextComponent("item.returning_pearl.changed_pos"),
                            true);
                }
                return ActionResult.success(player.getItemInHand(hand));
            }
        }
        return ActionResult.pass(player.getItemInHand(hand));
    }

    @Override public ActionResultType useOn(ItemUseContext ctx) {
        if(!ctx.getLevel().isClientSide && ctx.getHand() == Hand.MAIN_HAND && ctx.getPlayer().isCrouching()) {
            if(ctx.getItemInHand().getTag() == null) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("x", ctx.getPlayer().blockPosition().getX());
                tag.putInt("y", ctx.getPlayer().blockPosition().getY());
                tag.putInt("z", ctx.getPlayer().blockPosition().getZ());
                tag.putString("dim", ctx.getLevel().dimension().location().toString());
                ctx.getItemInHand().setTag(tag);
            } else {
                ctx.getItemInHand().getTag().putInt("x", ctx.getPlayer().blockPosition().getX());
                ctx.getItemInHand().getTag().putInt("y", ctx.getPlayer().blockPosition().getY());
                ctx.getItemInHand().getTag().putInt("z", ctx.getPlayer().blockPosition().getZ());
                ctx.getItemInHand().getTag().putString("dim",
                        ctx.getLevel().dimension().location().toString());
                ctx.getPlayer().displayClientMessage(new TranslationTextComponent("item.returning_pearl.changed_pos"),
                        true);
            }
            return ActionResultType.SUCCESS;
        }
        return super.useOn(ctx);
    }
}