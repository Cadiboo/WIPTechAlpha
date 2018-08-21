package cadiboo.wiptech;

import org.lwjgl.opengl.GL11;

import cadiboo.wiptech.block.BlockEnamel;
import cadiboo.wiptech.block.BlockItem;
import cadiboo.wiptech.block.BlockModOre;
import cadiboo.wiptech.block.BlockResource;
import cadiboo.wiptech.block.BlockSpool;
import cadiboo.wiptech.block.BlockWire;
import cadiboo.wiptech.client.model.ModelsCache;
import cadiboo.wiptech.client.render.block.model.WireModelLoader;
import cadiboo.wiptech.client.render.entity.EntityNapalmRenderer;
import cadiboo.wiptech.client.render.entity.EntityPortableGeneratorRenderer;
import cadiboo.wiptech.client.render.entity.EntityRailgunRenderer;
import cadiboo.wiptech.client.render.entity.EntitySlugCasingRenderer;
import cadiboo.wiptech.client.render.entity.EntitySlugRenderer;
import cadiboo.wiptech.client.render.tileentity.TileEntityEnamelRenderer;
import cadiboo.wiptech.client.render.tileentity.TileEntityWireRenderer;
import cadiboo.wiptech.entity.item.EntityPortableGenerator;
import cadiboo.wiptech.entity.item.EntityRailgun;
import cadiboo.wiptech.entity.projectile.EntityNapalm;
import cadiboo.wiptech.entity.projectile.EntitySlug;
import cadiboo.wiptech.entity.projectile.EntitySlugCasing;
import cadiboo.wiptech.init.ModEntities;
import cadiboo.wiptech.init.ModItems;
import cadiboo.wiptech.item.ItemCoil;
import cadiboo.wiptech.item.ItemFlamethrower;
import cadiboo.wiptech.item.ItemModArmor;
import cadiboo.wiptech.item.ItemModAxe;
import cadiboo.wiptech.item.ItemModHoe;
import cadiboo.wiptech.item.ItemModPickaxe;
import cadiboo.wiptech.item.ItemModShovel;
import cadiboo.wiptech.item.ItemModSword;
import cadiboo.wiptech.item.ItemPortableGenerator;
import cadiboo.wiptech.item.ItemRail;
import cadiboo.wiptech.item.ItemRailgun;
import cadiboo.wiptech.item.ItemSlug;
import cadiboo.wiptech.item.ModItem;
import cadiboo.wiptech.item.ModItemBlock;
import cadiboo.wiptech.tileentity.TileEntityEnamel;
import cadiboo.wiptech.tileentity.TileEntityWire;
import cadiboo.wiptech.util.ModEnums.BlockItemTypes;
import cadiboo.wiptech.util.ModEnums.ModMaterials;
import cadiboo.wiptech.util.ModReference;
import cadiboo.wiptech.util.ModWritingUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ModReference.ID)
public final class EventSubscriber {

	private static int entityId = 0;

	@SubscribeEvent
	public static final void onRegisterBlocksEvent(final RegistryEvent.Register<Block> event) {
		final IForgeRegistry<Block> registry = event.getRegistry();

		registerBlocksForMaterials(registry);

		WIPTech.debug("registered blocks");

		registerTileEntity(TileEntityWire.class);
		registerTileEntity(TileEntityEnamel.class);

		WIPTech.debug("registered tile entities");

	}

	private static final void registerTileEntity(Class<? extends TileEntity> clazz) {
		GameRegistry.registerTileEntity(clazz, new ResourceLocation(ModReference.ID, getRegistryNameForClass(clazz, "TileEntity")));
	}

	private static final void registerBlocksForMaterials(IForgeRegistry<Block> registry) {
		for (ModMaterials material : ModMaterials.values()) {
			if (material.getProperties().hasOre())
				registry.register(new BlockModOre(material));

			if (material.getProperties().hasBlock())
				registry.register(new BlockResource(material));

			if (material.getProperties().hasIngotAndNugget()) {
				registry.register(new BlockItem(material, BlockItemTypes.INGOT));
				registry.register(new BlockItem(material, BlockItemTypes.NUGGET));
			}

			if (material.getProperties().hasWire()) {

				registry.register(new BlockWire(material));
				registry.register(new BlockSpool(material));
			}

			if (material.getProperties().hasEnamel())
				registry.register(new BlockEnamel(material));

		}
	}

