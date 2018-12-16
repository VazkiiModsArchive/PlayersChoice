package vazkii.playerschoice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState.ModState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.ProgressManager.ProgressBar;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = PlayersChoice.MOD_ID, name = PlayersChoice.MOD_NAME, version = PlayersChoice.VERSION, dependencies = PlayersChoice.DEPENDENCIES, clientSideOnly = true)
public class PlayersChoice {

	public static final String MOD_ID = "playerschoice";
	public static final String MOD_NAME = "Player's Choice";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;	
	public static final String DEPENDENCIES = "before:*";

	private static final int PRE_INIT = ModState.PREINITIALIZED.ordinal();
	
	@Instance
	public static PlayersChoice instance;

	private File markerFile;

	public ModSettings settings;
	public ModSettings.DataHolder data;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		File configFolder = event.getSuggestedConfigurationFile().getParentFile();
		File fmlPropFile = new File(configFolder, "fmlModState.properties");
		File jsonFile = new File(configFolder, "playerschoice.json");
		File instanceDir = configFolder.getParentFile();
		File filesDir = new File(configFolder, "playerschoice_files");
		markerFile = new File(instanceDir, "playerschoice.marker");

		settings = new ModSettings(jsonFile, fmlPropFile, instanceDir, filesDir);
		data = settings.holder;
		
		if(!markerFile.exists()) try {
			nukeMods();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.START && Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu && !markerFile.exists())
			Minecraft.getMinecraft().displayGuiScreen(new GuiChooseMods());
	}

	public void addMarker() {
		if(!markerFile.exists())
			try {
				markerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	// I am so sorry cpw
	private void nukeMods() throws ReflectiveOperationException {
		Loader loader = Loader.instance();
		List<ModContainer> mods = Loader.instance().getModList();
		LoadController controller = ReflectionHelper.getPrivateValue(Loader.class, loader, "modController");
		Multimap<ModContainer, ModState> states = ReflectionHelper.getPrivateValue(LoadController.class, controller, "modStates");
		
		ImmutableMap<String, EventBus> eventBusses = ReflectionHelper.getPrivateValue(LoadController.class, controller, "eventChannels");
		Map<String, EventBus> eventBussesMutable = new HashMap(eventBusses);
		
		ConcurrentHashMap<Object, ?> registeredObjs = ReflectionHelper.getPrivateValue(
				net.minecraftforge.fml.common.eventhandler.EventBus.class, MinecraftForge.EVENT_BUS, "listeners");
		
		Enumeration<Object> keys = registeredObjs.keys();
		List<Object> removeThese = new ArrayList();
		while(keys.hasMoreElements()) {
			Object o = keys.nextElement();
			Class clazz = o.getClass();
			if(!clazz.getName().contains("net.minecraftforge.") && clazz != this.getClass())
				removeThese.add(o);
		}
		for(Object o : removeThese)
			MinecraftForge.EVENT_BUS.unregister(o);
		
		for(ModContainer container : mods)
			if(container instanceof FMLModContainer && container.getMod() != this && loader.getModState(container).ordinal() < PRE_INIT && !data.crashingMods.contains(container.getModId())) {
				states.put(container, ModState.DISABLED);
				eventBussesMutable.put(container.getModId(), new EventBus());
			}
		
		eventBusses = ImmutableMap.copyOf(eventBussesMutable);
		ReflectionHelper.setPrivateValue(LoadController.class, controller, eventBusses, "eventChannels");
	}

}
