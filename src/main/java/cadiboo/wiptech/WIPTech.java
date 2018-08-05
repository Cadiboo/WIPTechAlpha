package cadiboo.wiptech;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Logger;

import cadiboo.wiptech.util.IProxy;
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
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * WIPTech Alpha
 * 
 * @author Cadiboo
 */

@Mod(modid = ModReference.ID, name = ModReference.NAME, version = ModReference.VERSION, acceptedMinecraftVersions = ModReference.ACCEPTED_VERSIONS, dependencies = "after:minecraft", canBeDeactivated = ModReference.CAN_BE_DEACTIVATED, clientSideOnly = false, serverSideOnly = false, modLanguage = "java", guiFactory = ModReference.GUI_FACTORY_CLASS)
public class WIPTech {

	// TODO radioactivity
	// TODO paramagnetism
	// TODO json models
	// FIXME json wire model gen & ^^^
	// TODO electricity damage
	// TODO electricity transfer
	// TODO electricity rendering
	// TODO autogenerate lang file (1.12.2 & 1.13)
	// TODO world gen to subscriber
	// TODO client SYNCING!!!
	// TODO start on machines. guns and entities later

	@Instance(ModReference.ID)
	public static WIPTech instance;

	@SidedProxy(serverSide = ModReference.SERVER_PROXY_CLASS, clientSide = ModReference.CLIENT_PROXY_CLASS)
	public static IProxy proxy;

	private static Logger logger;

	/**
	 * Run before anything else. <s>Read your config, create blocks, items, etc, and
	 * register them with the GameRegistry</s>
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		ModWritingUtil.writeMod();
		GameRegistry.registerWorldGenerator(new ModWorldGenerator(), 3);
//		WIPTechAPI.addMaterial("bauxite", new ModMaterialProperties(true, false, false, false, false, ModMaterials.ALUMINIUM.getProperties().getHardness(), 0, false));

	}

	/**
	 * Do your mod setup. Build whatever data structures you care about. Register
	 * recipes, send FMLInterModComms messages to other mods.
	 */
	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	/**
	 * Mod compatibility, or anything which depends on other mods’ init phases being
	 * finished.
	 */
	@EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		ModWritingUtil.writeMod();
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#INFO
	 * ERROR} INFO.
	 * 
	 * @param messages the message objects to log.
	 * @author Cadiboo
	 */
	public static void info(Object... messages) {
		for (Object msg : messages) {
			logger.info(msg);
		}
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#ERROR
	 * ERROR} level.
	 * 
	 * @param messages the message objects to log.
	 * @author Cadiboo
	 */
	public static void error(Object... messages) {
		for (Object msg : messages) {
			logger.error(msg);
		}
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#DEBUG
	 * DEBUG} level.
	 * 
	 * @param messages the message objects to log.
	 * @author Cadiboo
	 */
	public static void debug(Object... messages) {
		for (Object msg : messages) {
			logger.debug(msg);
		}
	}

	/**
	 * Logs message object(s) with the {@link org.apache.logging.log4j.Level#FATAL
	 * FATAL} level.
	 * 
	 * @param messages the message objects to log.
	 * @author Cadiboo
	 */
	public static void fatal(Object... messages) {
		for (Object msg : messages) {
			logger.fatal(msg);
		}
	}

	/**
	 * Logs all {@link java.lang.reflect.Field Field}s and their values of an object
	 * with the {@link org.apache.logging.log4j.Level#INFO INFO} level.
	 * 
	 * @param objects the objects to dump.
	 * @author Cadiboo
	 */
	public static void dump(Object... objects) {
		for (Object obj : objects) {
			Field[] fields = obj.getClass().getDeclaredFields();
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
