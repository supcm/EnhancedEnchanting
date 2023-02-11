package net.supcm.enhancedenchanting.common.exaltation;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public abstract class Exaltation {
    private final String name;
    private final boolean isAbility;
    private final boolean haveRand;
    private float damage;
    private float range;
    protected final float baseDamage;
    protected final float baseRange;
    public Exaltation(String name, float baseDamage, float baseRange, boolean isAbility, boolean haveRand) {
        this.name = "exaltation." + name;
        this.isAbility = isAbility;
        this.baseDamage = baseDamage;
        this.baseRange = baseRange;
        damage = baseDamage;
        range = baseRange;
        this.haveRand = haveRand;
    }
    public Exaltation(String name, float baseDamage, float baseRange, boolean isAbility) {
        this(name, baseDamage, baseRange, isAbility, true);
    }
    public boolean isAbility() { return isAbility; }
    public boolean haveRand() { return haveRand; }
    public String getName() { return name; }
    public float getDamage() { return damage; }
    public float getRange() { return range; }
    public float getBaseDamage() { return baseDamage; }
    public float getBaseRange() { return baseRange; }
    public void setDamage(float damage) { this.damage = damage; }
    public void setRange(float range) { this.range = range; }
    public void onAttack(LivingEntity attacker, Entity attacked, float damage, CompoundNBT tag) {
        boostAttack(attacker, attacked, damage, tag);
    }
    public void onDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {
        boostDig(digger, pos, tag);
    }
    public void onUse(PlayerEntity player, ItemUseContext ctx, CompoundNBT tag) { boostUse(player, ctx, tag); }
    public void boost(Exaltation ex) {
        if(!ex.isAbility() && this.isAbility()) {
            BoostExaltation bex = (BoostExaltation) ex;
            bex.boostExaltation(this);
        }
    }
    public void boostAttack(LivingEntity attacker, Entity attacked, float attack, CompoundNBT tag) {
        if(tag != null) {
            List<BoostExaltation> bexs = Lists.newArrayList();
            for(int i = 1; i < 4; i++)
                if (tag.contains("" + i) &&
                        deserialize(tag.getCompound("" + i)) instanceof BoostExaltation)
                    bexs.add((BoostExaltation)deserialize(tag.getCompound("" + i)));
            for(BoostExaltation bex : bexs)
                bex.onAttack(attacker, attacked, attack, tag);
        }
    }
    public void boostDig(LivingEntity digger, BlockPos pos, CompoundNBT tag) {
        if(tag != null) {
            List<BoostExaltation> bexs = Lists.newArrayList();
            for(int i = 1; i < 4; i++)
                if(tag.contains("" + i) &&
                        deserialize(tag.getCompound("" + i)) instanceof BoostExaltation)
                    bexs.add((BoostExaltation)deserialize(tag.getCompound("" + i)));
            for(BoostExaltation bex : bexs)
                bex.onDig(digger, pos, tag);
        }
    }
    public void boostUse(PlayerEntity player, ItemUseContext ctx, CompoundNBT tag) {
        if(tag != null) {
            List<BoostExaltation> bexs = Lists.newArrayList();
            for(int i = 1; i < 4; i++)
                if(tag.contains("" + i) &&
                        deserialize(tag.getCompound("" + i)) instanceof BoostExaltation)
                    bexs.add((BoostExaltation)deserialize(tag.getCompound("" + i)));
            for(BoostExaltation bex : bexs)
                bex.onUse(player, ctx, tag);
        }
    }
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("Name", name);
        tag.putFloat("Range", getRange());
        tag.putFloat("Damage", getDamage());
        return tag;
    }
    public static Exaltation deserialize(CompoundNBT tag) {
        if(tag != null) {
            Exaltation ex = Exaltations.getExaltationByName(tag.getString("Name"));
            if(ex == null) return null;
            ex.setDamage(tag.getFloat("Damage"));
            ex.setRange(tag.getFloat("Range"));
            return ex;
        }
        return null;
    }
}
