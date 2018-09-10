package cadiboo.wiptech.util;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import cadiboo.wiptech.creativetab.ModCreativeTabs;
import cadiboo.wiptech.util.ModEnums.ModMaterials;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Utility Methods
 * @author Cadiboo
 */
public final class ModUtil {

	/**
	 * Sets the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl#setRegistryName(net.minecraft.util.ResourceLocation) Registry Name} and the {@link net.minecraft.block.Block#setUnlocalizedName() Unlocalised Name} for the block taking vanilla overriding into account
	 * @param block      the block to set registry names for
	 * @param material   the {@link cadiboo.wiptech.util.ModEnums.ModMaterials Mod Material} to get the names based on
	 * @param nameSuffix the string to be appended to the names (for example "ore" or "block")
	 */
	public static void setRegistryNames(final Block block, final ModMaterials material, final String nameSuffix) {
		final ModResourceLocation registryName = new ModResourceLocation(material.getResouceLocationDomain(nameSuffix.toLowerCase(), ForgeRegistries.BLOCKS), material.getVanillaNameLowercase(nameSuffix) + "_" + nameSuffix);
		block.setHardness(material.getProperties().getHardness());
		setRegistryNames(block, registryName);
	}

	/**
	 * Sets the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl#setRegistryName(net.minecraft.util.ResourceLocation) Registry Name} and the {@link net.minecraft.item.Item#setUnlocalizedName() Unlocalised Name} for the item taking vanilla overriding and vanilla name quirks into account
	 * @param item       the item to set registry names for
	 * @param material   the {@link cadiboo.wiptech.util.ModEnums.ModMaterials Mod Material} to get the names based on
	 * @param nameSuffix the string to be appended to the names (for example "shovel" or "helmet")
	 */
	public static void setRegistryNames(final Item item, final ModMaterials material, final String nameSuffix) {
		final ModResourceLocation registryName = new ModResourceLocation(material.getResouceLocationDomain(nameSuffix.toLowerCase(), ForgeRegistries.ITEMS), material.getVanillaNameLowercase(nameSuffix) + "_" + nameSuffix);
		setRegistryNames(item, registryName);

		final Item overriddenItem = ForgeRegistries.ITEMS.getValue(registryName);
		if (overriddenItem != null) {
			item.setUnlocalizedName(overriddenItem.getUnlocalizedName().replace("item.", ""));
		}
	}

	/**
	 * Sets the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl#setRegistryName(net.minecraft.util.ResourceLocation) Registry Name} and the {@link net.minecraft.item.Item#setUnlocalizedName() Unlocalised Name} (if applicable) for the entry
	 * @param entry the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl IForgeRegistryEntry.Impl<?>} to set the names for
	 * @param name  the name for the entry that the registry name is derived from
	 */
	public static void setRegistryNames(final IForgeRegistryEntry.Impl<?> entry, final String name) {
		setRegistryNames(entry, new ModResourceLocation(ModReference.MOD_ID, name));
	}

	/**
	 * Sets the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl#setRegistryName(net.minecraft.util.ResourceLocation) Registry Name} and the {@link net.minecraft.item.Item#setUnlocalizedName() Unlocalised Name} (if applicable) for the entry
	 * @param entry        the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl IForgeRegistryEntry.Impl<?>} to set the names for
	 * @param registryName the registry name for the entry that the unlocalised name is also gotten from
	 */
	public static void setRegistryNames(final IForgeRegistryEntry.Impl<?> entry, final ModResourceLocation registryName) {
		setRegistryNames(entry, registryName, registryName.getResourcePath());
	}

	/**
	 * Sets the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl#setRegistryName(net.minecraft.util.ResourceLocation) Registry Name} and the {@link net.minecraft.item.Item#setUnlocalizedName() Unlocalised Name} (if applicable) for the entry
	 * @param entry           the {@link net.minecraftforge.registries.IForgeRegistryEntry.Impl IForgeRegistryEntry.Impl<?>} to set the names for
	 * @param registryName    the registry name for the entry
	 * @param unlocalizedName the unlocalized name for the entry
	 */
	public static void setRegistryNames(final IForgeRegistryEntry.Impl<?> entry, final ModResourceLocation registryName, final String unlocalizedName) {
		entry.setRegistryName(registryName);
		if (entry instanceof Block) {
			((Block) entry).setUnlocalizedName(unlocalizedName);
			((Block) entry).setHardness(1);
		}
		if (entry instanceof Item) {
			((Item) entry).setUnlocalizedName(unlocalizedName);
			setCreativeTab((Item) entry);
		}
	}

