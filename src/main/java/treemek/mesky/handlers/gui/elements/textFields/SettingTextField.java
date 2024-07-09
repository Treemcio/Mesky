package treemek.mesky.handlers.gui.elements.textFields;

import java.lang.reflect.Field;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import treemek.mesky.config.SettingsConfig;
import treemek.mesky.config.SettingsConfig.Setting;
import treemek.mesky.handlers.RenderHandler;

public class SettingTextField extends GuiTextField{

	private FontRenderer fontRendererObj;
	private String buttonText;
	Setting setting;
	boolean onlyNumbers;
	private boolean shouldSaveBlank;

	public SettingTextField(int id, String buttonText, int width, int height, Setting setting, int maxStringLength, boolean onlyNumbers, boolean shouldSaveBlank) {
		super(id, Minecraft.getMinecraft().fontRendererObj, 0, 0, width, height - 4);
		this.buttonText = buttonText;
		this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
		this.setting = setting;
		this.onlyNumbers = onlyNumbers;
		this.shouldSaveBlank = shouldSaveBlank;
		this.setMaxStringLength(maxStringLength);
		setTextAsSetting();
	}
	
	
	
	public void drawTextField(int x, int y) {
		this.xPosition = x;
		this.yPosition = y + 2; // because height has -4 and thats because of wrong drawing of textbox having border outside of its height
		
		this.drawTextBox();
		
		int correctSize = height+4;
		
		float defaultFontHeight = fontRendererObj.FONT_HEIGHT;
		float scaleFactor = (float) (correctSize / defaultFontHeight) / 2f;
		
		float textY = y + ((correctSize / 2) - ((defaultFontHeight * scaleFactor) / 2));
		RenderHandler.drawText(buttonText, x + (width*1.25), textY, scaleFactor, true, 0x3e91b5);
	}

	public void setTextAsSetting() {
		try {
			String text;
			if(onlyNumbers) {
				text = String.valueOf(setting.number);
			}else {
				text = setting.text;
			}
			
			if(text != null) this.setText(text);
		} catch (IllegalArgumentException e) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("There was a problem retrieving the value for " + buttonText + ": " + e.getMessage()));
			e.printStackTrace();
			return;
		}
	}
	
	public void keyTyped(char typedChar, int keyCode){
		if(onlyNumbers && !isDigit(typedChar, keyCode)) return;
		
		this.textboxKeyTyped(typedChar, keyCode);
		
		try {
			if(!shouldSaveBlank && this.getText().length() == 0) return;
			setting.text = this.getText();
			
			if(onlyNumbers) {
				setting.number = Integer.parseInt(this.getText());
			}
			
		} catch (IllegalArgumentException e) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("There was a problem with saving input for " + buttonText + ": " + e.getMessage()));
			e.printStackTrace();
			return;
		}
	}
	
	
	private boolean isDigit(char typedChar, int keyCode) {
		if(Character.isDigit(typedChar)) {
			return true;
		}else {
			if(keyCode == 14 || keyCode == 203 || keyCode == 205 || keyCode == 211) {
				return true;
			}else if((keyCode == Keyboard.KEY_A || keyCode == Keyboard.KEY_C || keyCode == Keyboard.KEY_V) && isCtrlKeyDown()){
				return true;
			}else {
				return false;
			}
		}
	}

	 public static boolean isCtrlKeyDown(){
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }



	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= xPosition && mouseX <= xPosition + width && mouseY >= yPosition && mouseY <= yPosition + height;
	}

}
