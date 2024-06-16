package treemek.mesky.handlers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import treemek.mesky.Commands;
import treemek.mesky.features.FishingTimer;
import treemek.mesky.handlers.gui.ChatFunctionsGui;
import treemek.mesky.handlers.gui.CosmeticsGui;
import treemek.mesky.handlers.gui.GUI;
import treemek.mesky.handlers.gui.GuiLocations;
import treemek.mesky.handlers.gui.Settings;
import treemek.mesky.handlers.gui.WaypointsGui;
import treemek.mesky.handlers.gui.alerts.AlertPosition;
import treemek.mesky.handlers.gui.alerts.AlertsGui;
import treemek.mesky.utils.Locations;
import treemek.mesky.utils.Locations.Location;

public class GuiHandler{
	
	public static int GuiType = 0;
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// for opening gui, because i have to wait 1 tick before opening it
		switch(GuiType) {
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(new GUI());
	            GuiType = 0;
	            break;
			case 2:
	            Minecraft.getMinecraft().displayGuiScreen(new Settings());
	            GuiType = 0;
	            break;
			case 3:
				Minecraft.getMinecraft().displayGuiScreen(new WaypointsGui());
				Location.checkTabLocation();
				WaypointsGui.region.setText(Locations.currentLocationText);
	            GuiType = 0;
	            break;
			case 4:
				Minecraft.getMinecraft().displayGuiScreen(new AlertsGui());
	            GuiType = 0;
	            break;
			case 5:
				Minecraft.getMinecraft().displayGuiScreen(new CosmeticsGui());
	            GuiType = 0;
	            break;
			case 6:
				Minecraft.getMinecraft().displayGuiScreen(new ChatFunctionsGui());
	            GuiType = 0;
	            break;
			case 7:
				Minecraft.getMinecraft().displayGuiScreen(new GuiLocations());
	            GuiType = 0;
	            break;
			case 8:
				Minecraft.getMinecraft().displayGuiScreen(new AlertPosition());
	            GuiType = 0;
	            break;
		}
	}
	
	private static final int CUSTOM_BUTTON_ID = ("Mesky".hashCode() << 16) | 69; // generates "unique" mod id from my name and 69 XD
	
	@SubscribeEvent
	public void onGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
	    GuiScreen guiScreen = event.gui;
	    // Adding button to pause gui
	    
	    if (guiScreen instanceof GuiIngameMenu) {
	    	 int x = event.gui.width - 105;
	            int x2 = x + 100;
	            int y = event.gui.height - 22;
	            int y2 = y + 20;

	            List<GuiButton> sortedButtons = event.buttonList;
	            Collections.sort(sortedButtons, new Comparator<GuiButton>() {
	                public int compare(GuiButton a, GuiButton b) {
	                    return (b.yPosition + b.height) - (a.yPosition + a.height);
	                }
	            });

	            for (GuiButton button : sortedButtons) {
	                int otherX = button.xPosition;
	                int otherX2 = button.xPosition + button.width;
	                int otherY = button.yPosition;
	                int otherY2 = button.yPosition + button.height;

	                if (otherX2 > x && otherX < x2 && otherY2 > y && otherY < y2) {
	                    y = otherY - 20 - 2;
	                    y2 = y + 20;
	                }
	            }
	    	
	    	event.buttonList.add(new GuiButton(CUSTOM_BUTTON_ID, x, Math.max(0, y), 100, 20, "Mesky"));
	    }
	}
	
	@SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        GuiScreen guiScreen = event.gui;
        GuiButton button = event.button;
        // interaction with my button in pause menu
        
        if (guiScreen instanceof GuiIngameMenu) {
            if (button.id == CUSTOM_BUTTON_ID) {
                GuiType = 1;
            }
        }
    }
	
	
}