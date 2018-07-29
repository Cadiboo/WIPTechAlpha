package cadiboo.wiptech.item;

import cadiboo.wiptech.util.ModEnums.ModMaterials;
import cadiboo.wiptech.util.ModUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;

public class ItemModPickaxe extends ItemPickaxe {

	public ItemModPickaxe(ModMaterials materialIn) {

		super(materialIn.getToolMaterial());
		ModUtil.setNameForMaterialItem(this, materialIn, "pickaxe");
		ModUtil.setCreativeTab(this);

	}

	@Override
	public CreativeTabs[] getCreativeTabs() {
		return ModUtil.getCreativeTabs(this);
	}

}
