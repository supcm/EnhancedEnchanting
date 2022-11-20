package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.init.BlockRegister;

import javax.annotation.Nullable;
import java.util.List;

public class XpCrystalItem extends Item {
    public XpCrystalItem() { super(new Properties().tab(EnhancedEnchanting.EETAB).rarity(Rarity.UNCOMMON)); }
    @Override public boolean isFoil(ItemStack stack) { return true; }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                          List<ITextComponent> text, ITooltipFlag flag) {
        text.add(new TranslationTextComponent("item.crystal.info"));
        text.add(new TranslationTextComponent("item.crystal.use"));
        super.appendHoverText(stack, world, text, flag);
    }

    @Override public ActionResultType useOn(ItemUseContext ctx) {
        Hand hand = ctx.getHand();
        World world = ctx.getLevel();
        if(!world.isClientSide && hand == Hand.MAIN_HAND) {
            if(world.getBlockState(ctx.getClickedPos()).getBlock() == BlockRegister.REASSESSMENT_TABLE.get())
                return ActionResultType.CONSUME;
            PlayerEntity player = ctx.getPlayer();
            player.onEnchantmentPerformed(player.getItemInHand(Hand.MAIN_HAND), -6);
            if(!player.isCreative())
                player.getItemInHand(hand).shrink(1);
            return ActionResultType.CONSUME;
        } else if(world.isClientSide && hand == Hand.MAIN_HAND) {
            PlayerEntity player = ctx.getPlayer();
            world.addParticle(ParticleTypes.SOUL,
                    player.blockPosition().getX(), player.blockPosition().getY()+0.15,
                    player.blockPosition().getZ(),
                    0.0D, 0.025D, 0.0D);
            player.playSound(SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
        }
        return super.useOn(ctx);
    }
}
