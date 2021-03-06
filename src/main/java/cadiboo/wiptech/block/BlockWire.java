package cadiboo.wiptech.block;

import java.util.List;

import cadiboo.wiptech.capability.energy.network.CapabilityEnergyNetworkList;
import cadiboo.wiptech.material.ModMaterial;
import cadiboo.wiptech.tileentity.TileEntityWire;
import cadiboo.wiptech.util.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

/**
 * Transmits energy, has a custom baked model, is awesome
 * 
 * @author cadigal
 */
public class BlockWire extends Block implements IModBlock, IBlockModMaterial {

	private static final int	format__0	= 0;
	private static final int	format__1	= 1;

	public static final AxisAlignedBB	CORE_AABB	= new AxisAlignedBB(07d / 16d, 07d / 16d, 07d / 16d, 09d / 16d, 09d / 16d, 09d / 16d);
	public static final AxisAlignedBB	UP_AABB		= new AxisAlignedBB(07d / 16d, 07d / 16d, 07d / 16d, 09d / 16d, format__1, 09d / 16d);
	public static final AxisAlignedBB	DOWN_AABB	= new AxisAlignedBB(07d / 16d, format__0, 07d / 16d, 09d / 16d, 09d / 16d, 09d / 16d);
	public static final AxisAlignedBB	NORTH_AABB	= new AxisAlignedBB(07d / 16d, 07d / 16d, format__0, 09d / 16d, 09d / 16d, 09d / 16d);
	public static final AxisAlignedBB	SOUTH_AABB	= new AxisAlignedBB(07d / 16d, 07d / 16d, 07d / 16d, 09d / 16d, 09d / 16d, format__1);
	public static final AxisAlignedBB	EAST_AABB	= new AxisAlignedBB(07d / 16d, 07d / 16d, 07d / 16d, format__1, 09d / 16d, 09d / 16d);
	public static final AxisAlignedBB	WEST_AABB	= new AxisAlignedBB(format__0, 07d / 16d, 07d / 16d, 09d / 16d, 09d / 16d, 09d / 16d);

	public static final IUnlistedProperty<Boolean>	CONNECTED_DOWN	= new Properties.PropertyAdapter<>(PropertyBool.create("connected_down"));
	public static final IUnlistedProperty<Boolean>	CONNECTED_UP	= new Properties.PropertyAdapter<>(PropertyBool.create("connected_up"));
	public static final IUnlistedProperty<Boolean>	CONNECTED_NORTH	= new Properties.PropertyAdapter<>(PropertyBool.create("connected_north"));
	public static final IUnlistedProperty<Boolean>	CONNECTED_SOUTH	= new Properties.PropertyAdapter<>(PropertyBool.create("connected_south"));
	public static final IUnlistedProperty<Boolean>	CONNECTED_WEST	= new Properties.PropertyAdapter<>(PropertyBool.create("connected_west"));
	public static final IUnlistedProperty<Boolean>	CONNECTED_EAST	= new Properties.PropertyAdapter<>(PropertyBool.create("connected_east"));

	protected final ModMaterial material;

	public BlockWire(final ModMaterial material) {
		this(material, "wire");
	}

	protected BlockWire(final ModMaterial material, final String nameSuffix) {
		super(Material.CIRCUITS);
		ModUtil.setRegistryNames(this, material, nameSuffix);
		this.material = material;
	}

	@Override
	public final ModMaterial getModMaterial() {
		return this.material;
	}

	@Override
	public int getLightValue(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return ModUtil.getMaterialLightValue(this.getModMaterial());
	}

	@Override
	public int getLightOpacity(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return ModUtil.getMaterialLightOpacity(this.getModMaterial());
	}

