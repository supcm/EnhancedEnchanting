package net.supcm.enhancedenchanting.common.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.supcm.enhancedenchanting.common.block.entity.WordForgeTile;
import net.supcm.enhancedenchanting.common.init.EntityTypeRegister;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class GuardianEntity extends FlyingEntity implements IRangedAttackMob {
    protected static final DataParameter<Integer> DATA_FLAG = EntityDataManager.defineId(GuardianEntity.class, DataSerializers.INT);
    protected static final DataParameter<Boolean> RECHARGING_FLAG = EntityDataManager.defineId(GuardianEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> ATTACK_FLAG = EntityDataManager.defineId(GuardianEntity.class, DataSerializers.BOOLEAN);
    private int xpLevel;
    private final ServerBossInfo bossEvent =
            (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(),
                    BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS)).setDarkenScreen(false);
    public GuardianEntity(EntityType<GuardianEntity> type, World level) {
        super(type, level);
        xpLevel = 30;
        moveControl = new MoveHelperController(this);
        setPathfindingMalus(PathNodeType.WATER, -1.0f);
        setPathfindingMalus(PathNodeType.LAVA, -1.0f);
        setAggressive(true);
    }
    public GuardianEntity(WordForgeTile tile) {
        this(EntityTypeRegister.GUARDIAN.get(), tile.getLevel());
        this.xpLevel = tile.enchLevel;
        setPos(tile.getBlockPos().getX(),
                tile.getBlockPos().getY() + 5,
                    tile.getBlockPos().getZ());

    }
    public static AttributeModifierMap.MutableAttribute createGuardAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FLYING_SPEED, 0.16)
                .add(Attributes.MAX_HEALTH, 750).add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5).add(Attributes.ATTACK_SPEED, 1.75)
                .add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.ARMOR, 2.5)
                .add(Attributes.KNOCKBACK_RESISTANCE, -0.15);
    }
    @Override protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_FLAG, 0);
        entityData.define(RECHARGING_FLAG, false);
        entityData.define(ATTACK_FLAG, false);
    }
    @Override protected float getStandingEyeHeight(Pose pose, EntitySize size) { return size.height/2; }
    @Override protected int getExperienceReward(PlayerEntity player) { return (int)(0.5*Math.pow(xpLevel, 2)); }
    @Override protected void registerGoals() {
        goalSelector.addGoal(5, new RandomFlyGoal(this));
        //goalSelector.addGoal(4, new LookRandomlyGoal(this));
        goalSelector.addGoal(3, new RangedAttackGoal(this, 1.5, 40, 8));
        goalSelector.addGoal(2, new LightningAttack(this));
        goalSelector.addGoal(1, new WindAttack(this));
        targetSelector.addGoal(0,
                new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 20,
                        true, false, null));
    }
    @Override public boolean hurt(DamageSource source, float damage) {
        if(getState() == 2) return false;
        if(getState() == 1 && source == DamageSource.LIGHTNING_BOLT) return false;
        if(source.getDirectEntity() instanceof AbstractFireballEntity) return super.hurt(source, 38f);
        if(source.getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity)source.getEntity();
            if(attacker.getItemInHand(Hand.MAIN_HAND).isEnchanted()){
                if(level.isClientSide)
                    if(attacker instanceof PlayerEntity)
                        ((PlayerEntity)attacker).displayClientMessage(new TranslationTextComponent("entity.enhancedenchanting.guard.hurt_cancel", 0),
                            true);
                return false;
            }
        }
        return super.hurt(source, damage);
    }

    @Nullable @Override protected SoundEvent getDeathSound() { return SoundEvents.BLAZE_DEATH; }
    @Nullable @Override protected SoundEvent getAmbientSound() { return SoundEvents.BLAZE_AMBIENT; }
    @Override protected boolean shouldDespawnInPeaceful() { return false; }
    @Override protected PathNavigator createNavigation(World world) {
        return new FlyingPathNavigator(this, world);
    }
    @Override public void performRangedAttack(LivingEntity livingentity, float p_82196_2_) {
        if((isRecharging() || getHealth() > 0.65) && !isAttacking() && getState() == 0){
            setState(0);
            setAttacking(true);
            Vector3d vector3d = livingentity.getDeltaMovement();
            double d0 = livingentity.getX() + vector3d.x - this.getX();
            double d1 = livingentity.getY() + 0.35d - this.getY();
            double d2 = livingentity.getZ() + vector3d.z - this.getZ();
            FireballEntity fireballEntity = new FireballEntity(level, this, d0, d1, d2);
            fireballEntity.explosionPower = 0;
            fireballEntity.setPos(getX(),
                    getY() + 1.5D,
                    fireballEntity.getZ());
            level.addFreshEntity(fireballEntity);
            level.playSound(null, blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.0f, 1.0f);
            setAttacking(false);
        }
    }
    @Override public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        if (hasCustomName())
            bossEvent.setName(getDisplayName());
    }
    @Override public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        bossEvent.setName(getDisplayName());
    }
    @Override public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }
    @Override public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }
    void setRecharging(boolean isRecharging) { entityData.set(RECHARGING_FLAG, isRecharging); }
    boolean isRecharging() { return entityData.get(RECHARGING_FLAG); }
    void setAttacking(boolean isAttacking) { entityData.set(ATTACK_FLAG, isAttacking); }
    boolean isAttacking() { return entityData.get(ATTACK_FLAG); }
    public int getState() {
        return entityData.get(DATA_FLAG);
    }
    public void setState(int i) {
        entityData.set(DATA_FLAG, i);
    }
    int i = 0;
    @Override protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setPercent(getHealth() / getMaxHealth());
        if(isRecharging()) {
            if(getState() != 0) setState(0);
            ++i;
            if(i >= 150){
                setRecharging(false);
                i = 0;
            }
        }
    }
    static class MoveHelperController extends MovementController {
        private final GuardianEntity entity;
        private int floatDuration;

        public MoveHelperController(GuardianEntity entity) {
            super(entity);
            this.entity = entity;
        }
        @Override public void tick() {
            if (this.operation == MovementController.Action.MOVE_TO) {
                if (this.floatDuration-- <= 0) {
                    this.floatDuration += entity.getRandom().nextInt(5) + 2;
                    Vector3d vector3d = new Vector3d(this.wantedX - entity.getX(), this.wantedY - entity.getY(), this.wantedZ - entity.getZ());
                    double d0 = vector3d.length();
                    vector3d = vector3d.normalize();
                    if (this.canReach(vector3d, MathHelper.ceil(d0))) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(vector3d.scale(0.1D)));
                    } else {
                        this.operation = MovementController.Action.WAIT;
                    }
                }
            }
            if(entity.isOnGround())
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.5D, 0));
        }
        private boolean canReach(Vector3d vector, int wantedDist) {
            AxisAlignedBB axisalignedbb = entity.getBoundingBox();
            for(int i = 1; i < wantedDist; ++i) {
                axisalignedbb = axisalignedbb.move(vector);
                if (!entity.level.noCollision(entity, axisalignedbb))
                    return false;
            }
            return true;
        }
    }
    static class RandomFlyGoal extends Goal {
        private final GuardianEntity guardian;

        public RandomFlyGoal(GuardianEntity entity) {
            guardian = entity;
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        @Override public boolean canUse() {
            MovementController movementcontroller = guardian.getMoveControl();
            if (!movementcontroller.hasWanted())
                return true;
            else {
                double d0 = movementcontroller.getWantedX() - guardian.getX();
                double d1 = movementcontroller.getWantedY() - guardian.getY();
                double d2 = movementcontroller.getWantedZ() - guardian.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                return d3 < 1.0D || d3 > 3600.0D;
            }
        }
        @Override public boolean canContinueToUse() {
            return false;
        }
        @Override public void start() {
            Random random = guardian.getRandom();
            double d0 = guardian.getX() + (double)((random.nextFloat() >= .45 ? -1 : 1)*((random.nextFloat() * 1.35F - 1.0F) * 8.0F));
            double d1 = guardian.getY() + (double)((random.nextFloat() >= .35 ? -1 : 1)*((random.nextFloat() * 0.65F - 1.0F) * 4.0F));
            double d2 = guardian.getZ() + (double)((random.nextFloat() >= .45 ? -1 : 1)*((random.nextFloat() * 1.35F - 1.0F) * 8.0F));
            guardian.getMoveControl().setWantedPosition(d0, d1, d2, 1.0D);
        }
    }
    static class LightningAttack extends Goal {
        private final GuardianEntity entity;
        public int chargeTime = 0;
        LightningAttack(GuardianEntity entity) { this.entity = entity; }
        @Override public boolean canUse() {
            return entity.getTarget() != null && !entity.isRecharging() && !entity.isAttacking() &&
                    entity.getHealth()/entity.getMaxHealth() <= 0.65;
        }
        @Override public boolean canContinueToUse() { return chargeTime > 0; }
        @Override public void start() {
            if(chargeTime <= 0){
                entity.setState(1);
                entity.setAttacking(true);
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.TRIDENT_THUNDER, SoundCategory.HOSTILE, 1.0f, 1.0f);
                chargeTime = 1;
                if (entity.getTarget() instanceof PlayerEntity)
                    ((PlayerEntity) entity.getTarget()).displayClientMessage(new TranslationTextComponent("entity.enhancedenchanting.guard.lightning_prepare", 0),
                            true);
            }
        }
        @Override public void stop() {
            if(chargeTime >= 150) {
                chargeTime = 0;
                entity.setRecharging(true);
                entity.setAttacking(false);
                entity.setState(0);
            }
        }
        @Override public void tick() {
            LivingEntity target = entity.getTarget();
            if(target != null && target.distanceToSqr(entity) < 32) {
                chargeTime++;
                if(chargeTime >= 150) {
                    boolean hasBlocksAbove = false;
                    for(int i = target.blockPosition().getY(); i < target.blockPosition().getY()+10; i++)
                        if(entity.level.getBlockState(new BlockPos(target.getX(), i, target.getZ())).getBlock()
                                != Blocks.AIR) {
                            hasBlocksAbove = true;
                            break;
                        }
                    BlockPos pos = new BlockPos(target.getX(), target.getY()+(hasBlocksAbove ? 5 : 0), target.getZ());
                    LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(entity.level);
                    bolt.setVisualOnly(true);
                    bolt.moveTo(pos, 0, 0);
                    if(!hasBlocksAbove) target.hurt(DamageSource.LIGHTNING_BOLT, 125);
                    entity.level.addFreshEntity(bolt);
                    stop();
                }
            }
        }
    }
    static class WindAttack extends Goal {
        private final GuardianEntity entity;
        public int chargeTime = 0;
        WindAttack(GuardianEntity entity) { this.entity = entity; }
        @Override public boolean canUse() {
            return entity.getTarget() != null && !entity.isRecharging() && !entity.isAttacking() &&
                    entity.getHealth()/entity.getMaxHealth() <= 0.35 && entity.random.nextInt(3) == 0;
        }
        @Override public boolean canContinueToUse() { return chargeTime > 0; }
        @Override public void start() {
            if(chargeTime <= 0){
                entity.setState(2);
                entity.setAttacking(true);
                chargeTime = 1;
                if (entity.getTarget() instanceof PlayerEntity)
                    ((PlayerEntity) entity.getTarget()).displayClientMessage(new TranslationTextComponent("entity.enhancedenchanting.guard.wind_prepare", 0),
                            true);
            }
        }
        @Override public void stop() {
            if(chargeTime >= 110) {
                chargeTime = 0;
                entity.setRecharging(true);
                entity.setAttacking(false);
                entity.setState(0);
            }
        }
        @Override public void tick() {
            LivingEntity target = entity.getTarget();
            if(target != null && target.distanceToSqr(entity) < 32) {
                entity.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.35f);
                chargeTime++;
                List<PlayerEntity> damage = entity.level.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(
                        entity.getX()-1.75f,
                        entity.getEyeY()-0.5f,
                        entity.getZ()-1.75f,
                        entity.getX()+1.75f,
                        entity.getEyeY()+0.5f,
                        entity.getZ()+1.75f
                ));
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.ELYTRA_FLYING, SoundCategory.HOSTILE, 1.0f, 1.0f);
                for(PlayerEntity toDamage : damage)
                    toDamage.hurt(DamageSource.MAGIC, 2.5f);
                if(chargeTime >= 110) stop();
            }
        }
    }
}