	@SubscribeEvent
	public static final void onRegisterItemsEvent(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();

		registerItemsForMaterials(registry);

		registry.register(new ItemPortableGenerator("portable_generator"));

		registry.register(new ItemFlamethrower("flamethrower"));

		registry.register(new ModItem("slug_casing"));

		registry.register(new ItemRailgun("railgun"));

		WIPTech.debug("registered items");

	}

	private static final void registerItemsForMaterials(IForgeRegistry<Item> registry) {
		for (ModMaterials material : ModMaterials.values()) {
			if (material.getProperties().hasOre())
				registry.register(new ModItemBlock(material.getOre(), new ResourceLocation(material.getResouceLocationDomain("ore", ForgeRegistries.ITEMS), material.getNameLowercase() + "_ore")));

			if (material.getProperties().hasBlock())
				registry.register(new ModItemBlock(material.getBlock(), new ResourceLocation(material.getResouceLocationDomain("block", ForgeRegistries.ITEMS), material.getNameLowercase()
						+ "_block")));

			if (material.getProperties().hasIngotAndNugget()) {
				registry.register(new ModItemBlock(material.getIngot(), new ResourceLocation(material.getResouceLocationDomain("ingot", ForgeRegistries.ITEMS), material.getNameLowercase()
						+ "_ingot")));
				registry.register(new ModItemBlock(material.getNugget(), new ResourceLocation(material.getResouceLocationDomain("nugget", ForgeRegistries.ITEMS), material.getNameLowercase()
						+ "_nugget")));
			}

			if (material.getProperties().hasWire()) {
				registry.register(new ModItemBlock(material.getWire()));
				registry.register(new ModItemBlock(material.getSpool()));
			}

			if (material.getProperties().hasEnamel())
				registry.register(new ModItemBlock(material.getEnamel()));

			if (material.getProperties().hasArmor()) {
				registry.register(new ItemModArmor(material, EntityEquipmentSlot.HEAD));
				registry.register(new ItemModArmor(material, EntityEquipmentSlot.CHEST));
				registry.register(new ItemModArmor(material, EntityEquipmentSlot.LEGS));
				registry.register(new ItemModArmor(material, EntityEquipmentSlot.FEET));
			}

			if (material.getProperties().hasTools()) {
				registry.register(new ItemModPickaxe(material));
				registry.register(new ItemModAxe(material));
				registry.register(new ItemModSword(material));
				registry.register(new ItemModShovel(material));
				registry.register(new ItemModHoe(material));
			}

			if (material.getProperties().hasCoil())
				registry.register(new ItemCoil(material));

			if (material.getProperties().hasRail())
				registry.register(new ItemRail(material));

			if (material.getProperties().hasRailgunSlug())
				registry.register(new ItemSlug(material));

		}

	}

	@SubscribeEvent
	public static final void onRegisterEntitiesEvent(final RegistryEvent.Register<EntityEntry> event) {

		event.getRegistry().register(buildEntityEntryFromClass(EntityPortableGenerator.class, false, 64, 20, true));

		event.getRegistry().register(buildEntityEntryFromClass(EntityRailgun.class, false, 64, 10, true));

		event.getRegistry().register(buildEntityEntryFromClass(EntitySlugCasing.class, false, 128, 2, true));

		event.getRegistry().register(buildEntityEntryFromClass(EntityNapalm.class, false, 128, 5, true));

		// TODO AdditionalSpawnData

		for (final ModMaterials material : ModMaterials.values())
			if (material.getProperties().hasRailgunSlug())
				event.getRegistry().register(buildEntityEntryFromClassWithName(EntitySlug.class, new ResourceLocation(ModReference.ID, material.getNameLowercase() + "_slug"), false, 128, 5, true));

	}

	private static final EntityEntry buildEntityEntryFromClass(final Class<? extends Entity> clazz, final boolean hasEgg, final int range, final int updateFrequency,
			final boolean sendVelocityUpdates) {
		return buildEntityEntryFromClassWithName(clazz, new ResourceLocation(ModReference.ID, getRegistryNameForClass(clazz, "Entity")), hasEgg, range, updateFrequency, sendVelocityUpdates);
	}