	@Override
	public boolean isFullCube(final IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(final IBlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canRenderInLayer(final IBlockState state, final BlockRenderLayer layer) {
		return this.material.getProperties().getBlockRenderLayers().contains(layer) || (this.getRenderLayer() == layer);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(final IBlockAccess world, final IBlockState state, final BlockPos pos, final EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
		final TileEntity tile = source.getTileEntity(pos);
		AxisAlignedBB AABB = BlockWire.CORE_AABB;
		if ((tile != null) && (tile instanceof TileEntityWire)) {
			final TileEntityWire wire = (TileEntityWire) tile;

			if (wire.isConnectedTo(EnumFacing.DOWN)) {
				AABB = AABB.union(DOWN_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.UP)) {
				AABB = AABB.union(UP_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.NORTH)) {
				AABB = AABB.union(NORTH_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.SOUTH)) {
				AABB = AABB.union(SOUTH_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.WEST)) {
				AABB = AABB.union(WEST_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.EAST)) {
				AABB = AABB.union(EAST_AABB);
			}
		}
		return AABB;
	}

	@Override
	public void addCollisionBoxToList(final IBlockState state, final World world, final BlockPos pos, final AxisAlignedBB entityBox, final List<AxisAlignedBB> collidingBoxes, final Entity entity, final boolean isActualState) {
		final TileEntity tile = world.getTileEntity(pos);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockWire.CORE_AABB);
		if ((tile != null) && (tile instanceof TileEntityWire)) {
			final TileEntityWire wire = (TileEntityWire) tile;

			if (wire.isConnectedTo(EnumFacing.DOWN)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockWire.DOWN_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.UP)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockWire.UP_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.NORTH)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockWire.NORTH_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.SOUTH)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockWire.SOUTH_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.WEST)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockWire.WEST_AABB);
			}
			if (wire.isConnectedTo(EnumFacing.EAST)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, BlockWire.EAST_AABB);
			}
		}

	}

	@Override
	public EnumBlockRenderType getRenderType(final IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean hasTileEntity(final IBlockState state) {
		return true;
	}

	@Override
	public TileEntityWire createTileEntity(final World world, final IBlockState state) {
		return new TileEntityWire();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		final IProperty<?>[] listedProperties = new IProperty[0]; /* no listed properties */
		final IUnlistedProperty<?>[] unlistedProperties = new IUnlistedProperty[] { CONNECTED_UP, CONNECTED_DOWN, CONNECTED_EAST, CONNECTED_WEST, CONNECTED_NORTH, CONNECTED_SOUTH };
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}

	@Override
	public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		if (!(state instanceof IExtendedBlockState) || (world.getTileEntity(pos) == null) || !(world.getTileEntity(pos) instanceof TileEntityWire)) {
			return state;
		}

		final TileEntityWire tile = (TileEntityWire) world.getTileEntity(pos);

		IExtendedBlockState retval = (IExtendedBlockState) state;

		retval = retval.withProperty(CONNECTED_DOWN, tile.isConnectedTo(EnumFacing.DOWN));
		retval = retval.withProperty(CONNECTED_UP, tile.isConnectedTo(EnumFacing.UP));
		retval = retval.withProperty(CONNECTED_NORTH, tile.isConnectedTo(EnumFacing.NORTH));
		retval = retval.withProperty(CONNECTED_SOUTH, tile.isConnectedTo(EnumFacing.SOUTH));
		retval = retval.withProperty(CONNECTED_WEST, tile.isConnectedTo(EnumFacing.WEST));
		retval = retval.withProperty(CONNECTED_EAST, tile.isConnectedTo(EnumFacing.EAST));

		return retval;
	}

	@Override
	public void breakBlock(final World world, final BlockPos pos, final IBlockState state) {
		if (world != null) {
			final TileEntity tile = world.getTileEntity(pos);
			if (tile != null) {
				if (tile instanceof TileEntityWire) {
					final TileEntityWire wire = (TileEntityWire) tile;
					world.getCapability(CapabilityEnergyNetworkList.NETWORK_LIST, null).removeConnection(tile.getPos());
				}
			}
		}

		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockExploded(final World world, final BlockPos pos, final Explosion explosion) {
		if (world != null) {
			final TileEntity tile = world.getTileEntity(pos);
			if (tile != null) {
				if (tile instanceof TileEntityWire) {
					final TileEntityWire wire = (TileEntityWire) tile;
					world.getCapability(CapabilityEnergyNetworkList.NETWORK_LIST, null).removeConnection(tile.getPos());
				}
			}
		}
		super.onBlockExploded(world, pos, explosion);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(final IBlockState state, final World world, final BlockPos pos) {
		return this.getBoundingBox(state, world, pos).offset(pos);
	}
}
