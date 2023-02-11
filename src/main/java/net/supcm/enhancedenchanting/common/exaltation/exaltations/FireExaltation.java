package net.supcm.enhancedenchanting.common.exaltation.exaltations;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;
import java.util.List;

public class FireExaltation extends Exaltation {
    public FireExaltation() {
        super("fire", 1.0f, 0, true);
    }
    @Override public void onAttack(LivingEntity attacker, Entity attacked, float attack, CompoundNBT tag ) {
        attacked.hurt(DamageSource.IN_FIRE, getDamage());
        attacked.setSecondsOnFire((int)(getDamage() * 2));
        if(getRange() >= 0.15f) {
            List<Entity> entities = attacked.level.getEntities(null,
                    new AxisAlignedBB(attacked.blockPosition().getX() - getRange(),
                            attacked.blockPosition().getY() - getRange(),
                            attacked.blockPosition().getZ() - getRange(),
                            attacked.blockPosition().getX() + getRange(),
                            attacked.blockPosition().getY() + getRange(),
                            attacked.blockPosition().getZ() + getRange()));
            for(Entity entity : entities) {
                if(entity.isAttackable() && attacker != entity) {
                    entity.setSecondsOnFire((int) getDamage());
                    entity.hurt(DamageSource.IN_FIRE, getDamage() / 2);
                    super.onAttack(attacker, attacked, attack, tag);
                }
            }

        }
        super.onAttack(attacker, attacked, attack, tag);
    }
    @Override public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) { super.onDig(digger, pos, tag); }
    public static class SmeltModifier extends LootModifier {
        public SmeltModifier(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }
        @Override protected List<ItemStack> doApply(List<ItemStack> loot, LootContext ctx) {
            ItemStack tool = ctx.getParamOrNull(LootParameters.TOOL);
            boolean hasTag = tool != null && !tool.isEmpty() && hasFire(tool);
            if(hasTag) {
                List<ItemStack> smelted = Lists.newArrayList();
                loot.forEach(stack -> smelted.add(
                        ctx.getLevel().getRecipeManager()
                                .getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), ctx.getLevel())
                                .map(FurnaceRecipe::getResultItem)
                                .filter(item -> !item.isEmpty())
                                .map(item -> ItemHandlerHelper.copyStackWithSize(item,
                                        stack.getCount() * item.getCount()))
                                .orElse(stack)));
                return smelted;
            }
            return loot;
        }
        boolean hasFire(ItemStack stack) {
            CompoundNBT tag = stack.getTag();
            if(tag != null){
                for (int i = 0; i < 4; i++) {
                    Exaltation ex = Exaltation.deserialize(tag.getCompound("" + i));
                    if (ex instanceof FireExaltation)
                        return true;
                }
            }
            return false;
        }
        public static class Serializer extends GlobalLootModifierSerializer<SmeltModifier> {
            @Override public SmeltModifier read(ResourceLocation location, JsonObject object,
                                                ILootCondition[] ailootcondition) {
                return new SmeltModifier(ailootcondition);
            }
            @Override public JsonObject write(SmeltModifier instance) {
                return null;
            }
        }
    }
}
