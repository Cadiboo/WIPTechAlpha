package cadiboo.wiptech.tileentity;

import cadiboo.wiptech.capability.inventory.IInventoryUser;
import cadiboo.wiptech.capability.inventory.ModItemStackHandler;
import cadiboo.wiptech.event.ModEventFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityModFurnace extends TileEntity implements ITickable, ITileEntitySyncable, IInventoryUser {

	public static final int	INPUT_SLOT	= 0;
	public static final int	FUEL_SLOT	= 1;
	public static final int	OUTPUT_SLOT	= 2;

	private final ModItemStackHandler inventory;

	private int	fuelTimeRemaining;
	private int	smeltTime;
	private int	maxFuelTime;
	private int	maxSmeltTime;

	public int getMaxFuelTime() {
		return this.maxFuelTime;
	}

	public int getMaxSmeltTime() {
		return this.maxSmeltTime;
	}

	public int getFuelTimeRemaining() {
		return this.fuelTimeRemaining;
	}

	public int getSmeltTime() {
		return this.smeltTime;
	}

	public TileEntityModFurnace() {
		TileEntityFurnace.class.getName();

		this.inventory = new ModItemStackHandler(3) {

			@Override
			public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
				if (!this.isItemValid(slot, stack)) {
					return stack;
				}
				return super.insertItem(slot, stack, simulate);
			}

			@Override
			protected void onContentsChanged(final int slot) {
				if (slot == INPUT_SLOT) {
					final ItemStack input = TileEntityModFurnace.this.getInventory().getStackInSlot(INPUT_SLOT);
					if (!input.isEmpty()) {
						TileEntityModFurnace.this.maxSmeltTime = TileEntityModFurnace.this.getSmeltTime(input);
					} else {
						TileEntityModFurnace.this.maxSmeltTime = 0;
					}
					TileEntityModFurnace.this.smeltTime = 0;
				}
			}

			@Override
			public boolean isItemValid(final int slot, final ItemStack stack) {
				if (slot == INPUT_SLOT) {
					if (!FurnaceRecipes.instance().getSmeltingResult(stack).isEmpty()) {
						return true;
					} else {
						return false;
					}
				}

				if (slot == FUEL_SLOT) {
					if (TileEntityFurnace.isItemFuel(stack)) {
						return true;
					} else {
						return false;
					}
				}

				if (slot == OUTPUT_SLOT) {
					return false;
				}
				return super.isItemValid(slot, stack);
			}
		};

	}

	@Override
	public void update() {
		if (this.fuelTimeRemaining > 0) {
			this.fuelTimeRemaining--;
		}

		if (this.world.isRemote) {
			return;
		}

		this.burnFuel();

		this.smelt();

		this.trySmeltItem();

		for (final EntityPlayer player : this.world.playerEntities) {
			this.syncToClient(player);
		}

//		this.world.playerEntities.forEach(player -> {
//			this.syncToClient(player);
//		});
	}

	private void smelt() {

		if (!this.shouldSmelt()) {
			return;
		}

		if (this.fuelTimeRemaining <= 0) {
			return;
		}

		this.smeltTime++;
	}

	private boolean shouldSmelt() {

		final ItemStack input = this.getInventory().getStackInSlot(INPUT_SLOT);
		final ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);

		final ItemStack output = this.getInventory().getStackInSlot(OUTPUT_SLOT);

		if (result.isEmpty() || input.isEmpty()) {
			return false;
		}

		if ((output.getCount() + result.getCount()) > output.getMaxStackSize()) {
			return false;
		}

		if (output.isEmpty() || output.isItemEqual(result)) {
			return true;
		}

		return false;
	}

	private void burnFuel() {

		if (!this.shouldSmelt()) {
			return;
		}

		if (this.fuelTimeRemaining > 0) {
			return;
		}

		final ItemStack fuel = this.getInventory().getStackInSlot(FUEL_SLOT);

		if (fuel.isEmpty()) {
			return;
		}

		this.fuelTimeRemaining = TileEntityFurnace.getItemBurnTime(fuel.copy());
		this.maxFuelTime = this.fuelTimeRemaining;

		fuel.shrink(1);

		if (!fuel.isEmpty()) {
			return;
		}

		final ItemStack containerItem = fuel.getItem().getContainerItem(fuel.copy());

		if (!containerItem.isEmpty()) {
			this.getInventory().setStackInSlot(FUEL_SLOT, containerItem.copy());
		}

	}

	private boolean canSmeltItem() {

		if (!this.shouldSmelt()) {
			return false;
		}

		if (this.fuelTimeRemaining <= 0) {
			return false;
		}

		final ItemStack input = this.getInventory().getStackInSlot(INPUT_SLOT);

		if (this.smeltTime >= this.maxSmeltTime) {
			final ItemStack output = this.getInventory().getStackInSlot(OUTPUT_SLOT);
			final ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);

			if (output.isEmpty() || output.isItemEqual(result)) {
				return true;
			}
		}
		return false;
	}

	private void trySmeltItem() {

		if ((this.fuelTimeRemaining <= 0) && (this.smeltTime > 0)) {
			this.smeltTime--;
		}

		if (!this.shouldSmelt()) {
			return;
		}

		if (!this.canSmeltItem()) {
			return;
		}
		this.smeltTime = 0;

		final ItemStack input = this.getInventory().getStackInSlot(INPUT_SLOT);
		final ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);
		final ItemStack output = this.getInventory().getStackInSlot(OUTPUT_SLOT);

		if (output.isEmpty()) {
			this.getInventory().setStackInSlot(OUTPUT_SLOT, result.copy());
		} else if (output.isItemEqual(result)) {
			output.grow(result.getCount());
		}

		if ((input.getItem() == Item.getItemFromBlock(Blocks.SPONGE)) && (input.getMetadata() == 1) && !this.getInventory().getStackInSlot(FUEL_SLOT).isEmpty() && (this.getInventory().getStackInSlot(FUEL_SLOT).getItem() == Items.BUCKET)) {
			this.getInventory().setStackInSlot(FUEL_SLOT, new ItemStack(Items.WATER_BUCKET));
		}

		input.shrink(1);

		if (!input.isEmpty()) {
			this.maxSmeltTime = this.getSmeltTime(input);
		} else {
			this.maxSmeltTime = 0;
		}

	}

	public int getSmeltTime(final ItemStack input) {
		final int smeltTime = ModEventFactory.getItemSmeltTime(input);
		if (smeltTime >= 0) {
			return smeltTime;
		}
		return 200;
	}

	@Override
	public ModItemStackHandler getInventory() {
		return this.inventory;
	}

	@Override
	public BlockPos getPosition() {
		return this.pos;
	}

	@Override
	public void readFromNBT(final NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.readNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(final NBTTagCompound compound) {
		super.writeToNBT(compound);
		this.writeNBT(compound);
		return compound;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		this.readNBT(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagCompound syncTag = super.serializeNBT();
		this.writeNBT(syncTag);
		return syncTag;
	}

	@Override
	public void readNBT(final NBTTagCompound syncTag) {
		this.fuelTimeRemaining = syncTag.getInteger("fuelTimeRemaining");
		this.smeltTime = syncTag.getInteger("smeltTime");
		this.maxFuelTime = syncTag.getInteger("maxFuelTime");
		this.maxSmeltTime = syncTag.getInteger("maxSmeltTime");
		this.getInventory().deserializeNBT(syncTag.getCompoundTag("inventory"));
	}

	@Override
	public void writeNBT(final NBTTagCompound syncTag) {
		syncTag.setInteger("fuelTimeRemaining", this.fuelTimeRemaining);
		syncTag.setInteger("smeltTime", this.smeltTime);
		syncTag.setInteger("maxFuelTime", this.maxFuelTime);
		syncTag.setInteger("maxSmeltTime", this.maxSmeltTime);
		syncTag.setTag("inventory", this.getInventory().serializeNBT());
	}

	public boolean isOn() {
		return this.fuelTimeRemaining > 0;
	}

	@Override
	public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@Override
	public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			final NonNullList<ItemStack> stacks = NonNullList.create();
			final int realSlot;
			switch (facing) {
			case UP:
				realSlot = INPUT_SLOT;
				stacks.add(this.getInventory().getStackInSlot(realSlot));
				break;
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				realSlot = FUEL_SLOT;
				stacks.add(this.getInventory().getStackInSlot(realSlot));
				break;
			default:
			case DOWN:
				realSlot = OUTPUT_SLOT;
				stacks.add(this.getInventory().getStackInSlot(realSlot));
			}
			return (T) new ItemStackHandler(stacks) {
				@Override
				public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
					return TileEntityModFurnace.this.getInventory().insertItem(realSlot, stack, simulate);
				}

				@Override
				public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
					return TileEntityModFurnace.this.getInventory().extractItem(realSlot, amount, simulate);
				}

				@Override
				public boolean isItemValid(final int slot, final ItemStack stack) {
					return TileEntityModFurnace.this.getInventory().isItemValid(realSlot, stack);
				}
			};
		}
		return super.getCapability(capability, facing);
	}

}