	private static final EntityEntry buildEntityEntryFromClassWithName(final Class<? extends Entity> clazz, final ResourceLocation registryName, final boolean hasEgg, final int range,
			final int updateFrequency, final boolean sendVelocityUpdates) {
		EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create();
		builder = builder.entity(clazz);
		builder = builder.id(registryName, entityId++);
		builder = builder.name(registryName.getResourcePath());
		builder = builder.tracker(range, updateFrequency, sendVelocityUpdates);

		if (hasEgg)
			builder = builder.egg(0xFFFFFF, 0xAAAAAA);

		return builder.build();
	}

	private static final String getRegistryNameForClass(Class clazz, String removeType) {
		return org.apache.commons.lang3.StringUtils.uncapitalize(clazz.getSimpleName().replace(removeType, "")).replaceAll("([A-Z])", "_$1").toLowerCase();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static final void onRegisterModelsEvent(final ModelRegistryEvent event) {

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWire.class, new TileEntityWireRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnamel.class, new TileEntityEnamelRenderer());
		WIPTech.debug("registered TESRs");

		RenderingRegistry.registerEntityRenderingHandler(EntityPortableGenerator.class, renderManager -> new EntityPortableGeneratorRenderer(renderManager));
		RenderingRegistry.registerEntityRenderingHandler(EntitySlugCasing.class, renderManager -> new EntitySlugCasingRenderer(renderManager));
		RenderingRegistry.registerEntityRenderingHandler(EntitySlug.class, renderManager -> new EntitySlugRenderer(renderManager));
		RenderingRegistry.registerEntityRenderingHandler(EntityRailgun.class, renderManager -> new EntityRailgunRenderer(renderManager));
		RenderingRegistry.registerEntityRenderingHandler(EntityNapalm.class, renderManager -> new EntityNapalmRenderer(renderManager));

		WIPTech.debug("registered EntityRenderers");

		registerModelsForMaterials();

		ResourceLocation portableGeneratorRegistryName = ModEntities.PORTABLE_GENERATOR.getRegistryName();
		ModelLoader.setCustomModelResourceLocation(ModItems.PORTABLE_GENERATOR, 0, new ModelResourceLocation(new ResourceLocation(portableGeneratorRegistryName.getResourceDomain(), ""
				+ portableGeneratorRegistryName.getResourcePath()), ""));

		registerItemModel(ModItems.FLAMETHROWER);
		registerItemModel(ModItems.RAILGUN);

		ResourceLocation slugCasingRegistryName = ModEntities.SLUG_CASING.getRegistryName();
		ModelLoader.setCustomModelResourceLocation(ModItems.SLUG_CASING, 0, new ModelResourceLocation(new ResourceLocation(slugCasingRegistryName.getResourceDomain(), "" + slugCasingRegistryName
				.getResourcePath()), ""));

		WIPTech.debug("registered block & item models");

	}

