package net.supcm.enhancedenchanting.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.enhancedenchanting.common.init.TileRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

import javax.annotation.Nonnull;

public class MatrixTile extends TileEntity implements ITickableTileEntity {
    public MatrixTile() { super(TileRegister.MATRIX_TILE_TYPE.get()); }
    public final ItemStackHandler handler = new ItemStackHandler(2) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);}
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot == 0 ? stack.getItem() == ItemRegister.PLATE.get() :
                    (stack.getItem() instanceof ItemRegister.GlyphItem ||
                            stack.getItem() instanceof ItemRegister.UnstableGlyphItem);
        }
        @Override public int getSlotLimit(int slot) { return slot == 0 ? 64 : 1; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public int tick;
    public boolean doCraft;
    public int renderTick;
    public boolean doRenderCrystal;
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
        tick = tag.getInt("Tick");
        doCraft = tag.getBoolean("DoCraft");
    }
    @Override public CompoundNBT save(CompoundNBT tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putInt("Tick", tick);
        tag.putBoolean("DoCraft", doCraft);
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
    public void setDoCraft(boolean craft) {
        doCraft = craft;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
    }
    @Override public void tick() {
        doRenderCrystal = doCraft;
        renderTick = tick;
        if(doCraft) {
            tick++;
            if(tick >= 60) {
                handler.getStackInSlot(0).shrink(1);
                level.explode(null, getBlockPos().getX() + 0.5D,
                        getBlockPos().getY() + 1.25D,
                        getBlockPos().getZ() + 0.5D, 0.1f, Explosion.Mode.NONE);
                ItemStack stack = new ItemStack(ItemRegister.CONCEPTION_BASE.get(), 4);
                if(!handler.getStackInSlot(1).isEmpty())
                    stack = handler.getStackInSlot(1).copy();
                level.addFreshEntity(new ItemEntity(level, getBlockPos().getX()+0.5,
                        getBlockPos().getY()+1.35D,
                        getBlockPos().getZ()+0.5, stack));
                tick = 0;
                setDoCraft(false);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
            }
        }
    }
}
