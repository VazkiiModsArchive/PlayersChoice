package vazkii.playerschoice;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import vazkii.playerschoice.ModSettings.ModConfig;

public class GuiChooseMods extends GuiScreen {

	private GuiSlotModSettings settingsList;
	
	protected int select;
	
	@Override
	public void initGui() {
		super.initGui();
		
		select = 0;
		settingsList = new GuiSlotModSettings(this);
		
		buttonList.add(new GuiButton(0, 20, height - 25, 120, 20, I18n.format("gui.done")));
		buttonList.add(new GuiButton(1, 190, height - 25, 200, 20, I18n.format("playerschoice.website")));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		settingsList.drawScreen(mouseX, mouseY, partialTicks);
		
		drawRect(0, 0, 160, 30, 0xDD000000);
		drawRect(0, height - 30, 160, height, 0xDD000000);
		drawCenteredString(mc.fontRenderer, I18n.format("playerschoice.mod_options"), 80, 10, 0xFFFFFFFF);
		
		if(select < PlayersChoice.instance.settings.mods.length) 
			renderModInfo(PlayersChoice.instance.settings.mods[select]);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	private void renderModInfo(ModConfig config) {
		GlStateManager.scale(2F, 2F, 2F);
		mc.fontRenderer.drawStringWithShadow(config.name, 90, 10, 0xFFFFFF);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		
		mc.fontRenderer.drawStringWithShadow(I18n.format("playerschoice.subtitle_" + config.enabled), 180, 40, 0x999999);
		
		String desc = config.desc.replaceAll("\\&", "\u00A7");
		List<String> strings = mc.fontRenderer.listFormattedStringToWidth(desc, Math.min(500, width - 200));
		for(int i = 0; i < strings.size(); i++)
			mc.fontRenderer.drawStringWithShadow(strings.get(i), 180, 60 + i * 10, 0xFFFFFF);

		buttonList.get(1).visible = config.website != null && !config.website.isEmpty();
	}
	
	@Override
	public void handleMouseInput() throws IOException {
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
		
		super.handleMouseInput();
		
		settingsList.handleMouseInput(mouseX, mouseY);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		switch(button.id) {
		case 0:
			PlayersChoice.instance.settings.commit();
			mc.displayGuiScreen(null);
			break;
		case 1:
			try {
				Class<?> oclass = Class.forName("java.awt.Desktop");
				Object object = oclass.getMethod("getDesktop").invoke(null);
				oclass.getMethod("browse", URI.class).invoke(object, new URI(PlayersChoice.instance.settings.mods[select].website));
			}
			catch(Throwable e) {
				e.printStackTrace();
			}
			break;
		} 
	}
	
}

