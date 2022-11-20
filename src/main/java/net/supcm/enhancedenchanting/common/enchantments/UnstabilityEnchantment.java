package net.supcm.enhancedenchanting.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.registries.ForgeRegistries;

public class UnstabilityEnchantment extends Enchantment {
    public UnstabilityEnchantment() { this(EquipmentSlotType.MAINHAND); }
    protected UnstabilityEnchantment(EquipmentSlotType... slots) { super(Rarity.RARE, EnchantmentType.WEAPON, slots); }

    @Override public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem ||
                stack.getItem() instanceof BowItem ||
                stack.getItem() instanceof TridentItem;
    }

    @Override public void doPostAttack(LivingEntity attacker, Entity target, int damage) {
        super.doPostAttack(attacker, target, damage);
        if(!attacker.level.isClientSide){
            switch (attacker.level.getRandom().nextInt(6)) {
                case 0:
                    attacker.moveTo(attacker.blockPosition().getX(),
                            attacker.blockPosition().getY() + attacker.level.getRandom().nextInt(5) + 1,
                            attacker.blockPosition().getZ());
                    break;
                case 1:
                    LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(target.level);
                    bolt.setVisualOnly(true);
                    bolt.moveTo(target.blockPosition(), 0, 0);
                    target.level.addFreshEntity(bolt);
                    target.hurt(DamageSource.LIGHTNING_BOLT, 10f);
                    break;
                case 2:
                    Effect effect = (Effect) ForgeRegistries.POTIONS.getValues().toArray()
                            [attacker.level.getRandom().nextInt(ForgeRegistries.POTIONS.getValues().size())];
                    attacker.addEffect(new EffectInstance(effect, attacker.level.getRandom().nextInt(140),
                            attacker.level.getRandom().nextInt(2)));
                    break;
                default:
                    break;
            }
        }
    }

    @Override public int getMaxLevel() { return 1; }
}
