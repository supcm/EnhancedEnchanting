package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.supcm.enhancedenchanting.EnhancedEnchanting;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;

import java.util.List;

@Mod.EventBusSubscriber(modid = EnhancedEnchanting.MODID)
public class IceSealExaltation extends Exaltation {
    public IceSealExaltation() {
        super("ice_seal", 0.0f, 0.5f, true, false);
    }
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float damage, CompoundNBT tag) {
        if(attacked.isAttackable())
            attacked.addTag("IceSeal");
        super.onAttack(attacker, attacked, damage, tag);
    }
    @SubscribeEvent public static void onEntityTick(LivingEvent.LivingUpdateEvent e) {
        if(!e.getEntity().level.isClientSide && e.getEntity().getTags().contains("IceSeal") &&
            e.getEntity().level.getGameTime() % 30 == 0) {
            e.getEntityLiving().hurt(DamageSource.MAGIC, 2.0f);
            List<Entity> entities = e.getEntity().level.getEntities(null,
                    new AxisAlignedBB(e.getEntity().blockPosition().getX() - 0.5f,
                            e.getEntity().blockPosition().getY() - 0.5f,
                            e.getEntity().blockPosition().getZ() - 0.5f,
                            e.getEntity().blockPosition().getX() + 0.5f,
                            e.getEntity().blockPosition().getY() + 0.5f,
                            e.getEntity().blockPosition().getZ() + 0.5f));
            for (Entity entity : entities) {
                if (entity.isAttackable()) {
                    entity.hurt(DamageSource.MAGIC, 1.0f);
                }
            }
        }
    }
    @SubscribeEvent public static void onEntityDeath(LivingDeathEvent e) {
        if(e.getEntity().level.isClientSide && e.getEntity().getTags().contains("IceSeal")) {
            e.getEntity().removeTag("IceSeal");
            List<Entity> entities = e.getEntity().level.getEntities(null,
                    new AxisAlignedBB(e.getEntity().blockPosition().getX() - 2.5f,
                            e.getEntity().blockPosition().getY() - 2.5f,
                            e.getEntity().blockPosition().getZ() - 2.5f,
                            e.getEntity().blockPosition().getX() + 2.5f,
                            e.getEntity().blockPosition().getY() + 2.5f,
                            e.getEntity().blockPosition().getZ() + 2.5f));
            for (Entity entity : entities) {
                if (entity.isAttackable()) {
                    entity.hurt(DamageSource.MAGIC, 5.0f);
                }
            }
        }
    }
}
