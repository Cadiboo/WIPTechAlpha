package cadiboo.wiptech.capability.attachments;

import java.util.Arrays;
import java.util.HashMap;

import com.google.common.collect.ImmutableSet;

import cadiboo.wiptech.item.IItemAttachment;
import cadiboo.wiptech.util.ModEnums.AttachmentPoint;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class AttachmentList implements INBTSerializable<NBTTagCompound> {

	private final ImmutableSet<AttachmentPoint> attachmentPoints;

	private final HashMap<AttachmentPoint, ItemStack> attachments;

	public AttachmentList(final AttachmentPoint... attachmentPoints) {
		this.attachmentPoints = ImmutableSet.copyOf(Arrays.asList(attachmentPoints));
		this.attachments = new HashMap<>();
		this.attachmentPoints.forEach(point -> {
			this.attachments.put(point, ItemStack.EMPTY);
		});
	}

	public ImmutableSet<AttachmentPoint> getPoints() {
		return this.attachmentPoints;
	}

	public ItemStack getAttachment(final AttachmentPoint attachmentPoint) {
		return this.attachments.get(attachmentPoint);
	}

	/**
	 * Adds a stack to the attachment list
	 * @param attachmentStack the stack to add (the stack's item must implement IItemAttachment)
	 * @return the amount of the stack that wasn't/couldn't be added
	 */
	public ItemStack addAttachment(final ItemStack attachmentStack) {

		if (attachmentStack.isEmpty()) {
			return attachmentStack;
		}

		final Item item = attachmentStack.getItem();
		if (item instanceof IItemAttachment) {
			final IItemAttachment attachmentItem = (IItemAttachment) item;
			final AttachmentPoint attachmentPoint = attachmentItem.getAttachmentPoint();

			if (attachmentPoint != null) {

				if (this.getPoints().contains(attachmentPoint)) {

					final ItemStack currentAttachmentStack = this.getAttachment(attachmentPoint);
					final ItemStack insertAttachmentStack = attachmentStack.copy();

					if (currentAttachmentStack.isEmpty()) {
						insertAttachmentStack.setCount(1);
						this.attachments.put(attachmentPoint, insertAttachmentStack);
						attachmentStack.shrink(1);
						return attachmentStack;
					}

					if (insertAttachmentStack.getCount() == 1) {
						this.attachments.put(attachmentPoint, insertAttachmentStack);
						return currentAttachmentStack;
					}

				}

			}

		}

		return attachmentStack;
	}

	public boolean canAddAttachment(final ItemStack attachmentStack) {
		if (attachmentStack.isEmpty()) {
			return false;
		}

		final Item item = attachmentStack.getItem();
		if (item instanceof IItemAttachment) {
			final IItemAttachment attachmentItem = (IItemAttachment) item;
			final AttachmentPoint attachmentPoint = attachmentItem.getAttachmentPoint();

			if (attachmentPoint != null) {

				if (this.getPoints().contains(attachmentPoint)) {

					final ItemStack currentAttachmentStack = this.getAttachment(attachmentPoint);
					final ItemStack insertAttachmentStack = attachmentStack.copy();

					if (currentAttachmentStack.isEmpty()) {
						return true;
					}

					if (insertAttachmentStack.getCount() == 1) {
						return true;
					}

				}

			}

		}

		return false;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagCompound compound = new NBTTagCompound();
		this.attachments.forEach((point, stack) -> {
			if (stack.isEmpty()) {
				return;
			}
			compound.setTag("" + point.getId(), stack.serializeNBT());
		});
		return compound;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound compound) {
		if (compound == null) {
			return;
		}
		for (final AttachmentPoint point : this.attachments.keySet()) {
			if (compound.hasKey("" + point.getId())) {
				this.attachments.put(point, new ItemStack((NBTTagCompound) compound.getTag("" + point.getId())));
			}
		}
	}

}
