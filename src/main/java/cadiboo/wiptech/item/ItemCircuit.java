package cadiboo.wiptech.item;

import cadiboo.wiptech.util.ModEnums.AttachmentPoints;
import cadiboo.wiptech.util.ModEnums.CircuitTypes;
import cadiboo.wiptech.util.ModUtil;
import net.minecraft.item.Item;

/**
 * @author Cadiboo
 */
public class ItemCircuit extends Item implements IItemAttachment, IModItem {

	private final CircuitTypes type;

	public ItemCircuit(final String name, final CircuitTypes type) {
		ModUtil.setRegistryNames(this, type.getNameLowercase() + "_" + name);
		this.type = type;
	}

	public CircuitTypes getType() {
		return this.type;
	}

	@Override
	public AttachmentPoints getAttachmentPoint() {
		return AttachmentPoints.CIRCUIT;
	}

}
