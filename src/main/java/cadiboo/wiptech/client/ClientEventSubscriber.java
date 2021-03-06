package cadiboo.wiptech.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import cadiboo.wiptech.WIPTech;
import cadiboo.wiptech.block.BlockEnamel;
import cadiboo.wiptech.block.BlockWire;
import cadiboo.wiptech.capability.attachments.AttachmentList;
import cadiboo.wiptech.capability.attachments.CapabilityAttachmentList;
import cadiboo.wiptech.capability.energy.network.CapabilityEnergyNetworkList;
import cadiboo.wiptech.capability.energy.network.EnergyNetwork;
import cadiboo.wiptech.capability.energy.network.EnergyNetworkList;
import cadiboo.wiptech.client.model.ModelsCache;
import cadiboo.wiptech.client.render.entity.EntityCoilgunBulletRenderer;
import cadiboo.wiptech.client.render.entity.EntityNapalmRenderer;
import cadiboo.wiptech.client.render.entity.EntityPortableGeneratorRenderer;
import cadiboo.wiptech.client.render.entity.EntityRailgunRenderer;
import cadiboo.wiptech.client.render.entity.EntitySlugCasingRenderer;
import cadiboo.wiptech.client.render.entity.EntitySlugRenderer;
import cadiboo.wiptech.client.render.tileentity.TileEntityAssemblyTableRenderer;
import cadiboo.wiptech.client.render.tileentity.TileEntityEnamelRenderer;
import cadiboo.wiptech.client.render.tileentity.TileEntityModFurnaceRenderer;
import cadiboo.wiptech.client.render.tileentity.TileEntityWireRenderer;
import cadiboo.wiptech.entity.item.EntityPortableGenerator;
import cadiboo.wiptech.entity.item.EntityRailgun;
import cadiboo.wiptech.entity.projectile.EntityCoilgunBullet;
import cadiboo.wiptech.entity.projectile.EntityNapalm;
import cadiboo.wiptech.entity.projectile.EntitySlug;
import cadiboo.wiptech.entity.projectile.EntitySlugCasing;
import cadiboo.wiptech.init.ModBlocks;
import cadiboo.wiptech.init.ModItems;
import cadiboo.wiptech.item.IItemAttachment;
import cadiboo.wiptech.item.ItemCoil;
import cadiboo.wiptech.item.ItemRail;
import cadiboo.wiptech.tileentity.TileEntityAssemblyTable;
import cadiboo.wiptech.tileentity.TileEntityEnamel;
import cadiboo.wiptech.tileentity.TileEntityModFurnace;
import cadiboo.wiptech.tileentity.TileEntityWire;
import cadiboo.wiptech.util.ExistsForDebugging;
import cadiboo.wiptech.util.ModEnums.AttachmentPoint;
import cadiboo.wiptech.util.ModEnums.CircuitType;
import cadiboo.wiptech.util.ModEnums.ScopeType;
import cadiboo.wiptech.util.ModReference;
import cadiboo.wiptech.util.ModWritingUtil;
import cadiboo.wiptech.util.resourcelocation.ModResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ModReference.MOD_ID)
public final class ClientEventSubscriber {

	public static final String	DEFAULT_VARIANT		= "normal";
	@Deprecated
	public static final String	INVENTORY_VARIANT	= "inventory";

	@SubscribeEvent
	public static void onRegisterModelsEvent(final ModelRegistryEvent event) {

		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(ModelsCache.INSTANCE);
		WIPTech.info("Registered resource manager reload listener for " + ModelsCache.class.getSimpleName());

		registerTileEntitySpecialRenderers();
		WIPTech.info("Registered tile entity special renderers");

		registerEntityRenderers();
		WIPTech.info("Registered entity renderers");

		registerModelsForAttachments();

		/* item blocks */
		registerItemBlockModel(ModBlocks.MOD_FURNACE, "facing=north");
		registerItemBlockModel(ModBlocks.ASSEMBLY_TABLE);

		/* items */
		registerItemModel(ModItems.PORTABLE_GENERATOR);
		registerItemModel(ModItems.FLAMETHROWER);
		registerItemModel(ModItems.RAILGUN);
		registerItemModel(ModItems.SLUG_CASING_BACK);
		registerItemModel(ModItems.SLUG_CASING_TOP);
		registerItemModel(ModItems.SLUG_CASING_BOTTOM);

		registerItemModel(ModItems.HANDHELD_RAILGUN);
		registerItemModel(ModItems.HANDHELD_COILGUN);
		registerItemModel(ModItems.HANDHELD_PLASMAGUN);

		registerItemModel(ModItems.ELECTRIC_SHIELD);

		WIPTech.info("Registered models");

	}

