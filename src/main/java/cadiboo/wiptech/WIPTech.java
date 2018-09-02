package cadiboo.wiptech;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cadiboo.wiptech.api.WIPTechAPI;
import cadiboo.wiptech.capability.energy.network.CapabilityEnergyNetworkList;
import cadiboo.wiptech.network.ModNetworkManager;
import cadiboo.wiptech.util.IProxy;
import cadiboo.wiptech.util.ModEnums.ModMaterialTypes;
import cadiboo.wiptech.util.ModGuiHandler;
import cadiboo.wiptech.util.ModMaterialProperties;
import cadiboo.wiptech.util.ModReference;
import cadiboo.wiptech.util.ModWritingUtil;
import cadiboo.wiptech.world.gen.ModWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * WIPTech Alpha
 * @author Cadiboo
 */
/*@formatter:off*/
@Mod(modid = ModReference.MOD_ID,
	name = ModReference.MOD_NAME,
	version = ModReference.Version.VERSION,
	acceptedMinecraftVersions = ModReference.ACCEPTED_VERSIONS,
	dependencies = ModReference.DEPENDENCIES,
	canBeDeactivated = ModReference.CAN_BE_DEACTIVATED,
	clientSideOnly = false,
	serverSideOnly = false,
	modLanguage = "java",
	guiFactory = ModReference.GUI_FACTORY_CLASS)
/*@formatter:on*/
public class WIPTech {

	/***** FOR 1.13 *****/
	// TODO: Texture locations

	/***** FOR ALL VERSIONS *****/
	// TODO: Material Texture locations
	// TODO: electrocution
	// TODO: Wire & Enamel ItemBlock Models->Wire & Enamel Item Models (Code & JSON)
	// TODO: radioactivity
	// TODO: Slugs & SlugCasings
	// TODO: world gen to subscriber
	// TODO: NetworkManager to subscriber
	// TODO: generators
	// TODO: handheld coilgun
	// TODO: Gauss cannon (mounted coilgun)
	// TODO: handheld PlasmaGun & mounted Plasma Cannon
	// TODO: Crushing & hammering
	// TODO: weapon modules :( scopes, chips, etc
	// TODO: steel & tungsten carbite - alloying -> custom furnace | craftable raw alloy?
	// TODO: Glitch models

	@Instance(ModReference.MOD_ID)
	public static WIPTech instance;

	@SidedProxy(serverSide = ModReference.SERVER_PROXY_CLASS, clientSide = ModReference.CLIENT_PROXY_CLASS)
	public static IProxy proxy;

	private static Logger logger;

	/**
	 * Run before anything else. <s>Read your config, create blocks, items, etc, and register them with the GameRegistry</s>
	 * @see {@link net.minecraftforge.common.ForgeModContainer#preInit(FMLPreInitializationEvent) ForgeModContainer.preInit}
	 */
	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.logLogicalSide();
		GameRegistry.registerWorldGenerator(new ModWorldGenerator(), 3);
		new ModNetworkManager();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());

		if (ModReference.Debug.debugAPIMaterials()) {
			for (final ModMaterialTypes type : ModMaterialTypes.values()) {
				final int n = 5;
				BigInteger bi = BigInteger.ZERO;
				final BigDecimal rows = new BigDecimal(Math.pow(2, n));
				while (bi.compareTo(rows.toBigInteger()) < 0) {
					String bin = bi.toString(2);// Integer.toBinaryString(i);
					while (bin.length() < n) {
						bin = "0" + bin;
					}
					final char[] chars = bin.toCharArray();
					final boolean[] boolArray = new boolean[n];
					final String[] tfArray = new String[n];
					for (int j = 0; j < chars.length; j++) {
						boolArray[j] = chars[j] == '0' ? true : false;
						tfArray[j] = chars[j] == '0' ? "T" : "F";
					}

					WIPTech.info(String.join("", tfArray));

					WIPTechAPI.addMaterial("TEST_MATERIAL_" + type.getNameUppercase() + "_" + String.join("", tfArray), type, new ModMaterialProperties(boolArray[0], boolArray[1], boolArray[2], boolArray[3], boolArray[4], 4.00f, 73));

					bi = bi.add(BigInteger.ONE);
				}
			}
		}

		CapabilityEnergyNetworkList.register();

	}

	/**
	 * Do your mod setup. Build whatever data structures you care about. Register recipes, send FMLInterModComms messages to other mods.
	 */
	@EventHandler
	public void init(final FMLInitializationEvent event) {
		if (proxy.getSide() == Side.CLIENT) {
			ModWritingUtil.writeMod();
		}
	}

	/**
	 * Mod compatibility, or anything which depends on other mods’ init phases being finished.
	 * @see {@link net.minecraftforge.common.ForgeModContainer#postInit(FMLPostInitializationEvent) ForgeModContainer.postInit}
	 */
	@EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
	}

	private static Logger getLogger() {
		if (logger == null) {
			final Logger tempLogger = LogManager.getLogger();
			tempLogger.error("[" + WIPTech.class.getSimpleName() + "]: getLogger called before logger has been initalised! Providing default logger");
			return tempLogger;
		}
		return logger;
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#DEBUG DEBUG} level.
	 * @param  messages the message objects to log.
	 * @author          Cadiboo
	 */
	public static void debug(final Object... messages) {
		for (final Object msg : messages) {
			getLogger().debug(msg);
		}
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#INFO ERROR} INFO.
	 * @param  messages the message objects to log.
	 * @author          Cadiboo
	 */
	public static void info(final Object... messages) {
		for (final Object msg : messages) {
			getLogger().info(msg);
		}
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#WARN WARN} level.
	 * @param  messages the message objects to log.
	 * @author          Cadiboo
	 */
	public static void warn(final Object... messages) {
		for (final Object msg : messages) {
			getLogger().warn(msg);
		}
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#ERROR ERROR} level.
	 * @param  messages the message objects to log.
	 * @author          Cadiboo
	 */
	public static void error(final Object... messages) {
		for (final Object msg : messages) {
			getLogger().error(msg);
		}
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#FATAL FATAL} level.
	 * @param  messages the message objects to log.
	 * @author          Cadiboo
	 */
	public static void fatal(final Object... messages) {
		for (final Object msg : messages) {
			getLogger().fatal(msg);
		}
	}

	/**
	 * Logs all {@link java.lang.reflect.Field Field}s and their values of an object with the {@link org.apache.logging.log4j.Level#INFO INFO} level.
	 * @param  objects the objects to dump.
	 * @author         Cadiboo
	 */
	public static void dump(final Object... objects) {
		for (final Object obj : objects) {
			final Field[] fields = obj.getClass().getDeclaredFields();
			info("Dump of " + obj + ":");
			for (int i = 0; i < fields.length; i++) {
				try {
					fields[i].setAccessible(true);
					info(fields[i].getName() + " - " + fields[i].get(obj));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					info("Error getting field " + fields[i].getName());
					info(e.getLocalizedMessage());
				}
			}
		}
	}
}
