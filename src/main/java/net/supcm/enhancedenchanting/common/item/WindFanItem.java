package net.supcm.enhancedenchanting.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.supcm.enhancedenchanting.EnhancedEnchanting;

import javax.annotation.Nullable;
import java.util.List;

public class WindFanItem extends Item {
    public WindFanItem() {
        super(new Properties().tab(EnhancedEnchanting.EETAB).stacksTo(1).durability(32)
                .setNoRepair().rarity(Rarity.RARE));
    }
    @Override public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list,
                                          ITooltipFlag flag) {
        list.add(new TranslationTextComponent("item.wind_fan.info"));
    }
    @Override public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(hand == Hand.MAIN_HAND) {
            if (!world.isClientSide) {
                List<Entity> entities = world.getEntities(null, new AxisAlignedBB(
                        player.blockPosition().getX() - 5,
                        player.blockPosition().getY() - 1,
                        player.blockPosition().getZ() - 5,
                        player.blockPosition().getX() + 5,
                        player.blockPosition().getY() + 1,
                        player.blockPosition().getZ() + 5
                ));
                if (!entities.isEmpty()) {
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity && entity != player) {
                            entity.setDeltaMovement(entity.getDeltaMovement().x,
                                    0.985d,
                                    entity.getDeltaMovement().z);
                            ((LivingEntity) entity)
                                    .addEffect(new EffectInstance(Effects.LEVITATION, 70, 0));
                        }
                    }
                    if (!player.isCreative()) {
                        ItemStack stack = player.getMainHandItem();
                        stack.setDamageValue(stack.getDamageValue() + 1);
                    }
                }
            } else {
                world.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                        player.blockPosition().getX(), player.blockPosition().getY()+0.15,
                        player.blockPosition().getZ(),
                        0.0D, 0.025D, 0.0D);
            }
        }
        return ActionResult.success(player.getItemInHand(hand));
    }
}
