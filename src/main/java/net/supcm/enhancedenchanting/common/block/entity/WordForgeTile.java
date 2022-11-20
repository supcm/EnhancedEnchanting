package net.supcm.enhancedenchanting.common.block.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.enhancedenchanting.EnhancedEnchantingConfig;
import net.supcm.enhancedenchanting.common.enchantments.EnchantmentsList;
import net.supcm.enhancedenchanting.common.entity.GuardianEntity;
import net.supcm.enhancedenchanting.common.init.TileRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import net.supcm.enhancedenchanting.common.data.recipes.EnchantingRecipe;
import net.supcm.enhancedenchanting.common.init.RecipeRegister;
import net.supcm.enhancedenchanting.common.item.CodexItem;

import javax.annotation.Nonnull;
import java.util.List;

public class WordForgeTile extends TileEntity {
    public WordForgeTile() {
        super(TileRegister.WORD_FORGE_TILE_TYPE.get());
    }
    public final ItemStackHandler handler = new ItemStackHandler(3) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof ItemRegister.GlyphItem;
        }
        @Override public int getSlotLimit(int slot) { return 16; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public int enchLevel;
    public boolean warranty = false;
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                inventory.cast() : super.getCapability(cap);
    }
    @Override protected void invalidateCaps() {
        super.invalidateCaps();
        inventory.invalidate();
    }
    @Override public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        handler.deserializeNBT(tag.getCompound("Inventory"));
        enchLevel = tag.getInt("EnchantmentLevel");
    }
    @Override public CompoundNBT save(CompoundNBT tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putBoolean("Warranty", warranty);
        tag.putInt("EnchantmentLevel", enchLevel);
        return super.save(tag);
    }

    @Override public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), -90, getUpdateTag());
    }
    @Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
    }
    @Override public CompoundNBT getUpdateTag() { return save(new CompoundNBT()); }
    @Override public void handleUpdateTag(BlockState state, CompoundNBT tag) { load(state, tag); }

    public void insertOrExtractItem(PlayerEntity player, int slot) {
        if(player.getItemInHand(Hand.MAIN_HAND).getItem() == handler.getStackInSlot(slot).getItem()
                && !(handler.getStackInSlot(slot).isEmpty() || player.getItemInHand(Hand.MAIN_HAND).isEmpty())) {
            player.getItemInHand(Hand.MAIN_HAND).grow(handler.getStackInSlot(slot).getCount());
            handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
        } else if(player.getItemInHand(Hand.MAIN_HAND).isEmpty()){
            if(player.inventory.getFreeSlot() == -1) return;
            else player.addItem(handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false));
        } else {
            player.setItemInHand(Hand.MAIN_HAND, handler.insertItem(slot,
                    player.getItemInHand(Hand.MAIN_HAND), false));
        }
    }
    public void getEnchLevel(int tier) {
        Enchantment ench = null;
        if(tier == 0) ench = getEnchantment();
        else if(tier == 2) {
            if (getEnchantmentT3() != null) ench = getEnchantmentT3();
        } else if(tier == 1)
            if(getEnchantmentT2() != null) ench = getEnchantmentT2();
        if(ench == null) {
            enchLevel = -1;
            return;
        }
        int count = Math.max(handler.getStackInSlot(0).getCount(),
                Math.max(handler.getStackInSlot(1).getCount(), handler.getStackInSlot(2).getCount()));
        if(ench.getMaxLevel() == 1) count = 1;
        enchLevel = (EnhancedEnchantingConfig.ENCHANTING_MULT.get() / ench.getMaxLevel())*count;
    }
    public Enchantment getEnchantment() {
        Enchantment ench = null;
        if(EnchantmentsList.T1_MAP.keySet().contains(handler.getStackInSlot(0)
                .getItem().getRegistryName().getPath()))
            ench = EnchantmentsList.T1_MAP.get(handler.getStackInSlot(0)
                    .getItem().getRegistryName().getPath());
        return ench;
    }
    public Enchantment getEnchantmentT2() {
        Enchantment ench = null;
        if(EnchantmentsList.T2_MAP.keySet().contains(
                handler.getStackInSlot(0).getItem().getRegistryName().getPath() + "_" +
                        handler.getStackInSlot(1).getItem().getRegistryName().getPath()))
            ench = EnchantmentsList.T2_MAP.get(
                    handler.getStackInSlot(0).getItem().getRegistryName().getPath() + "_" +
                            handler.getStackInSlot(1).getItem().getRegistryName().getPath());
        return ench;
    }
    public Enchantment getEnchantmentT3() {
        Enchantment ench = null;
        if(EnchantmentsList.T3_MAP.keySet().contains(
                handler.getStackInSlot(0).getItem().getRegistryName().getPath() + "_" +
                        handler.getStackInSlot(1).getItem().getRegistryName().getPath() + "_" +
                        handler.getStackInSlot(2).getItem().getRegistryName().getPath()))
            ench = EnchantmentsList.T3_MAP.get(
                    handler.getStackInSlot(0).getItem().getRegistryName().getPath() + "_" +
                            handler.getStackInSlot(1).getItem().getRegistryName().getPath() + "_" +
                            handler.getStackInSlot(2).getItem().getRegistryName().getPath());
        return ench;
    }
    public ActionResultType enchantBook(PlayerEntity player, ItemStack handItem, int tier) {
        List<GuardianEntity> guardians = level.getEntitiesOfClass(GuardianEntity.class, new AxisAlignedBB(
                getBlockPos().getX() - 32,
                getBlockPos().getY() - 32,
                getBlockPos().getZ() - 32,
                getBlockPos().getX() + 32,
                getBlockPos().getY() + 32,
                getBlockPos().getZ() + 32
        ));
        if(!guardians.isEmpty()) {
            player.sendMessage(new TranslationTextComponent("enchanting.guardian_exists", 0), player.getUUID());
            return ActionResultType.PASS;
        }
        Enchantment ench = null;
        if(tier == 1)
            ench = getEnchantmentT2();
        else if(tier == 0)
            ench = getEnchantment();
        else
            ench = getEnchantmentT3();
        if(ench == null || enchLevel == -1) return ActionResultType.PASS;
        int count = Math.max(handler.getStackInSlot(0).getCount(),
                Math.max(handler.getStackInSlot(1).getCount(), handler.getStackInSlot(2).getCount()));
        if(ench.getMaxLevel() == 1) count = 1;
        if(count > ench.getMaxLevel()) {
            if(level.getRandom().nextInt(17-count) == 0 && !warranty) {
                player.sendMessage(new TranslationTextComponent("block.word_forge.guardian_summon", 0), player.getUUID());
                level.addFreshEntity(new GuardianEntity(this));
                warranty = true;
                return ActionResultType.PASS;
            }
        }
        if (player.experienceLevel < enchLevel && !player.isCreative()){
            player.sendMessage(new TranslationTextComponent("enchanting.notenoughxp", 0), player.getUUID());
            return ActionResultType.PASS;
        }
        if (!player.isCreative()) {
            player.onEnchantmentPerformed(handItem, enchLevel);
            handItem.shrink(1);
        }
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(stack, new EnchantmentData(ench, count));
        player.addItem(stack);
        level.explode(player, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.25D,
                getBlockPos().getZ() + 0.5D, 0.5f, Explosion.Mode.NONE);
        for(ItemStack codex : player.inventory.items) {
            if(codex.getItem() instanceof CodexItem) {
                StringNBT str = StringNBT.valueOf(CodexItem.getGlyphsFor(ench.getRegistryName().toString())
                        + "'" + ench.getRegistryName().toString());
                List<INBT> tag = codex.getOrCreateTag().getList("Revealed", 8);
                if(!tag.contains(str))
                    tag.add(str);
            }
        }
        warranty = false;
        player.awardStat(Stats.ENCHANT_ITEM);
        if (player instanceof ServerPlayerEntity)
            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, stack, 1 | 2);
        setChanged();
        return ActionResultType.SUCCESS;
    }
    public ActionResultType enchantItem(PlayerEntity player, ItemStack handItem) {
        List<GuardianEntity> guardians = level.getEntitiesOfClass(GuardianEntity.class, new AxisAlignedBB(
                getBlockPos().getX() - 32,
                getBlockPos().getY() - 32,
                getBlockPos().getZ() - 32,
                getBlockPos().getX() + 32,
                getBlockPos().getY() + 32,
                getBlockPos().getZ() + 32
        ));
        if(!guardians.isEmpty()) {
            player.sendMessage(new TranslationTextComponent("enchanting.guardian_exists", 0), player.getUUID());
            return ActionResultType.PASS;
        }
        if(!handler.getStackInSlot(0).isEmpty() ||
                !handler.getStackInSlot(1).isEmpty() ||
                !handler.getStackInSlot(2).isEmpty()){
            List<EnchantingRecipe> recipeList = level.getRecipeManager()
                    .getAllRecipesFor(RecipeRegister.ENCHANTING_RECIPE_TYPE);
            for(EnchantingRecipe recipe : recipeList) {
                if(recipe.getIngredients().get(0).test(handItem)) {
                    ItemStack output = recipe.getResultItem();
                    int thisLevel = recipe.getLevel();
                    if (player.experienceLevel < enchLevel && !player.isCreative()){
                        player.sendMessage(new TranslationTextComponent("enchanting.notenoughxp", 0), player.getUUID());
                        return ActionResultType.PASS;
                    }
                    if(!player.isCreative()) {
                        player.onEnchantmentPerformed(output, thisLevel);
                        handItem.shrink(1);
                    }
                    player.addItem(output);
                    level.explode(player, getBlockPos().getX() + 0.5D, getBlockPos().getY() + 1.25D,
                            getBlockPos().getZ() + 0.5D, 0.5f, Explosion.Mode.NONE);
                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayerEntity)
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, output, 1 | 2);
                    setChanged();
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }
}
