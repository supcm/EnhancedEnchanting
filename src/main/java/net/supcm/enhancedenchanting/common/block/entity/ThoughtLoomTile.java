package net.supcm.enhancedenchanting.common.block.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.enhancedenchanting.common.data.recipes.ConceptionRecipe;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import net.supcm.enhancedenchanting.common.init.RecipeRegister;
import net.supcm.enhancedenchanting.common.init.TileRegister;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ThoughtLoomTile extends TileEntity {
    public ThoughtLoomTile() { super(TileRegister.THOUGHT_WEAVER_TILE_TYPE.get()); }
    public final ItemStackHandler handler = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot != 0 || stack.getItem() instanceof ItemRegister.UnstableGlyphItem;
        }
        @Override public int getSlotLimit(int slot) { return slot == 0 ? 1 : super.getSlotLimit(slot); }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
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
    }
    @Override public CompoundNBT save(CompoundNBT tag) {
        tag.put("Inventory", handler.serializeNBT());
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
            if(player.inventory.getFreeSlot() != -1)
                player.addItem(handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false));
        } else {
            player.setItemInHand(Hand.MAIN_HAND, handler.insertItem(slot,
                    player.getItemInHand(Hand.MAIN_HAND), false));
        }
    }
    public ActionResultType createConception(PlayerEntity player, ItemStack handItem){
        if(!handler.getStackInSlot(0).isEmpty() && (
                !handler.getStackInSlot(1).isEmpty() ||
                        !handler.getStackInSlot(2).isEmpty() ||
                        !handler.getStackInSlot(3).isEmpty())) {
            List<ConceptionRecipe> recipes = level.getRecipeManager()
                    .getAllRecipesFor(RecipeRegister.CONCEPTION_RECIPE_TYPE);
            for(ConceptionRecipe recipe : recipes) {
                if(recipe.getLevel() <= player.experienceLevel ||
                    player.isCreative()) {
                    int lvl = recipe.getLevel();
                    ItemStack output = recipe.getResultItem();
                    NonNullList<Ingredient> inputs = recipe.getIngredients();
                    NonNullList<Item> inv = NonNullList.withSize(handler.getSlots(), Items.AIR);
                    for(int i = 0; i < handler.getSlots(); i++)
                        inv.set(i, handler.getStackInSlot(i).getItem());
                    if(inputs.get(0).test(handler.getStackInSlot(0)) &&
                            Arrays.stream(inputs.get(1).getItems()).anyMatch(i -> inv.contains(i.getItem())) &&
                            Arrays.stream(inputs.get(2).getItems()).anyMatch(i -> inv.contains(i.getItem()))&&
                            Arrays.stream(inputs.get(3).getItems()).anyMatch(i -> inv.contains(i.getItem()))) {
                        if(!player.isCreative() && lvl != -1) {
                            player.onEnchantmentPerformed(output, lvl);
                            handItem.shrink(1);
                        }
                        level.addFreshEntity(new ItemEntity(level,
                                worldPosition.getX() + 0.5,
                                worldPosition.getY() + 1.25,
                                worldPosition.getZ() + 0.5,
                                output));
                        if(level.getRandom().nextInt(4) == 0) handler.getStackInSlot(0).shrink(1);
                        handler.getStackInSlot(1).shrink(1);
                        handler.getStackInSlot(2).shrink(1);
                        handler.getStackInSlot(3).shrink(1);
                        player.awardStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayerEntity)
                            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, output, 1 | 2);
                        level.playSound(null, worldPosition, SoundEvents.TRIPWIRE_DETACH, SoundCategory.BLOCKS,
                                1.0f, 1.0f);
                        setChanged();
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
                        return ActionResultType.SUCCESS;
                    }
                } else
                    player.sendMessage(new TranslationTextComponent("enchanting.notenoughxp", 0),
                            player.getUUID());
            }
        }
        return ActionResultType.PASS;
    }
}
