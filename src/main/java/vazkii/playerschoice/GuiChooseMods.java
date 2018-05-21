package vazkii.playerschoice;

import net.minecraft.client.gui.GuiScreen;

public class GuiChooseMods extends GuiScreen {

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		mc.fontRenderer.drawStringWithShadow("hello", 20, 20, 0xFFFFFF);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
}
