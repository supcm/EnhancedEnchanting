package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nullable;
import java.util.List;

public class IncubatorItem extends Item {
    public IncubatorItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world,
                                          List<ITextComponent> list, ITooltipFlag flag) {
        if(stack.getTag() != null) {
            String source = "Kill";
            if(stack.getTag().contains("Mine"))
                source = "Mine";
            StringTextComponent stored = (StringTextComponent) new StringTextComponent("")
                    .append(new TranslationTextComponent("item.incubator." + source.toLowerCase()))
                    .append(": " + stack.getTag().getInt(source)
                            + "/" + stack.getTag().getInt("Quest"));
            list.add(stored);
        }
    }

    @Override public void inventoryTick(ItemStack stack, World world, Entity entity, int ticks, boolean flag) {
        if(!world.isClientSide) {
            if(stack.getTag() == null) {
                CompoundNBT tag = new CompoundNBT();
                String source = world.random.nextInt(11) >= 5 ? "Kill" : "Mine";
                tag.putInt(source, 0);
                tag.putInt("Quest", 60 + world.random.nextInt(80));
                stack.setTag(tag);
            } else {
                if((stack.getTag().contains("Kill") &&
                        stack.getTag().getInt("Kill") >= stack.getTag().getInt("Quest")) ||
                        (stack.getTag().contains("Mine") &&
                                stack.getTag().getInt("Mine") >= stack.getTag().getInt("Quest"))) {
                    if(entity instanceof PlayerEntity)
                        ((PlayerEntity)entity).inventory.items.replaceAll(s -> {
                            if(s == stack)
                                return new ItemStack(ItemRegister.EXALTATION_STONE.get());
                            return s;
                        });
                }
            }
        }
    }
}
