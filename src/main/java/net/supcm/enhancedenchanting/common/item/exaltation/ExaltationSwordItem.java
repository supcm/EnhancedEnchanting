package net.supcm.enhancedenchanting.common.item.exaltation;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.exaltation.BoostExaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltations;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nullable;
import java.util.List;

public class ExaltationSwordItem extends SwordItem {
    public ExaltationSwordItem() {
        super(ItemTier.NETHERITE, 3, -2.1f,
                new Properties().tab(EnhancedEnchanting.EETAB));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list,
                                          ITooltipFlag flag) {
        if(stack.getTag() != null) {
            CompoundNBT tag = stack.getTag();
            for (int i = 0; i < 4; i++) {
                String text = i + ": ";
                if(tag.contains("" + i))
                    text += new TranslationTextComponent(
                            stack.getTag().getCompound("" + i).getString("Name")).getString();
                else
                    text += new TranslationTextComponent("exaltation.empty").getString();
                list.add(new StringTextComponent(text).withStyle(TextFormatting.BLUE));

            }
            list.add(new StringTextComponent("CurrentSlot: " + tag.getInt("CurrentExaltation"))
                    .withStyle(TextFormatting.LIGHT_PURPLE));
        }
    }
    @Override public void inventoryTick(ItemStack stack, World world, Entity entity, int ticks, boolean flag) {
        if(!world.isClientSide && entity instanceof LivingEntity) {
            if(stack.getTag() == null || !stack.getTag().contains("CurrentExaltation")) {
                CompoundNBT tag = new CompoundNBT();
                tag.putInt("CurrentExaltation", 0);
            }
        }
    }
    @Override public boolean hurtEnemy(ItemStack stack, LivingEntity attacked, LivingEntity attacker) {
        if(stack.getTag() != null) {
            //for(int i = 0; i< 4; i++){
            Exaltation ex = Exaltation.deserialize(stack.getTag().getCompound("" +
                    stack.getTag().getInt("CurrentExaltation")));
            if (ex != null) {
                for (int j = 0; j < 4; j++) {
                    if(j == stack.getTag().getInt("CurrentExaltation")) continue;
                    Exaltation bex = Exaltation.deserialize(stack.getTag()
                            .getCompound("" + j));
                    if (bex instanceof BoostExaltation)
                        ex.boost(bex);
                }
                ex.onAttack(attacker, attacked, getDamage(), stack.getTag());
            }
            //}
        }
        if(attacker instanceof PlayerEntity)
            return ((PlayerEntity) attacker).isCreative() || super.hurtEnemy(stack, attacked, attacker);
        return super.hurtEnemy(stack, attacker, attacked);
    }
    @Override public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos,
                                       LivingEntity living) {
        if(canHarvestBlock(stack, state) && stack.getTag() != null) {
            //for(int i = 0; i < 4; i++){
            Exaltation ex = Exaltation.deserialize(stack.getTag().getCompound("" +
                    stack.getTag().getInt("CurrentExaltation")));
            if (ex != null) {
                for (int j = 0; j < 4; j++) {
                    if(j == stack.getTag().getInt("CurrentExaltation")) continue;
                    Exaltation bex = Exaltation.deserialize(stack.getTag()
                            .getCompound("" + j));
                    if (bex instanceof BoostExaltation)
                        ex.boost(bex);
                }
                ex.onDig(living, pos, stack.getTag());
            }
            //}
        }
        if(living instanceof PlayerEntity)
            return ((PlayerEntity) living).isCreative() || super.mineBlock(stack, world, state, pos, living);
        return super.mineBlock(stack, world, state, pos, living);
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(!world.isClientSide && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getTag() != null) {
                int currentEx = stack.getTag().getInt("CurrentExaltation");
                for(int i = 0; i < 4; i++) {
                    if(stack.getTag().contains("" + i)) {
                        Exaltation ex = Exaltation.deserialize(stack.getTag().getCompound("" + i));
                        if(ex.isAbility() && currentEx < i) {
                            currentEx = i;
                            break;
                        } else if(i == 3) {
                            for(int j = 0; j < 4; j++){
                                if(stack.getTag().contains("" + j)){
                                    Exaltation nex = Exaltation.deserialize(stack.getTag().getCompound("" + j));
                                    if (nex.isAbility()) {
                                        currentEx = j;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                stack.getTag().putInt("CurrentExaltation", currentEx);
            }
        }
        return super.use(world, player, hand);
    }
}
