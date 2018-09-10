package cadiboo.wiptech.capability.attachments;

import java.util.Arrays;
import java.util.HashMap;

import com.google.common.collect.ImmutableSet;

import cadiboo.wiptech.WIPTech;
import cadiboo.wiptech.item.IItemAttachment;
import cadiboo.wiptech.util.ModEnums.AttachmentPoints;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AttachmentList {

	private final ImmutableSet<AttachmentPoints> attachmentPoints;

	private final HashMap<AttachmentPoints, ItemStack> attachments;

	public AttachmentList(final AttachmentPoints... attachmentPoints) {
		this.attachmentPoints = ImmutableSet.copyOf(Arrays.asList(attachmentPoints));
		WIPTech.info(this.attachmentPoints.size());
		this.attachments = new HashMap<>();
		this.attachmentPoints.forEach(point -> {
			this.attachments.put(point, ItemStack.EMPTY);
		});
	}

	public ImmutableSet<AttachmentPoints> getPoints() {
		return this.attachmentPoints;
	}

	public ItemStack getAttachment(final AttachmentPoints attachmentPoint) {
		return this.attachments.get(attachmentPoint);
	}

	public ItemStack addAttachment(final ItemStack attachmentStack) {
		final Item item = attachmentStack.getItem();
		if (item instanceof IItemAttachment) {
			final IItemAttachment attachmentItem = (IItemAttachment) item;
			final AttachmentPoints attachmentPoint = attachmentItem.getAttachmentPoint();

			if (attachmentPoint != null) {

				if (this.getPoints().contains(attachmentPoint)) {

					if (!this.getAttachment(attachmentPoint).isItemEqual(attachmentStack)) {

						this.attachments.put(attachmentPoint, attachmentStack);

					}

				}

			}

		}

		return attachmentStack;
	}

	public ItemStack getCircuit() {
		final ItemStack circuit = this.getAttachment(AttachmentPoints.CIRCUIT);
		return circuit;
	}

}
