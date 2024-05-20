package treemek.mesky.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.Reference;
import treemek.mesky.config.ConfigHandler;
import treemek.mesky.handlers.RenderHandler;
import treemek.mesky.utils.Waypoints.Waypoint;

public class Alerts {

	public static class Alert {
		private String triggerMessage;
        private String displayedMessage;
        private float Time; // [ms]
        private boolean onlyParty;
        public boolean ignorePlayers;
        public boolean isEqual; // will be added but it requires fixing all code in AlertGui (and i dont have time for that rn)
        
        public Alert(String trigger, String displayed, float time, boolean onlyParty, boolean ignorePlayers, boolean isEqual) {
	        this.triggerMessage = trigger;
	        this.displayedMessage = displayed;
	        this.Time = time;
	        this.onlyParty = onlyParty;
	        this.ignorePlayers = ignorePlayers;
	        this.isEqual = isEqual;
        }
        
        public String getTrigger() {
            return triggerMessage;
        }

        public String getDisplay() {
            return displayedMessage;
        }
        public float getTime() {
        	return Time;
        }

		public boolean getOnlyParty() {
			return onlyParty;
		}
		
		public boolean getIgnorePlayers() {
        	return ignorePlayers;
        }
        public boolean getIsEqual() {
        	return isEqual;
        }
    }
	
	// Method to add data
    public static void addAlert(String trigger, String display, float time) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
    	
    	alertsList.add(new Alert(trigger, display, time * 1000, false, false, false));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Added: "));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Trigger: " + trigger));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Display: " + display));
		player.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Time: " + time + 's'));
		ConfigHandler.SaveAlert(alertsList);
    }
    
 // Method to add data
    public static void deleteAlert(int id) {
    	EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Deleted: " + alertsList.get(id).getTrigger()));
		alertsList.remove(id);
        ConfigHandler.SaveAlert(alertsList);
		return;
    }
    
 // Method to add data
    public static void showAlert() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.AQUA + "[Mesky]: " + EnumChatFormatting.WHITE + "Alert list:"));
        for (Alert alert : alertsList) {
        	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Trigger: " + EnumChatFormatting.WHITE + alert.getTrigger()));
        	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Display: " + EnumChatFormatting.WHITE + alert.getDisplay()));
        	player.addChatMessage(new ChatComponentText(EnumChatFormatting.BOLD.DARK_AQUA + "Time: " + EnumChatFormatting.WHITE + alert.getTime()/20f + "s"));
        	player.addChatMessage(new ChatComponentText(""));
        }
		ConfigHandler.SaveAlert(alertsList);
    }
	
	public static String appearedAlert = null;
	private static float alertDisplayDuration = 1000;
	public static long alertDisplayTime = 0;
	// we must setup saving it in file
	
	public static List<Alert> alertsList = new ArrayList<>();
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onChat(ClientChatReceivedEvent event) {
		// Alerts
		String message = event.message.getUnformattedText();
		String nickname = Minecraft.getMinecraft().thePlayer.getName();
		String nonColorMessage = StringUtils.stripControlCodes(message);
		String onlyNonColorMessage = nonColorMessage;
		boolean autor = false;
		
		if(nonColorMessage.contains(": ")){
			onlyNonColorMessage = nonColorMessage.substring(nonColorMessage.indexOf(":") + 1);
			String[] beforeMessage = nonColorMessage.split(":")[0].split(" ");
			
			for (String string : beforeMessage) {
				if(string.equals(nickname) || string.equals(nickname + ":")) autor = true;
			}
		}
		
		
		for(int i = 0; i < alertsList.size(); i++) {
			if(alertsList.get(i).triggerMessage.equals("")) continue;
			if(alertsList.get(i).getIsEqual()) {
				if(onlyNonColorMessage.equals(alertsList.get(i).getTrigger())) {
					if(alertsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) return;
					if(alertsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) return;
					if(autor) return;

					Alerts.appearedAlert = Alerts.alertsList.get(i).getDisplay();
					Alerts.alertDisplayTime = System.currentTimeMillis();
					alertDisplayDuration = Alerts.alertsList.get(i).getTime();
					ResourceLocation soundLocation = new ResourceLocation("minecraft", "random.anvil_land");
				    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation));
				}
			}else {
				if(message.contains(alertsList.get(i).getTrigger())) {
					if(alertsList.get(i).getOnlyParty() && !nonColorMessage.startsWith("Party >")) return;
					if(alertsList.get(i).getIgnorePlayers() && nonColorMessage.contains(": ")) return;
					if(autor) return; // ignores messages written by yourself
					
					Alerts.appearedAlert = Alerts.alertsList.get(i).getDisplay();
					Alerts.alertDisplayTime = System.currentTimeMillis();
					alertDisplayDuration = Alerts.alertsList.get(i).getTime();
					ResourceLocation soundLocation = new ResourceLocation("minecraft", "random.anvil_land");
				    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation));
				}
			}
		}
	}
	
	
	
	public static void DisplayCustomAlerts(String display, int dur) {
		Alerts.appearedAlert = display;
		Alerts.alertDisplayTime = System.currentTimeMillis();
		alertDisplayDuration = dur;
		ResourceLocation soundLocation = new ResourceLocation("minecraft", "random.anvil_land");
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(soundLocation));
	}
	
	 @SubscribeEvent
	    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
	        if (appearedAlert != null) {
	            renderAlert(event.resolution);
	        }
	    }

	    private void renderAlert(ScaledResolution resolution) {
	        Minecraft mc = Minecraft.getMinecraft();
	        FontRenderer fontRenderer = mc.fontRendererObj;

	        int posX = resolution.getScaledWidth() / 2;
	        int posY = resolution.getScaledHeight() / 2;
	        
	        RenderHandler.drawTitle(appearedAlert, resolution, 0xf54245);
	        
	     // Check if the delay duration has passed
	        if (System.currentTimeMillis() - alertDisplayTime >= alertDisplayDuration) {
	        	appearedAlert = null; // Reset appearedAlert after the delay
	            
	        }
	       
	    }
	    
}
