package net.supcm.enhancedenchanting.common.block.entity;

import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.supcm.enhancedenchanting.common.data.recipes.ReassessmentRecipe;
import net.supcm.enhancedenchanting.common.init.BlockRegister;
import net.supcm.enhancedenchanting.common.init.ItemRegister;
import net.supcm.enhancedenchanting.common.init.RecipeRegister;
import net.supcm.enhancedenchanting.common.init.TileRegister;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class ReassessmentTableTile extends TileEntity implements ITickableTileEntity {
    private ReassessmentRecipe recipe = null;
    public boolean isValid;
    public int[] conceptions = new int[] {0, 0, 0, 0, 0, 0};
    public Map<BlockPos, ItemStack> pillars = Maps.newHashMap();
    public final ItemStackHandler handler = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) {
            updateRecipe();
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2 | 4 | 16);
        }
        @Override public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return true; }
        @Override protected int getStackLimit(int slot, @Nonnull ItemStack stack) { return 1; }
        @Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return !isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
        }};
    public final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> handler);
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
                inventory.cast() : super.getCapability(cap);
    }
    public ReassessmentTableTile() { super(TileRegister.REASSESSMENT_TABLE_TILE_TYPE.get()); }
    @Override protected void invalidateCaps() {
        super.invalidateCaps();
        inventory.invalidate();
    }
    @Override public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        handler.deserializeNBT(tag.getCompound("Inventory"));
        isValid = tag.getBoolean("IsValid");
        conceptions = tag.getIntArray("Conceptions");
    }
    @Override public CompoundNBT save(CompoundNBT tag) {
        tag.put("Inventory", handler.serializeNBT());
        tag.putBoolean("IsValid", isValid);
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
    public ReassessmentRecipe getRecipe() { return recipe; }
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
    int i = 0;
    @Override public void tick() {
        validate(worldPosition, i == 0);
        i++;
        if(!level.isClientSide && isValid)
            if(recipe == null && !handler.getStackInSlot(0).isEmpty())
                updateRecipe();
    }
    public void createResult() {
        if(recipe != null) {
            for(ItemStack stack : pillars.values()) {
                if(stack.getItem() == ItemRegister.CONCEPTION_BEAUTY.get())
                    stack.shrink(recipe.getConceptions()[0]);
                else if(stack.getItem() == ItemRegister.CONCEPTION_CREATION.get())
                    stack.shrink(recipe.getConceptions()[1]);
                else if(stack.getItem() == ItemRegister.CONCEPTION_ART.get())
                    stack.shrink(recipe.getConceptions()[2]);
                else if(stack.getItem() == ItemRegister.CONCEPTION_TRUTH.get())
                    stack.shrink(recipe.getConceptions()[3]);
                else if(stack.getItem() == ItemRegister.CONCEPTION_SOUL.get())
                    stack.shrink(recipe.getConceptions()[4]);
                else
                    stack.shrink(recipe.getConceptions()[5]);
            }
            for(BlockPos pos : pillars.keySet())
                if(level.getBlockEntity(pos) instanceof ReassessmentPillarTile) {
                    ReassessmentPillarTile tile = ((ReassessmentPillarTile) level.getBlockEntity(pos));
                    tile.handler.setStackInSlot(0, tile.handler.getStackInSlot(0));
                }
            handler.setStackInSlot(0, recipe.getResultItem());
        }
        updateRecipe();
    }
    private int[] createConceptions() {
        int[] conceptions = new int[] {0, 0, 0, 0, 0, 0};
        for(ItemStack stack : pillars.values()) {
            if(stack.getItem() == ItemRegister.CONCEPTION_BEAUTY.get()){
                conceptions[0] = stack.getCount();
            } else if(stack.getItem() == ItemRegister.CONCEPTION_CREATION.get()){
                conceptions[1] = stack.getCount();
            } else if(stack.getItem() == ItemRegister.CONCEPTION_ART.get()){
                conceptions[2] = stack.getCount();
            } else if(stack.getItem() == ItemRegister.CONCEPTION_TRUTH.get()){
                conceptions[3] = stack.getCount();
            } else if(stack.getItem() == ItemRegister.CONCEPTION_SOUL.get()){
                conceptions[4] = stack.getCount();
            } else if(stack.getItem() == ItemRegister.CONCEPTION_LIES.get())
                conceptions[5] = stack.getCount();
        }
        return conceptions;
    }
    public void updateRecipe() {
        if(isValid){
            boolean setLeastOne = false;
            int[] conceptions = createConceptions();
            List<ReassessmentRecipe> recipes = level.getRecipeManager()
                    .getAllRecipesFor(RecipeRegister.REASSESSMENT_RECIPE_TYPE);
            for (ReassessmentRecipe recipe : recipes) {
                if (recipe.getIngredients().get(0).test(handler.getStackInSlot(0))) {
                    boolean isVal = true;
                    for (int i = 0; i < conceptions.length; i++) {
                        if (recipe.getConceptions()[i] != 0 &&
                                conceptions[i] < recipe.getConceptions()[i]) {
                            isVal = false;
                            break;
                        }
                    }
                    if (isVal) {
                        this.recipe = recipe;
                        this.conceptions = recipe.getConceptions();
                        setLeastOne = true;
                    }
                }
            }
            if(!setLeastOne) {
                recipe = null;
                this.conceptions = new int[] {0, 0, 0, 0, 0, 0};
            }
            for(BlockPos pos : pillars.keySet())
                if(level.getBlockEntity(pos) instanceof ReassessmentPillarTile) {
                    ReassessmentPillarTile tile = ((ReassessmentPillarTile) level.getBlockEntity(pos));
                    tile.setChanged();
                }
        }
    }
    public void invalidatePillars() {
        for(BlockPos pos : pillars.keySet()) {
            if(level.getBlockEntity(pos) instanceof ReassessmentPillarTile){
                ReassessmentPillarTile tile = (ReassessmentPillarTile) level.getBlockEntity(pos);
                tile.setLinkedTable(null);
            }
        }
        pillars.clear();
        updateRecipe();
    }
    private void changeValid(boolean valid, BlockPos[] pillarsPoses) {
        isValid = valid;
        if(valid){
            for (BlockPos pos : pillarsPoses) {
                ReassessmentPillarTile tile = (ReassessmentPillarTile)level.getBlockEntity(pos);
                tile.setLinkedTable(this);
                pillars.put(pos, tile.handler.getStackInSlot(0));
                updateRecipe();
            }
        } else {
            invalidatePillars();
        }
    }
    public void validate(BlockPos pos, boolean isFirstStart) {
        boolean valid = true;
        BlockPos[] pillarsPoses = new BlockPos[6];
        int k = 0;
        for(int i = -2; i < 3; i++)
            for(int j = -2; j < 3; j++) {
                if((i == -2 || i == 2) && j == 0) {
                    if(level.getBlockState(pos.north(i)).getBlock() != BlockRegister.REASSESSMENT_PILLAR.get())
                        valid = false;
                    else {
                        pillarsPoses[k] = new BlockPos(pos.north(i));
                        k++;
                    }
                } else if((i == -1 || i == 1) && (j == -2 || j == 2)) {
                    if(level.getBlockState(pos.north(i).west(j)).getBlock() != BlockRegister.REASSESSMENT_PILLAR.get())
                        valid = false;
                    else {
                        pillarsPoses[k] = new BlockPos(pos.north(i).west(j));
                        k++;
                    }
                } else {
                    if (level.getBlockState(pos.north(i).west(j)).getMaterial() != Material.AIR &&
                            !(i == 0 && j == 0))
                        valid = false;
                }
            }
        if(isFirstStart) {
            changeValid(isValid, pillarsPoses);
            updateRecipe();
        }
        if(!valid && isValid) changeValid(false, pillarsPoses);
        else if(valid && !isValid) changeValid(true, pillarsPoses);
    }
}