	@SideOnly(Side.CLIENT)
	private static final void registerModelsForMaterials() {
		for (ModMaterials material : ModMaterials.values()) {

			if (material.getProperties().hasWire()) {
				ModelLoader.setCustomStateMapper(material.getWire(), new StateMapperBase() {
					@Override
					protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
						return new ModelResourceLocation(new ResourceLocation(ModReference.ID, material.getNameLowercase() + "_wire"), ModWritingUtil.default_variant_name);
					}
				});
			}

			if (material.getProperties().hasEnamel()) {
				ModelLoader.setCustomStateMapper(material.getEnamel(), new StateMapperBase() {

					@Override
					protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
						return new ModelResourceLocation(new ResourceLocation(ModReference.ID, material.getNameLowercase() + "_enamel"), ModWritingUtil.default_variant_name);
					}
				});
			}

		}

		ModelLoaderRegistry.registerLoader(new WireModelLoader());
		WIPTech.debug("registered wire & enamel custom models");

		for (

		ModMaterials material : ModMaterials.values()) {
			if (material.getProperties().hasOre())
				if (material.getOre() != null)
					registerItemBlockModel(material.getOre());

			if (material.getProperties().hasBlock())
				if (material.getBlock() != null)
					registerItemBlockModel(material.getBlock());

			if (material.getProperties().hasIngotAndNugget()) {

				if (material.getIngot() != null && material.getResouceLocationDomain("ingot", ForgeRegistries.ITEMS).equals(ModReference.ID))
					registerItemBlockModel(material.getIngot());
				if (material.getNugget() != null && material.getResouceLocationDomain("nugget", ForgeRegistries.ITEMS).equals(ModReference.ID))
					registerItemBlockModel(material.getNugget());

			}

			if (material.getProperties().hasWire()) {

				if (material.getWire() != null)
					registerItemBlockModel(material.getWire());
				if (material.getSpool() != null)
					registerItemBlockModel(material.getSpool());

			}

			if (material.getProperties().hasEnamel())
				if (material.getEnamel() != null)
					registerItemBlockModel(material.getEnamel());

			if (material.getProperties().hasArmor()) {
				if (material.getHelmet() != null)
					registerItemModel(material.getHelmet());
				if (material.getChestplate() != null)
					registerItemModel(material.getChestplate());
				if (material.getLeggings() != null)
					registerItemModel(material.getLeggings());
				if (material.getBoots() != null)
					registerItemModel(material.getBoots());
			}

			if (material.getProperties().hasTools()) {
				if (material.getPickaxe() != null)
					registerItemModel(material.getPickaxe());
				if (material.getAxe() != null)
					registerItemModel(material.getAxe());
				if (material.getSword() != null)
					registerItemModel(material.getSword());
				if (material.getShovel() != null)
					registerItemModel(material.getShovel());
				if (material.getHoe() != null)
					registerItemModel(material.getHoe());
			}

			if (material.getProperties().hasCoil())
				if (material.getCoil() != null)
					registerItemModel(material.getCoil());

			if (material.getProperties().hasRail())
				if (material.getRail() != null)
					registerItemModel(material.getRail());

			if (material.getProperties().hasRailgunSlug())
				if (material.getSlugItem() != null)
					registerItemModel(material.getSlugItem());

		}
	}

	@SideOnly(Side.CLIENT)
	private static final void registerItemModel(final Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), ModWritingUtil.default_variant_name));
	}

	@SideOnly(Side.CLIENT)
	private static final void registerItemBlockModel(final Block block) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), ModWritingUtil.default_variant_name));
	}

	@SideOnly(Side.CLIENT)
	private static final void registerBlockItemModel(final Block block) {
		registerItemBlockModel(block);
	}

	@SideOnly(Side.CLIENT)
	private static final void registerBlockItemItemOverrideModel(final Block block) {
		ModelLoader.setCustomModelResourceLocation(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", block.getRegistryName().getResourcePath())), 0, new ModelResourceLocation(block
				.getRegistryName(), ModWritingUtil.default_variant_name));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public static final void writeMod(final ModelBakeEvent event) {

		ModWritingUtil.writeMod();

	}

	/**
	 * taken from Draco18s
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static final void onModelBakeEvent(final ModelBakeEvent event) {

		/*@formatter:off*/
		final ResourceLocation[] models = {
				new ResourceLocation(ModReference.ID, "entity/portable_generator_handle"),
				new ResourceLocation(ModReference.ID, "entity/railgun_base"),
				new ResourceLocation(ModReference.ID, "entity/railgun_turret")
		};
		/*@formatter:on*/

		for (ResourceLocation model : models) {
			ModelResourceLocation location = new ModelResourceLocation(model.toString());

			IBakedModel bakedModel = ModelsCache.INSTANCE.getOrLoadBakedModel(model);

			event.getModelRegistry().putObject(location, bakedModel);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static final void onRenderGameOverlay(final RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || Minecraft.getMinecraft().currentScreen != null)
			return;

		IEnergyStorage energy = null;

		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult rayTraceResult = mc.objectMouseOver;
		mc.entityRenderer.getMouseOver(event.getPartialTicks());

		if (energy == null && mc.getRenderViewEntity().getRidingEntity() != null) {
			if (mc.getRenderViewEntity().getRidingEntity().hasCapability(CapabilityEnergy.ENERGY, null))
				energy = mc.getRenderViewEntity().getRidingEntity().getCapability(CapabilityEnergy.ENERGY, null);
		}
		if (energy == null && rayTraceResult != null && rayTraceResult.getBlockPos() != null) {
			TileEntity tileHit = mc.world.getTileEntity(rayTraceResult.getBlockPos());
			if (tileHit != null)
				if (tileHit.hasCapability(CapabilityEnergy.ENERGY, rayTraceResult.sideHit))
					energy = tileHit.getCapability(CapabilityEnergy.ENERGY, rayTraceResult.sideHit);
		}
		if (energy == null && rayTraceResult != null && rayTraceResult.entityHit != null) {
			if (rayTraceResult.entityHit.hasCapability(CapabilityEnergy.ENERGY, null))
				energy = rayTraceResult.entityHit.getCapability(CapabilityEnergy.ENERGY, null);
		}

		if (energy == null)
			return;

		double power = (double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored();
		int scaled_height = (int) Math.round((1 - power) * 52D);
		ScaledResolution Scaled = new ScaledResolution(Minecraft.getMinecraft());
		int Width = Scaled.getScaledWidth() - 10;
		int Height = Scaled.getScaledHeight() - 54;

		// TODO replace this with binding the energy texture
//				Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(ModReference.ID, "textures/gui/turbine.png"));

		// TESTING

		mc.getTextureManager().bindTexture(new ResourceLocation(ModReference.ID, "textures/gui/energy.png"));

		drawNonStandardTexturedRect(Width, Height, 0, 0, 10, 54, 256, 256);
		drawNonStandardTexturedRect(Width + 1, Height + 1 + scaled_height, 10, 0, 8, 52 - scaled_height, 256, 256);
		int percent = (int) Math.round(power * 100);
		mc.fontRenderer.drawStringWithShadow(percent + "%", Width - 7 - String.valueOf(percent).length() * 6, Height + 35, 0xFFFFFF);
		String outOf = energy.getEnergyStored() + "/" + energy.getMaxEnergyStored();
		mc.fontRenderer.drawStringWithShadow(outOf, Width - 1 - outOf.length() * 6, Height + 45, 0xFFFFFF);

	}

	private static final void drawNonStandardTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		double f = 1F / (double) textureWidth;
		double f1 = 1F / (double) textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, y + height, 0).tex(u * f, (v + height) * f1).endVertex();
		bufferbuilder.pos(x + width, y + height, 0).tex((u + width) * f, (v + height) * f1).endVertex();
		bufferbuilder.pos(x + width, y, 0).tex((u + width) * f, v * f1).endVertex();
		bufferbuilder.pos(x, y, 0).tex(u * f, v * f1).endVertex();
		tessellator.draw();
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public static final void onTooltipEvent(final ItemTooltipEvent event) {

		Item item = event.getItemStack().getItem();

		if (!item.getRegistryName().getResourceDomain().equals(ModReference.ID))
			return;

		if (item instanceof ItemCoil)
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((ItemCoil) item).getModMaterial().getProperties().getConductivity() + "");

		if (item instanceof ItemRail)
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((ItemRail) item).getModMaterial().getProperties().getConductivity() + "");

		if (Block.getBlockFromItem(item) instanceof BlockWire && !(Block.getBlockFromItem(item) instanceof BlockEnamel)) {
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((BlockWire) Block.getBlockFromItem(item)).getModMaterial().getProperties().getConductivity() + "");
			setTooltip(event, WIPTech.proxy.localize("Ouch! Put some insulation around it"));
		}

		if (Block.getBlockFromItem(item) instanceof BlockEnamel)
			setTooltip(event, WIPTech.proxy.localize("conductivity") + ": " + ((BlockEnamel) Block.getBlockFromItem(item)).getModMaterial().getProperties().getConductivity() + "");

	}

	private static final void setTooltip(final ItemTooltipEvent event, final String tooltip) {
		for (int i = 0; i < event.getToolTip().size(); i++) {
			if (net.minecraft.util.StringUtils.stripControlCodes(event.getToolTip().get(i)).equals(event.getItemStack().getItem().getRegistryName().toString())) { // TODO why? and what does
				// this do???
				event.getToolTip().add(i, tooltip);
				return;
			}
		}
		event.getToolTip().add(tooltip);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static final void onPlayerInteract(final PlayerInteractEvent event) {
		if (event.getEntityPlayer() == null || event.getEntityPlayer().getRidingEntity() == null || !(event.getEntityPlayer().getRidingEntity() instanceof EntityRailgun))
			return;

		((EntityRailgun) event.getEntityPlayer().getRidingEntity()).shoot();

		return;
	}
}