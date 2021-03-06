package cadiboo.wiptech.util;

import java.util.List;

import javax.annotation.Nullable;

import cadiboo.wiptech.client.gui.inventory.GuiAssemblyTable;
import cadiboo.wiptech.client.gui.inventory.GuiModFurnace;
import cadiboo.wiptech.client.gui.inventory.GuiPortableGenerator;
import cadiboo.wiptech.client.gui.inventory.GuiRailgun;
import cadiboo.wiptech.entity.item.EntityPortableGenerator;
import cadiboo.wiptech.entity.item.EntityRailgun;
import cadiboo.wiptech.inventory.ContainerAssemblyTable;
import cadiboo.wiptech.inventory.ContainerModFurnace;
import cadiboo.wiptech.inventory.ContainerPortableGenerator;
import cadiboo.wiptech.inventory.ContainerRailgun;
import cadiboo.wiptech.tileentity.TileEntityAssemblyTable;
import cadiboo.wiptech.tileentity.TileEntityModFurnace;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles the opening (creation) of Guis for both the server and client
 * @author Cadiboo
 */
public class ModGuiHandler implements IGuiHandler {

	public static final int	RAILGUN				= 0;
	public static final int	PORTABLE_GENERATOR	= 1;
	public static final int	MOD_FURNACE			= 2;
	public static final int	ASSEMBLY_TABLE		= 3;

	/**
	 * gets the server's part of a Gui
	 * @return a {@link net.minecraft.inventory.Container Container} for the server
	 */
	@Override
	@Nullable
	public Container getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch (ID) {
		case RAILGUN:
			final List<Entity> railguns = world.getEntitiesWithinAABB(EntityRailgun.class, new AxisAlignedBB(new BlockPos(x, y, z)));
			if ((railguns.size() > 0) && (railguns.get(0) != null) && (railguns.get(0) instanceof EntityRailgun)) {
				return new ContainerRailgun(player.inventory, (EntityRailgun) railguns.get(0));
			} else {
				assert false;
				return null;
			}
		case PORTABLE_GENERATOR:
			final List<Entity> portableGenerators = world.getEntitiesWithinAABB(EntityPortableGenerator.class, new AxisAlignedBB(new BlockPos(x, y, z)));
			if ((portableGenerators.size() > 0) && (portableGenerators.get(0) != null) && (portableGenerators.get(0) instanceof EntityPortableGenerator)) {
				return new ContainerPortableGenerator(player.inventory, (EntityPortableGenerator) portableGenerators.get(0));
			} else {
				assert false;
				return null;
			}
		case MOD_FURNACE:
			return new ContainerModFurnace(player.inventory, (TileEntityModFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case ASSEMBLY_TABLE:
			return new ContainerAssemblyTable(player.inventory, (TileEntityAssemblyTable) world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

	/**
	 * gets the client's part of a Gui
	 * @return a {@link net.minecraft.client.gui.GuiScreen GuiScreen} for the client
	 */
	@Override
	@Nullable
	@SideOnly(Side.CLIENT)
	public Gui getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		switch (ID) {
		case RAILGUN:
			final List<Entity> railguns = world.getEntitiesWithinAABB(EntityRailgun.class, new AxisAlignedBB(new BlockPos(x, y, z)));
			if ((railguns.size() > 0) && (railguns.get(0) != null) && (railguns.get(0) instanceof EntityRailgun)) {
				return new GuiRailgun(player.inventory, (EntityRailgun) railguns.get(0));
			} else {
				assert false;
				return null;
			}
		case PORTABLE_GENERATOR:
			final List<Entity> portableGenerators = world.getEntitiesWithinAABB(EntityPortableGenerator.class, new AxisAlignedBB(new BlockPos(x, y, z)));
			if ((portableGenerators.size() > 0) && (portableGenerators.get(0) != null) && (portableGenerators.get(0) instanceof EntityPortableGenerator)) {
				return new GuiPortableGenerator(player.inventory, (EntityPortableGenerator) portableGenerators.get(0));
			} else {
				assert false;
				return null;
			}
		case MOD_FURNACE:
			return new GuiModFurnace(player.inventory, (TileEntityModFurnace) world.getTileEntity(new BlockPos(x, y, z)));
		case ASSEMBLY_TABLE:
			return new GuiAssemblyTable(player.inventory, (TileEntityAssemblyTable) world.getTileEntity(new BlockPos(x, y, z)));
		default:
			return null;
		}
	}

}
