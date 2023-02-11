package net.supcm.enhancedenchanting.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import net.supcm.enhancedenchanting.common.init.TileRegister;

import javax.annotation.Nonnull;

public class ExaltationTableTile extends TileEntity {
    public final ItemStackHandler handler = new ItemStackHandler(5) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if(slot == 0)
                return stack.getItem() == ItemRegister.EXALTATION_SWORD.get() ||
                    stack.getItem() == ItemRegister.EXALTATION_PICKAXE.get() ||
                        stack.getItem() == ItemRegister.EXALTATION_AXE.get() ||
                        stack.getItem() == ItemRegister.EXALTATION_SHOVEL.get();
            else
                return !getStackInSlot(0).isEmpty() && stack.getItem() == ItemRegister.EXALTATION_STONE.get();
        }
        @Override public int getSlotLimit(int slot) { return slot == 0 ? 1 : super.getSlotLimit(slot); }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public ExaltationTableTile() {
        super(TileRegister.EXALTATION_TABLE_TILE_TYPE.get());
    }
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
}