	/**
	 * Gets the game name from a slot<br>
	 * For example {@link net.minecraft.inventory.EntityEquipmentSlot.CHEST EntityEquipmentSlot.CHEST} -> "CHESTPLATE"
	 * @param slot the {@link net.minecraft.inventory.EntityEquipmentSlot EntityEquipmentSlot} to get the name for
	 * @return the game name for the slot
	 */
	public static String getSlotGameNameUppercase(final EntityEquipmentSlot slot) {
		switch (slot) {
		case CHEST:
			return "CHESTPLATE";
		case FEET:
			return "BOOTS";
		case HEAD:
			return "HELMET";
		case LEGS:
			return "LEGGINGS";
		default:
			return slot.name().toUpperCase();
		}
	}

	/**
	 * Converts the game name to lowercase as per {@link java.lang.String#toLowerCase() String.toLowerCase}.
	 */
	public static String getSlotGameNameLowercase(final EntityEquipmentSlot slot) {
		return getSlotGameNameUppercase(slot).toLowerCase();
	}

	/**
	 * Capitalizes the game name as per {@link org.apache.commons.lang3.StringUtils#capitalize(String) StringUtils.capitalize}.
	 */
	public static String getSlotGameNameFormatted(final EntityEquipmentSlot slot) {
		return StringUtils.capitalize(getSlotGameNameLowercase(slot));
	}

	/**
	 * Utility method to make sure that all our items appear on our creative tab, the search tab and any other tab they specify
	 * @param item the {@link net.minecraft.item.Item Item}
	 * @return an array of all tabs that this item is on.
	 */
	public static CreativeTabs[] getCreativeTabs(final Item item) {
		return new CreativeTabs[] { item.getCreativeTab(), ModCreativeTabs.CREATIVE_TAB, CreativeTabs.SEARCH };
	}

	/**
	 * Utility method to make sure that all our items appear on our creative tab
	 * @param item the {@link net.minecraft.item.Item Item}
	 */
	public static void setCreativeTab(final Item item) {
		if (item.getCreativeTab() == null) {
			item.setCreativeTab(ModCreativeTabs.CREATIVE_TAB);
		}
	}

	/**
	 * Utility method allowing centralized control of glowing material ores, blocks etc. Helpful for debugging
	 * @param material the {@link cadiboo.wiptech.util.ModEnums.ModMaterials Mod Material}
	 * @return a light value corresponding to the {@link cadiboo.wiptech.util.ModEnums.ModMaterials material}
	 */
	public static int getMaterialLightValue(final ModMaterials material) {
		if (ModReference.Debug.debugOres()) {
			return 14;
		}
		switch (material) {
		case PLUTONIUM:
			return 6;
		case URANIUM:
			return 8;
		case GLITCH:
			return Math.round(Math.round(ModUtil.map(0, 1, 0, 15, new Random().nextDouble())));
		default:
			return 0;
		}
	}

	/**
	 * Utility method allowing centralized control of glowing material ores, blocks etc. Helpful for debugging
	 * @param material the {@link cadiboo.wiptech.util.ModEnums.ModMaterials Mod Material}
	 * @return a light opacity value corresponding to the {@link cadiboo.wiptech.util.ModEnums.ModMaterials material}
	 */
	public static int getMaterialLightOpacity(final ModMaterials material) {
		if (ModReference.Debug.debugOres()) {
			return 1;
		}
		switch (material) {
		case PLUTONIUM:
			return 9;
		case URANIUM:
			return 7;
		case GLITCH:
			return Math.round(Math.round(ModUtil.map(0, 1, 0, 15, new Random().nextDouble())));
		default:
			return 0;
		}
	}

	/**
	 * https://stackoverflow.com/a/5732117
	 * @param input_start
	 * @param input_end
	 * @param output_start
	 * @param output_end
	 * @param input
	 * @return
	 */
	public static double map(final double input_start, final double input_end, final double output_start, final double output_end, final double input) {
		final double input_range = input_end - input_start;
		final double output_range = output_end - output_start;

		return (((input - input_start) * output_range) / input_range) + output_start;
	}

	/**
	 * Examples:<br>
	 * (TileEntitySuperAdvancedFurnace, "TileEntity") -> super_advanced_furnace<br>
	 * (EntityPortableGenerator, "Entity") -> portable_generator<br>
	 * (TileEntityPortableGenerator, "Entity") -> tile_portable_generator<br>
	 * (EntityPortableEntityGeneratorEntity, "Entity") -> portable_generator<br>
	 * @param clazz      the class
	 * @param removeType the string to be removed from the class's name
	 * @return the recommended registry name for the class
	 */
	public static String getRegistryNameForClass(final Class clazz, final String removeType) {
		return org.apache.commons.lang3.StringUtils.uncapitalize(clazz.getSimpleName().replace(removeType, "")).replaceAll("([A-Z])", "_$1").toLowerCase();
	}

