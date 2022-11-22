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

public class ReassessmentPillarTile extends TileEntity {
    public ReassessmentTableTile tile = null;
    public int[] conceptions;
    public final ItemStackHandler handler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) {
            if(tile != null) {
                tile.pillars.replace(ReassessmentPillarTile.this.getBlockPos(), handler.getStackInSlot(slot));
                tile.updateRecipe();
            }
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
        }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() instanceof ItemRegister.ConceptionItem;
        }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                inventory.cast() : super.getCapability(cap);
    }
    @Override public void setChanged() {
        conceptions = tile != null ? tile.conceptions : new int[]{0, 0, 0, 0, 0, 0};
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
        super.setChanged();
    }
    public ReassessmentPillarTile() {
        super(TileRegister.REASSESSMENT_PILLAR_TILE_TYPE.get());
    }
    @Override protected void invalidateCaps() {
        super.invalidateCaps();
        inventory.invalidate();
    }
    @Override public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        handler.deserializeNBT(tag.getCompound("Inventory"));
        conceptions = tag.getIntArray("Conceptions");
    }
    @Override public CompoundNBT save(CompoundNBT tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putIntArray("Conceptions", conceptions);
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
        if(!(handler.getStackInSlot(slot).isEmpty() || player.getItemInHand(Hand.MAIN_HAND).isEmpty()) &&
                player.getItemInHand(Hand.MAIN_HAND).getItem() == handler.getStackInSlot(slot).getItem()) {
            player.getItemInHand(Hand.MAIN_HAND).grow(handler.getStackInSlot(slot).getCount());
            handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
        } else if(player.getItemInHand(Hand.MAIN_HAND).isEmpty()){
            if(player.inventory.getFreeSlot() != -1)
                player.addItem(handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false));
            else
                level.addFreshEntity(new ItemEntity(level,
                        player.blockPosition().getX() + 0.5,
                        player.blockPosition().getY() + 0.5,
                        player.blockPosition().getZ() + 0.5,
                        handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false)));
        } else {
            player.setItemInHand(Hand.MAIN_HAND, handler.insertItem(slot,
                    player.getItemInHand(Hand.MAIN_HAND), false));
        }
    }
    void setLinkedTable(ReassessmentTableTile tile){
        this.tile = tile;
        setChanged();
    }
}
