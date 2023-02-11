package net.supcm.enhancedenchanting.common.item.exaltation;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;
import net.supcm.enhancedenchanting.common.exaltation.Exaltations;
import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;

public class ExaltationStoneItem extends Item {
    public ExaltationStoneItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list,
                                          ITooltipFlag flag) {
        if(stack.getTag() != null && stack.getTag().contains("Exaltation")) {
            list.add(new TranslationTextComponent(
                    stack.getTag().getCompound("Exaltation").getString("Name") + ".desc")
                    .withStyle(TextFormatting.BLUE));
            list.add(new TranslationTextComponent("exaltation.range")
                    .append(": " + stack.getTag().getCompound("Exaltation")
                    .getFloat("Range")).withStyle(TextFormatting.BLUE));
            list.add(new TranslationTextComponent("exaltation.damage")
                    .append(": " + stack.getTag().getCompound("Exaltation")
                            .getFloat("Damage")).withStyle(TextFormatting.BLUE));
        }
        super.appendHoverText(stack, world, list, flag);
    }

    @Override public ITextComponent getName(ItemStack stack) {
        if(stack.getTag() != null && stack.getTag().contains("Exaltation"))
            return new TranslationTextComponent(stack.getTag().getCompound("Exaltation")
                    .getString("Name"));
        return super.getName(stack);
    }

    @Override public void inventoryTick(ItemStack stack, World world, Entity entity,
                                        int tick, boolean flag) {
        if(!world.isClientSide && entity instanceof PlayerEntity) {
            if(stack.getTag() == null)
                generateExaltation(world, stack);
        }
    }
    private void generateExaltation(World world, ItemStack stack) {
        CompoundNBT tag = new CompoundNBT();
        float randDamage = Float.parseFloat(new DecimalFormat("#.##").
                format(world.getRandom().nextFloat()).replace(',', '.'));
        float randRange = Float.parseFloat(new DecimalFormat("#.##").
                format(world.getRandom().nextFloat()).replace(',', '.'));
        Exaltation ex = Exaltations.createRandomExaltation(world.getRandom());
        ex.setDamage(ex.getBaseDamage() + (ex.haveRand() ? randDamage : 0));
        ex.setRange(ex.getBaseRange() + (ex.haveRand() ? randDamage : 0));
        tag.putBoolean("IsAbility", ex.isAbility());
        tag.put("Exaltation", ex.serialize());
        stack.setTag(tag);
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(world.isClientSide && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getTag() != null)
                generateExaltation(world, stack);
        }
        return super.use(world, player, hand);
    }
}
