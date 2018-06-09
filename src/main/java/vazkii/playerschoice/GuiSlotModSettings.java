package vazkii.playerschoice;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.GuiScrollingList;
import vazkii.playerschoice.GuiChooseMods.ModSlot;
import vazkii.playerschoice.ModSettings.ModConfig;

public class GuiSlotModSettings extends GuiScrollingList {

	GuiChooseMods parent;
	
	public GuiSlotModSettings(GuiChooseMods parent) {
		super(Minecraft.getMinecraft(), 160, 0, 30, parent.height - 40, 0, 20);
		this.parent = parent;
	}

	@Override
	protected int getSize() {
		return parent.slots.size();
	}

	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		ModSlot slot = parent.slots.get(index);
		if(slot.config != null) {
			parent.setSelected(slot.config);
			if(doubleClick)
				slot.config.enabled = !slot.config.enabled;
		}
	}

	@Override
	protected boolean isSelected(int index) {
		ModSlot slot = parent.slots.get(index);
		return parent.getSelect() == slot.config;
	}

	@Override
	protected void drawBackground() {
		// NO-OP
	}

	@Override
	protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
		FontRenderer font = Minecraft.getMinecraft().fontRenderer;
		ModSlot slot = parent.slots.get(slotIdx);
		if(slot.config == null) {
			String c = slot.category;
			if(c.isEmpty())
				c = I18n.format("playerschoice.no_category");
			
			String s = "-- " + c + " --";
			font.drawStringWithShadow(s, left + listWidth / 2 - font.getStringWidth(s) / 2, slotTop + 4, 0xFFFFFF);
		} else {
			int color = slot.config.enabled ? 0x55FF55 : 0xFF5555;
			String prefix = slot.config.enabled ? "\u2714 " : "\u2718 ";
			font.drawStringWithShadow(prefix + slot.config.name, left + 5, slotTop + 4, color);
		}
	}

}
