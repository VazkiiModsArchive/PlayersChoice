package vazkii.playerschoice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;
import vazkii.playerschoice.ModSettings.ModConfig;

public class GuiSlotModSettings extends GuiScrollingList {

	GuiChooseMods parent;
	
	public GuiSlotModSettings(GuiChooseMods parent) {
		super(Minecraft.getMinecraft(), 160, 0, 30, parent.height - 40, 0, 20);
		this.parent = parent;
	}

	@Override
	protected int getSize() {
		return PlayersChoice.instance.settings.mods.length;
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		parent.setSelected(index);
		if(doubleClick) {
			ModConfig config = PlayersChoice.instance.settings.mods[index];
			config.enabled = !config.enabled;
		}
	}

	@Override
	protected boolean isSelected(int index) {
		return parent.getSelect() == index;
	}

	@Override
	protected void drawBackground() {
		// NO-OP
	}

	@Override
	protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		ModConfig config = PlayersChoice.instance.settings.mods[slotIdx];
		
		int color = config.enabled ? 0x55FF55 : 0xFF5555;
		String prefix = config.enabled ? "\u2714 " : "\u2718 ";
		font.drawStringWithShadow(prefix + config.name, left + 5, slotTop + 4, color);
	}

}