	private static void registerTileEntitySpecialRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWire.class, new TileEntityWireRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnamel.class, new TileEntityEnamelRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityModFurnace.class, new TileEntityModFurnaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAssemblyTable.class, new TileEntityAssemblyTableRenderer());
	}

	private static void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityPortableGenerator.class, EntityPortableGeneratorRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySlugCasing.class, EntitySlugCasingRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySlug.class, EntitySlugRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRailgun.class, EntityRailgunRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityNapalm.class, EntityNapalmRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityCoilgunBullet.class, EntityCoilgunBulletRenderer::new);
	}

	private static void registerModelsForAttachments() {

		for (final CircuitType type : CircuitType.values()) {
			registerItemModel(type.getItem("circuit"));
		}

		for (final ScopeType type : ScopeType.values()) {
			registerItemModel(type.getItem("scope"));
		}

		registerItemModel(ModItems.SHOTGUN);
		registerItemModel(ModItems.GRENADE_LAUNCHER);

		registerItemModel(ModItems.HEARTBEAT_SENSOR);
		registerItemModel(ModItems.LASER);

	}

	private static void registerItemBlockModel(final Block block) {
		registerItemBlockModel(block, DEFAULT_VARIANT);
	}

	private static void registerItemBlockModel(final Block block, final String variant) {
		registerItemModel(Item.getItemFromBlock(block), variant);
	}

	private static void registerItemModel(final Item item) {
		registerItemModel(item, DEFAULT_VARIANT);
	}

	private static void registerItemModel(final Item item, final String variant) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), variant));
	}

	@SubscribeEvent
	public static void onModelBakeEvent(final ModelBakeEvent event) {
		final IRegistry<ModelResourceLocation, IBakedModel> registry = event.getModelRegistry();

		injectModels(registry);
		WIPTech.info("Injected models");

	}

	private static void injectModels(final IRegistry<ModelResourceLocation, IBakedModel> registry) {
		final HashSet<ModResourceLocation> modelLocations = new HashSet<>();

		modelLocations.add(new ModResourceLocation(ModReference.MOD_ID, "entity/portable_generator_handle"));
		modelLocations.add(new ModResourceLocation(ModReference.MOD_ID, "entity/portable_generator_wheel"));
		modelLocations.add(new ModResourceLocation(ModReference.MOD_ID, "entity/railgun_base"));
		modelLocations.add(new ModResourceLocation(ModReference.MOD_ID, "entity/railgun_turret"));
		modelLocations.add(new ModResourceLocation(ModReference.MOD_ID, "entity/railgun_gun"));
		// models.add(new ModResourceLocation(ModReference.MOD_ID, "entity/slug_casing_back"));
		// models.add(new ModResourceLocation(ModReference.MOD_ID, "entity/slug_casing_top"));
		// models.add(new ModResourceLocation(ModReference.MOD_ID, "entity/slug_casing_bottom"));

		for (final ModResourceLocation modelLocation : modelLocations) {
			try {
				/* modified from code made by Draco18s */
				final ModelResourceLocation location = new ModelResourceLocation(modelLocation.toString());

				final IBakedModel bakedModel = ModelsCache.INSTANCE.getBakedModel(modelLocation);

				registry.putObject(location, bakedModel);
				WIPTech.debug("Sucessfully injected " + modelLocation.toString() + " into Model Registry");
			} catch (final Exception e) {
				WIPTech.error("Error injecting model " + modelLocation.toString() + " into Model Registry");
			}
		}
	}

	@SubscribeEvent
	public static void onRenderWorldLast(final RenderWorldLastEvent event) {

		final World world = Minecraft.getMinecraft().world;
		if (world == null) {
			return;
		}

		renderEnergyNetworks(world, event.getPartialTicks());

	}

	private static void renderEnergyNetworks(final World world, final float partialTicks) {

		// if (!ModReference.Debug.debugEnergyNetworks()) {
		// return;
		// }

		final ItemStack check = Minecraft.getMinecraft().player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

		if (check.isEmpty()) {
			return;
		}

		final Block block = Block.getBlockFromItem(check.getItem());

		if (!(block instanceof BlockWire)) {
			return;
		}

		if (world == null) {
			return;
		}

		final EnergyNetworkList list = world.getCapability(CapabilityEnergyNetworkList.NETWORK_LIST, null);
		if (list == null) {
			return;
		}
		GlStateManager.enableBlend();

		// Usually the player
		final Entity entity = Minecraft.getMinecraft().getRenderViewEntity();

		// Interpolating everything back to 0,0,0. These are transforms you can find at RenderEntity class
		final double d0 = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
		final double d1 = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
		final double d2 = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);

		// Apply 0-our transforms to set everything back to 0,0,0
		Tessellator.getInstance().getBuffer().setTranslation(-d0, -d1, -d2);

		for (final EnergyNetwork network : list.getNetworks()) {
			final Random rand = new Random(network.hashCode());

			GlStateManager.color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 0.6f);
			for (final BlockPos pos : network.getConnections()) {

				// our positions
				final int sX = pos.getX();
				final int sY = pos.getY();
				final int sZ = pos.getZ();

				// bind our texture
				Minecraft.getMinecraft().getTextureManager().bindTexture(new ModResourceLocation(ModReference.MOD_ID, "textures/misc/circuits.png"));

				if (pos.distanceSq(d0, d1, d2) > Math.pow(5, 2)) {
					Minecraft.getMinecraft().getTextureManager().getTexture(new ModResourceLocation(ModReference.MOD_ID, "textures/misc/circuits.png")).setBlurMipmap(true, false);
				} else {
					Minecraft.getMinecraft().getTextureManager().getTexture(new ModResourceLocation(ModReference.MOD_ID, "textures/misc/circuits.png")).setBlurMipmap(false, false);
				}

				// actual render function
				ClientUtil.drawCuboidAt(sX + 0.5, sY + 0.5, sZ + 0.5, 0, 1, 0, 1, 0.5, 0.5, 0.5, 1);

			}

		}
		// When you are done rendering all your boxes reset the offsets. We do not want everything that renders next to still be at 0,0,0 :)
		Tessellator.getInstance().getBuffer().setTranslation(0, 0, 0);

		GlStateManager.disableBlend();

	}

	@SubscribeEvent
	public static void onRenderGameOverlay(final RenderGameOverlayEvent.Post event) {
		if ((event.getType() != RenderGameOverlayEvent.ElementType.ALL) || (Minecraft.getMinecraft().currentScreen != null)) {
			return;
		}

		renderEnergyOverlay(event.getPartialTicks());

	}

	private static void renderEnergyOverlay(final float partialTicks) {
		IEnergyStorage energy = null;

		final Minecraft mc = Minecraft.getMinecraft();
		final RayTraceResult rayTraceResult = mc.objectMouseOver;
		mc.entityRenderer.getMouseOver(partialTicks);

		if ((energy == null) && (mc.getRenderViewEntity().getRidingEntity() != null)) {
			if (mc.getRenderViewEntity().getRidingEntity().hasCapability(CapabilityEnergy.ENERGY, null)) {
				energy = mc.getRenderViewEntity().getRidingEntity().getCapability(CapabilityEnergy.ENERGY, null);
			}
		}
		if ((energy == null) && (rayTraceResult != null) && (rayTraceResult.getBlockPos() != null)) {
			final TileEntity tileHit = mc.world.getTileEntity(rayTraceResult.getBlockPos());
			if (tileHit != null) {
				if (tileHit.hasCapability(CapabilityEnergy.ENERGY, rayTraceResult.sideHit)) {
					energy = tileHit.getCapability(CapabilityEnergy.ENERGY, rayTraceResult.sideHit);
				}
			}
		}
		if ((energy == null) && (rayTraceResult != null) && (rayTraceResult.entityHit != null)) {
			if (rayTraceResult.entityHit.hasCapability(CapabilityEnergy.ENERGY, null)) {
				energy = rayTraceResult.entityHit.getCapability(CapabilityEnergy.ENERGY, null);
			}
		}

		if (energy == null) {
			return;
		}

		final double power = (double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored();
		final int scaled_height = (int) Math.round((1 - power) * 52D);
		final ScaledResolution Scaled = new ScaledResolution(Minecraft.getMinecraft());
		final int Width = Scaled.getScaledWidth() - 10;
		final int Height = Scaled.getScaledHeight() - 54;

		mc.getTextureManager().bindTexture(new ModResourceLocation(ModReference.MOD_ID, "textures/gui/energy.png"));

		ClientUtil.drawNonStandardTexturedRect(Width, Height, 0, 0, 10, 54, 256, 256);
		ClientUtil.drawNonStandardTexturedRect(Width + 1, Height + 1 + scaled_height, 10, 0, 8, 52 - scaled_height, 256, 256);
		final int percent = (int) Math.round(power * 100);
		mc.fontRenderer.drawStringWithShadow(percent + "%", Width - 7 - (String.valueOf(percent).length() * 6), Height + 35, 0xFFFFFF);
		final String outOf = energy.getEnergyStored() + "/" + energy.getMaxEnergyStored();
		mc.fontRenderer.drawStringWithShadow(outOf, Width - 1 - (outOf.length() * 6), Height + 45, 0xFFFFFF);

	}

	@SubscribeEvent
	public static void drawBlockHighlightEvent(final DrawBlockHighlightEvent event) {
		try {
			final EntityPlayer player = event.getPlayer();
			if (player == null) {
				return;
			}

			final RayTraceResult rayTraceResult = event.getTarget();
			if ((rayTraceResult == null) || (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK)) {
				return;
			}

			final World world = player.world;
			if (world == null) {
				return;
			}

			final float partialTicks = event.getPartialTicks();
			final BlockPos pos = rayTraceResult.getBlockPos();
			final IBlockState blockState = world.getBlockState(pos);
			if ((blockState.getMaterial() == Material.AIR) || !world.getWorldBorder().contains(pos)) {
				return;
			}

			final Block block = blockState.getBlock();

			if (!(block instanceof BlockWire) && !(block instanceof BlockEnamel) && !ModReference.Debug.debugBoundingBoxes() && !ModReference.Debug.debugCollisionBoxes()) {
				return;
			}

			event.setCanceled(true);

			final AxisAlignedBB oldSelectedBox = blockState.getSelectedBoundingBox(world, pos);

			final List<AxisAlignedBB> boxes = new ArrayList<>();

			blockState.addCollisionBoxToList(world, pos, new AxisAlignedBB(pos), boxes, player, false);

			if (boxes.size() <= 1) {
				boxes.clear();
				boxes.add(oldSelectedBox);
			}

			final double renderX = player.lastTickPosX + ((player.posX - player.lastTickPosX) * partialTicks);
			final double renderY = player.lastTickPosY + ((player.posY - player.lastTickPosY) * partialTicks);
			final double renderZ = player.lastTickPosZ + ((player.posZ - player.lastTickPosZ) * partialTicks);

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);

			for (AxisAlignedBB box : boxes) {
				if ((block instanceof BlockWire) && box.equals(BlockWire.CORE_AABB.offset(pos)) && !ModReference.Debug.debugCollisionBoxes()) {
					continue;
				}
				if ((block instanceof BlockEnamel) && box.equals(BlockEnamel.CORE_AABB.offset(pos)) && !ModReference.Debug.debugCollisionBoxes()) {
					continue;
				}

				if (!ModReference.Debug.debugCollisionBoxes()) {
					if (box.maxY > (box.minY + 1)) {
						box = box.setMaxY(oldSelectedBox.maxY);
					}
				}

				final AxisAlignedBB renderBox = box.grow(0.0020000000949949026D).offset(-renderX, -renderY, -renderZ);

				if (ModReference.Debug.debugCollisionBoxes()) {
					event.getContext().drawSelectionBoundingBox(renderBox, 1.0F, 0.0F, 0.0F, 0.4F);
					continue;
				}

				if (ModReference.Debug.debugBoundingBoxes()) {
					event.getContext().drawSelectionBoundingBox(renderBox, 0.0F, 1.0F, 1.0F, 0.4F);
					continue;
				}
				event.getContext().drawSelectionBoundingBox(renderBox, 0.0F, 0.0F, 0.0F, 0.4F);

			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		} catch (final Exception e) {
			event.setCanceled(false);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onTooltipEvent(final ItemTooltipEvent event) {

		final Item item = event.getItemStack().getItem();

		if (item.getHorseArmorType(event.getItemStack()) != HorseArmorType.NONE) {
			final HorseArmorType armorType = item.getHorseArmorType(event.getItemStack());

			setTooltip(event, TextFormatting.RESET + "");

			setTooltip(event, WIPTech.proxy.localize("item.modifiers.horse") + ": ");

			final int protection = armorType.getProtection();

			if (protection > 0) {
				setTooltip(event, TextFormatting.BLUE + " " + WIPTech.proxy.localizeAndFormat("attribute.modifier.plus.0", protection, WIPTech.proxy.localize("enchantment.protect.all")));
			} else if (protection < 0) {
				setTooltip(event, TextFormatting.RED + " " + WIPTech.proxy.localizeAndFormat("attribute.modifier.take.0", protection, WIPTech.proxy.localize("enchantment.protect.all")));
			}

		}

		if (!item.getRegistryName().getNamespace().equals(ModReference.MOD_ID)) {
			return;
		}

		if (item instanceof ItemCoil) {
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((ItemCoil) item).getModMaterial().getProperties().getConductivity() + "");
		}

		if (item instanceof ItemRail) {
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((ItemRail) item).getModMaterial().getProperties().getConductivity() + "");
		}

		if ((Block.getBlockFromItem(item) instanceof BlockWire) && !(Block.getBlockFromItem(item) instanceof BlockEnamel)) {
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((BlockWire) Block.getBlockFromItem(item)).getModMaterial().getProperties().getConductivity() + "");
			setTooltip(event, WIPTech.proxy.localize("Ouch! Put some insulation around it"));
		}

		if (Block.getBlockFromItem(item) instanceof BlockEnamel) {
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((BlockEnamel) Block.getBlockFromItem(item)).getModMaterial().getProperties().getConductivity() + "");
		}

		if (item instanceof IItemAttachment) {
			setTooltip(event, WIPTech.proxy.localize("attachmentpoint") + ": " + WIPTech.proxy.localize(((IItemAttachment) item).getAttachmentPoint().getUnlocalizedName() + ".name"));
		}

		final AttachmentList attachmentList = event.getItemStack().getCapability(CapabilityAttachmentList.ATTACHMENT_LIST, null);
		if (attachmentList != null) {

			boolean isEmpty = true;
			for (final AttachmentPoint attachmentPoint : attachmentList.getPoints()) {
				if (!isEmpty) {
					continue;
				}
				final ItemStack attachmentStack = attachmentList.getAttachment(attachmentPoint);
				if (attachmentStack.isEmpty()) {
					continue;
				}
				isEmpty = false;
			}

			if (!isEmpty) {
				setTooltip(event, TextFormatting.RESET + "");
				setTooltip(event, WIPTech.proxy.localize("attachments") + ":");
				for (final AttachmentPoint attachmentPoint : attachmentList.getPoints()) {
					final ItemStack attachmentStack = attachmentList.getAttachment(attachmentPoint);

					String value = WIPTech.proxy.localize(attachmentStack.getTranslationKey() + ".name");

					if (attachmentStack.isEmpty()) {
						if (Boolean.valueOf(true)) {
							continue;
						}
						value = WIPTech.proxy.localize("empty");
					}
					final String tooltip = WIPTech.proxy.localize(attachmentPoint.getUnlocalizedName() + ".name") + ": " + value;

					setTooltip(event, TextFormatting.BLUE + " " + tooltip);
				}
			}
		}

		// setTooltip(event, event.getItemStack().serializeNBT().toString());

	}

	private static void setTooltip(final ItemTooltipEvent event, final String tooltip) {

		for (int index = 0; index < event.getToolTip().size(); index++) {

			final String line = event.getToolTip().get(index);
			final String check = net.minecraft.util.StringUtils.stripControlCodes(line);
			final String registryName = event.getItemStack().getItem().getRegistryName().toString();

			if (check.equals(registryName)) { // TODO why? and what does this do???
				event.getToolTip().add(index, tooltip);
				return;
			}
		}
		event.getToolTip().add(tooltip);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@ExistsForDebugging
	public static void writeMod(final ModelBakeEvent event) {

		ModWritingUtil.writeMod();

	}

}