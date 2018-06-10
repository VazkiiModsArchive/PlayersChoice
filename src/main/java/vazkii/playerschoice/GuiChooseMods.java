package vazkii.playerschoice;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import vazkii.playerschoice.ModSettings.ModConfig;

public class GuiChooseMods extends GuiScreen {

	private GuiSlotModSettings settingsList;
	
	private ModConfig first = null;
	private ModConfig mod = null;
	private boolean didOk = false;
	
	public List<ModSlot> slots;
	
	public GuiChooseMods() {

	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		List<ModConfig> configs = new ArrayList(Arrays.asList(PlayersChoice.instance.settings.mods));
		Collections.sort(configs);
		slots = new ArrayList();
		String curr = "";
		for(ModConfig config : configs) {
			if(!config.category.equals(curr))
				slots.add(new ModSlot(null, config.category));
			
			slots.add(new ModSlot(config, ""));
			if(first == null)
				first = config;
			
			curr = config.category;
		}
		
		mod = first;
		settingsList = new GuiSlotModSettings(this);
		
		buttonList.add(new GuiButton(0, 20, height - 35, 120, 20, I18n.format("gui.done")));
		buttonList.add(new GuiButton(1, 190, height - 35, 200, 20, I18n.format("playerschoice.website")));
		buttonList.add(new GuiButton(2, width / 2 - 100, height / 2 + 40, I18n.format("playerschoice.ok")));
		
		setupButtons();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		
		if(didOk) {
			settingsList.drawScreen(mouseX, mouseY, partialTicks);
			
			drawRect(0, 0, 160, 30, 0xDD000000);
			drawRect(0, height - 40, 160, height, 0xDD000000);
			drawCenteredString(mc.fontRenderer, I18n.format("playerschoice.mod_options"), 80, 10, 0xFFFFFFFF);
			
			if(mod != null) 
				renderModInfo();
		} else {
			GlStateManager.scale(2F, 2F, 2F);
			drawCenteredString(mc.fontRenderer, I18n.format("playerschoice.name"), width / 4, height / 4 - 40, 0x22FFFF);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			
			List<String> strings = mc.fontRenderer.listFormattedStringToWidth(I18n.format("playerschoice.info"), 200);
			for(int i = 0; i < strings.size(); i++)
				drawCenteredString(mc.fontRenderer, strings.get(i), width / 2, height / 2 - 40 + i * 10, 0xFFFFFF);
		}
		
		if(didOk)
			drawCenteredString(mc.fontRenderer, I18n.format("playerschoice.needs_restart"), 80, height - 12, 0xFFFF00);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	private void renderModInfo() {
		GlStateManager.scale(2F, 2F, 2F);
		mc.fontRenderer.drawStringWithShadow(mod.name, 90, 10, 0xFFFFFF);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		
		mc.fontRenderer.drawStringWithShadow(I18n.format("playerschoice.subtitle_" + mod.enabled), 180, 40, 0x999999);
		
		String desc = mod.desc.replaceAll("\\&", "\u00A7");
		List<String> strings = mc.fontRenderer.listFormattedStringToWidth(desc, Math.min(500, width - 200));
		for(int i = 0; i < strings.size(); i++)
			mc.fontRenderer.drawStringWithShadow(strings.get(i), 180, 60 + i * 10, 0xFFFFFF);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		
		super.handleMouseInput();
		
		if(didOk)
			settingsList.handleMouseInput(mouseX, mouseY);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		// NO-OP
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		switch(button.id) {
		case 0:
			PlayersChoice.instance.settings.commit();
			PlayersChoice.instance.addMarker();

			FMLCommonHandler.instance().exitJava(0, false);
			
			break;
		case 1:
			try {
				Class<?> oclass = Class.forName("java.awt.Desktop");
				Object object = oclass.getMethod("getDesktop").invoke(null);
				oclass.getMethod("browse", URI.class).invoke(object, new URI(mod.website));
			}
			catch(Throwable e) {
				e.printStackTrace();
			}
			break;
		case 2:
			didOk = true;
			setupButtons();
		} 
	}
	
	public void setSelected(ModConfig mod) {
		this.mod = mod;
		setupButtons();
	}
	
	public ModConfig getSelect() {
		return mod;
	}
	
	private void setupButtons() {
		buttonList.get(0).visible = didOk;
		buttonList.get(1).visible = didOk && mod.website != null && !mod.website.isEmpty();
		buttonList.get(2).visible = !didOk;
	}
	
	public class ModSlot {
		
		public final ModConfig config;
		public final String category;
		
		public ModSlot(ModConfig config, String category) {
			this.config = config;
			this.category = category;
		}
		
	}
	
}

