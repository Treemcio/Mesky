package treemek.mesky.cosmetics.wings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import treemek.mesky.cosmetics.CosmeticHandler;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class FireWings extends ModelBase
{
	private Minecraft mc;
	private ResourceLocation location;
	private ModelRenderer wing;
	private ModelRenderer wingTip;
	private boolean playerUsesFullHeight;
	int WingsScale = 70;
	float yRotation = 20;
	float[] color = new float[]{212, 212, 212};

	public FireWings(){
		this.mc = Minecraft.getMinecraft();
		this.location = new ResourceLocation("mesky", "textures/wings.png");
		this.playerUsesFullHeight = Loader.isModLoaded("animations");

		// Set texture offsets.
		setTextureOffset("wing.bone", 0, 0);
		setTextureOffset("wing.skin", -10, 8);
		setTextureOffset("wingtip.bone", 0, 5);
		setTextureOffset("wingtip.skin", -10, 18);

		// Create wing model renderer.
		wing = new ModelRenderer(this, "wing");
		wing.setTextureSize(30, 30); // 300px / 10px
		wing.setRotationPoint(-2F, 0, 0);
		wing.addBox("bone", -10.0F, -1.0F, -1.0F, 10, 2, 2);
		wing.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);

		// Create wing tip model renderer.
		wingTip = new ModelRenderer(this, "wingtip");
		wingTip.setTextureSize(30, 30); // 300px / 10px
		wingTip.setRotationPoint(-10.0F, 0.0F, 0.0F);
		wingTip.addBox("bone", -10.0F, -0.5F, -0.5F, 10, 1, 1);
		wingTip.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
		wing.addChild(wingTip); // Make the wingtip rotate around the wing.
	}

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer player = event.entityPlayer;

		if (player.equals(mc.thePlayer) && !player.isInvisible()){
			if(CosmeticHandler.Wings && CosmeticHandler.WingsType == 1) {
				renderWings(player, event.partialRenderTick);
			}
		}
	}

	private void renderWings(EntityPlayer player, float partialTicks)
	{
		double scale = WingsScale / 100D;
		double rotate = interpolate(player.prevRenderYawOffset, player.renderYawOffset, partialTicks);

		int SpeedYRotation = 75;
		int WalkYRotation = 50;
		int TargetYRotation = 20;
		
		
		GL11.glPushMatrix();
		GL11.glScaled(-scale, -scale, scale);
		GL11.glRotated(180 + rotate, 0, 1, 0); // Rotate the wings to be with the player.
		GL11.glTranslated(0, -(playerUsesFullHeight ? 1.5 : 1.3) / scale, 0); // Move wings correct amount up.
		GL11.glTranslated(0, 0, 0.2 / scale);

		if (player.isSneaking()){
			GL11.glTranslated(0D, 0.125D / scale, 0D);
		}

		
		// Acceleration of wing flapping
		if(player.moveForward > 0) {
			if (player.isSprinting()){
				TargetYRotation = 70;
				if(yRotation < TargetYRotation) {
				yRotation += 0.2;
				System.out.println(yRotation);
				}
			}else {
				TargetYRotation = 50;
				if(yRotation > TargetYRotation) {
					yRotation -= 0.15;
				}else if(yRotation < TargetYRotation) {
					yRotation += 0.05;
				}
			}
		}else {
			TargetYRotation = 20;
			if(yRotation > TargetYRotation) {
				yRotation -= 0.05;
			}
		}

		float[] colors = color;
		GL11.glColor3f(colors[0], colors[1], colors[2]);
		mc.getTextureManager().bindTexture(location);

		for (int j = 0; j < 2; ++j)
		{
			GL11.glEnable(GL11.GL_CULL_FACE);
			float f11 = (System.currentTimeMillis() % 1000) / 1000F * (float) Math.PI * 2.0F;
			this.wing.rotateAngleX = (float) Math.toRadians(-80F) - (float) Math.cos((double)f11) * 0.2F;
			this.wing.rotateAngleZ = (float) Math.toRadians(20F);
			this.wing.rotateAngleY = (float) Math.toRadians(yRotation);
			if(Math.round(yRotation) == TargetYRotation) this.wing.rotateAngleY += (float) Math.sin(f11) * 0.1F;
			this.wingTip.rotateAngleZ = -((float)(Math.sin((double)(f11 + 2.0F)) + 0.5D)) * 0.35F;
			this.wing.render(0.0625F);
			GL11.glScalef(-1.0F, 1.0F, 1.0F);

			if (j == 0)
			{
				GL11.glCullFace(1028);
			}
		}

		GL11.glCullFace(1029);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glColor3f(255F, 255F, 255F);
		GL11.glPopMatrix();
	}

	private float interpolate(float yaw1, float yaw2, float percent)
	{
		float f = (yaw1 + (yaw2 - yaw1) * percent) % 360;

		if (f < 0)
		{
			f += 360;
		}

		return f;
	}
}