package net.supcm.enhancedenchanting.common.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;
import net.supcm.enhancedenchanting.common.block.entity.ExaltationTableTile;
import net.supcm.enhancedenchanting.common.exaltation.Exaltation;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.init.ContainerRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;

public class ExaltationTableContainer extends Container {
    private final IWorldPosCallable callable;
    public final ExaltationTableTile tile;
    public ExaltationTableContainer(int window, PlayerInventory playerInventory, ExaltationTableTile tile) {
        super(ContainerRegister.EXALTATION_TABLE_CONTAINER.get(), window);
        callable = IWorldPosCallable.create(playerInventory.player.level, tile.getBlockPos());
        this.tile = tile;
        int index = 0;
        for(int i = 0; i < 3; ++i)
            for(int j = 0; j < 9; ++j)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for(int k = 0; k < 9; ++k)
            addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        addSlot(new SlotItemHandler(tile.handler, index++, 80, 36) {
            @Override public void setChanged() {
                super.setChanged();
                ExaltationTableContainer.this.updateStones();
            }
            @Override public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                for(int i = 1; i < 5; i++)
                    slots.get(36+i).set(ItemStack.EMPTY);
                return super.onTake(player, stack);
            }
        });
        for(int x = 0; x < 2; x++)
            for(int y = 0; y < 2; y++)
                addSlot(new SlotItemHandler(tile.handler, index++, x % 2 != 0 ? 134 : 26, y > 0 ? 54 : 18) {
                    @Override public void setChanged() {
                        super.setChanged();
                        ExaltationTableContainer.this.updateStones();
                    }
                    @Override public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                        if(getSlot(36).hasItem()){
                            CompoundNBT tag = getSlot(36).getItem().getTag();
                            if (tag != null && tag.contains("" + (getSlotIndex() - 1)))
                                tag.remove("" + (getSlotIndex() - 1));
                            getSlot(36).getItem().setTag(tag);
                        }
                        return super.onTake(player, stack);
                    }
                });
    }
    public ExaltationTableContainer(int window, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(window, playerInventory, getTileAtPos(buffer.readBlockPos(), playerInventory.player.level));
    }
    public static ExaltationTableTile getTileAtPos(BlockPos pos, World world) {
        return world.getBlockEntity(pos) instanceof ExaltationTableTile ?
                (ExaltationTableTile) world.getBlockEntity(pos) : null;
    }
    @Override public boolean stillValid(PlayerEntity player) {
        return stillValid(callable, player, BlockRegister.EXALTATION_TABLE.get());
    }
    private void updateStones() {
        if(getSlot(36).hasItem()) {
           CompoundNBT tag =  getSlot(36).getItem().getTag();
           if(tag != null) {
               for(int i = 1; i < 5; i++) {
                   if(!getSlot(36 + i).hasItem()) {
                       Exaltation ex = Exaltation.deserialize(tag.getCompound("" + (i - 1)));
                       if (ex != null) {
                           ItemStack stone = new ItemStack(ItemRegister.EXALTATION_STONE.get());
                           CompoundNBT stoneTag = new CompoundNBT();
                           stoneTag.putBoolean("IsAbility", ex.isAbility());
                           stoneTag.put("Exaltation", ex.serialize());
                           stone.setTag(stoneTag);
                           getSlot(36 + i).set(stone);
                       }
                   }
               }
           }
           for(int i = 1; i < 5; i++) {
               ItemStack stone = getSlot(36 + i).getItem();
               if(stone.getTag() != null)
                   tag.put("" + (i - 1), stone.getTag().getCompound("Exaltation"));
               getSlot(36).getItem().setTag(tag);
           }
        }
    }
    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 5;
    @Override public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX,
                   VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
}
