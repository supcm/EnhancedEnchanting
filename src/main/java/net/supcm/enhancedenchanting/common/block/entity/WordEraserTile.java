package net.supcm.enhancedenchanting.common.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import net.supcm.enhancedenchanting.common.init.TileRegister;
import javax.annotation.Nonnull;
import java.util.Map;

public class WordEraserTile extends TileEntity implements ITickableTileEntity {
    public final ItemStackHandler handler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
            currentEnchantment = 0;
            currentEnchantmentName = "";
            changeCurrentEnchantment();
        }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.isEnchanted() && !(stack.getItem() instanceof ItemRegister.UnstableGlyphItem)
                    && stack.getItem() != Items.ENCHANTED_BOOK;
        }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    public String currentEnchantmentName = "";
    Map<Enchantment, Integer> enchs = Maps.newHashMap();
    public int currentEnchantment = 0;
    public WordEraserTile() {
        super(TileRegister.WORD_ERASER_TILE_TYPE.get());
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
        currentEnchantmentName = tag.getString("EnchantmentName");
        currentEnchantment = tag.getInt("Enchantment");
    }
    @Override public CompoundNBT save(CompoundNBT tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putString("EnchantmentName", currentEnchantmentName);
        tag.putInt("Enchantment", currentEnchantment);
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
                (player.getItemInHand(Hand.MAIN_HAND).getItem() == handler.getStackInSlot(slot).getItem())) {
            if(player.getItemInHand(Hand.MAIN_HAND).getCount() + handler.getStackInSlot(slot).getCount()
                    <= player.getItemInHand(Hand.MAIN_HAND).getMaxStackSize()) {
                player.getItemInHand(Hand.MAIN_HAND).grow(handler.getStackInSlot(slot).getCount());
                handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
            } else {
                if(player.inventory.getFreeSlot() != -1)
                    player.addItem(handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false));
                else
                    level.addFreshEntity(new ItemEntity(level,
                            player.blockPosition().getX() + 0.5,
                            player.blockPosition().getY() + 0.5,
                            player.blockPosition().getZ() + 0.5,
                            handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false)));
            }
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
    @Override public void tick() {
        if(!handler.getStackInSlot(0).isEmpty() && level.getGameTime() % 50 == 0)
            changeCurrentEnchantment();
    }
    void changeCurrentEnchantment() {
        enchs = EnchantmentHelper.getEnchantments(handler.getStackInSlot(0));
        if (!enchs.isEmpty()) {
            if(currentEnchantment < enchs.size() - 1)
                currentEnchantment++;
            else currentEnchantment = 0;
            currentEnchantmentName = ((Enchantment) enchs.keySet().toArray()[currentEnchantment]).getDescriptionId();
        } else {
            currentEnchantmentName = "";
            currentEnchantment = 0;
        }
    }
    public void proceedErasing() {
        if(!enchs.isEmpty()) {
            ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
            int i = 0;
            for(Map.Entry<Enchantment, Integer> entry : enchs.entrySet()) {
                if(i == currentEnchantment) {
                    EnchantedBookItem.addEnchantment(stack, new EnchantmentData(entry.getKey(),
                            entry.getValue()));
                    enchs.remove(entry.getKey());
                    EnchantmentHelper.setEnchantments(enchs, handler.getStackInSlot(0));
                    if(currentEnchantment > 0) currentEnchantment--;
                    changeCurrentEnchantment();
                    setChanged();
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
                    level.addFreshEntity(new ItemEntity(level,
                            worldPosition.getX() + 0.5d, worldPosition.getY() + 1.25d,
                            worldPosition.getZ() + 0.5d,
                            stack));
                    break;
                } else
                    i++;
            }
        }
    }
}
