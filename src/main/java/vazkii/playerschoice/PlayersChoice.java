package vazkii.playerschoice;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(modid = PlayersChoice.MOD_ID, name = PlayersChoice.MOD_NAME, version = PlayersChoice.VERSION, clientSideOnly = true)
public class PlayersChoice {
	
	public static final String MOD_ID = "playerschoice";
	public static final String MOD_NAME = "Player's Choice";
	public static final String BUILD = "GRADLE:BUILD";
	public static final String VERSION = "GRADLE:VERSION-" + BUILD;	
	
	private File fmlPropFile;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		fmlPropFile = new File(event.getSuggestedConfigurationFile().getParentFile(), "fmlModState.properties");
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void tick(ClientTickEvent event) {
		if(event.phase == Phase.START && Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu && !fmlPropFile.exists())
			Minecraft.getMinecraft().displayGuiScreen(new GuiChooseMods());
	}
	
}