	/**
	 * Examples:<br>
	 * super_advanced_furnace -> Super Advanced Furnace<br>
	 * portable_generator -> Portable Generator<br>
	 * tile_portable_generator -> Tile Portable Generator <br>
	 * @param unlocalised the unlocalised name in
	 * @return the recommended localised name for the class
	 */
	public static String getLocalisedName(final String unlocalised) {
		final String[] strs = unlocalised.split("_");
		for (int i = 0; i < strs.length; i++) {
			strs[i] = org.apache.commons.lang3.StringUtils.capitalize(strs[i]);
		}
		final String localisedName = String.join(" ", strs);
		return localisedName;
	}

	/**
	 * Generic & dynamic version of {@link Container#transferStackInSlot(EntityPlayer, int)}<br>
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
	 * @param player
	 * @param index
	 * @param container the container to apply the transfer to
	 * @return
	 */
	public static ItemStack transferStackInSlot(final EntityPlayer player, final int index, final Container container) {
		ItemStack itemstack = ItemStack.EMPTY;
		final Slot slot = container.inventorySlots.get(index);
		if ((slot != null) && slot.getHasStack()) {
			final ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			final int containerSlots = container.inventorySlots.size() - player.inventory.mainInventory.size();
			if (index < containerSlots) {
				if (!mergeItemStack(itemstack1, containerSlots, container.inventorySlots.size(), true, container)) {
					return ItemStack.EMPTY;
				}
			} else if (!mergeItemStack(itemstack1, 0, containerSlots, false, container)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}

	/**
	 * Exact copy of {@link net.minecraft.inventory.Container#mergeItemStack} with the same javadoc (improved for readability)<br>
	 * Merges provided ItemStack with the first available one in the container/player inventor between startIndex (included) and endIndex (excluded).<br>
	 * <font color="#FDCA42"> ⚠WARNING⚠: The Container implementation does not check if the item is valid for the slot! </font>
	 * @param stack            the stack to merge
	 * @param startIndex
	 * @param endIndex
	 * @param reverseDirection
	 * @param container        the container to apply the merge to
	 */
	public static boolean mergeItemStack(final ItemStack stack, final int startIndex, final int endIndex, final boolean reverseDirection, final Container container) {
		boolean flag = false;
		int i = startIndex;

		if (reverseDirection) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			while (!stack.isEmpty()) {
				if (reverseDirection) {
					if (i < startIndex) {
						break;
					}
				} else if (i >= endIndex) {
					break;
				}

				final Slot slot = container.inventorySlots.get(i);
				final ItemStack itemstack = slot.getStack();

				if (!itemstack.isEmpty() && (itemstack.getItem() == stack.getItem()) && (!stack.getHasSubtypes() || (stack.getMetadata() == itemstack.getMetadata())) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
					final int j = itemstack.getCount() + stack.getCount();
					final int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

					if (j <= maxSize) {
						stack.setCount(0);
						itemstack.setCount(j);
						slot.onSlotChanged();
						flag = true;
					} else if (itemstack.getCount() < maxSize) {
						stack.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.onSlotChanged();
						flag = true;
					}
				}

				if (reverseDirection) {
					--i;
				} else {
					++i;
				}
			}
		}

		if (!stack.isEmpty()) {
			if (reverseDirection) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (true) {
				if (reverseDirection) {
					if (i < startIndex) {
						break;
					}
				} else if (i >= endIndex) {
					break;
				}

				final Slot slot1 = container.inventorySlots.get(i);
				final ItemStack itemstack1 = slot1.getStack();

				if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
					if (stack.getCount() > slot1.getSlotStackLimit()) {
						slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
					} else {
						slot1.putStack(stack.splitStack(stack.getCount()));
					}

					slot1.onSlotChanged();
					flag = true;
					break;
				}

				if (reverseDirection) {
					--i;
				} else {
					++i;
				}
			}
		}

		return flag;
	}

	public static int[] splitIntoParts(final int whole, final int parts) {
		final int[] arr = new int[parts];
		int remain = whole;
		int partsLeft = parts;
		for (int i = 0; partsLeft > 0; i++) {
			final int size = ((remain + partsLeft) - 1) / partsLeft; // rounded up, aka ceiling
			arr[i] = size;
			remain -= size;
			partsLeft--;
		}
		return arr;
	}

}
