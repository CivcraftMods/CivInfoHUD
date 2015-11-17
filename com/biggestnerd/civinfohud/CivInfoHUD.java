package com.biggestnerd.civinfohud;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod(modid="civinfohud", name="Civcraft Info HUD", version="v1.0")
public class CivInfoHUD {

	private Minecraft mc;
	private int fps, ping = 0;
	private double tps = 0;
	private Pattern tpsPattern = Pattern.compile("^TPS from last 1m, 5m, 15m: [*]?([0-9]+).*$");
	private long lastCheck = 0;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		mc = Minecraft.getMinecraft();
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(mc.theWorld != null && (System.currentTimeMillis() - lastCheck > 30000)) {
			mc.thePlayer.sendChatMessage("/tps");
			lastCheck = System.currentTimeMillis();
		}
	}
	
	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
		if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
			int vertOffset = mc.fontRendererObj.FONT_HEIGHT + 2;
			int horizOffset = 10;
			fps = mc.getDebugFPS();
			Color c = Color.GREEN;
			if(fps < 60) {
				c = Color.YELLOW;
			}
			if(fps < 30) {
				c = Color.RED;
			}
			mc.fontRendererObj.drawStringWithShadow("FPS: " + fps, horizOffset, res.getScaledHeight() - vertOffset, c.getRGB());
			
			horizOffset += mc.fontRendererObj.getStringWidth("FPS: " + fps + " ");
			c = Color.GREEN;
			if(tps < 18) {
				c = Color.YELLOW;
			}
			if(tps < 13) {
				c = Color.RED;
			}
			mc.fontRendererObj.drawStringWithShadow("TPS: " + tps, horizOffset, res.getScaledHeight() - vertOffset, c.getRGB());
			
			horizOffset += mc.fontRendererObj.getStringWidth("TPS: " + tps + " ");
			c = Color.GREEN;
			if(ping > 150) {
				c = Color.YELLOW;
			}
			if(ping > 300) {
				c = Color.RED;
			}
			mc.fontRendererObj.drawStringWithShadow("Ping: " + ping, horizOffset, res.getScaledHeight() - vertOffset, c.getRGB());
			GL11.glColor3f(1, 1, 1);
		}
	}
	
	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent event) {
		String msg = event.message.getUnformattedText();
		Matcher tpsMatcher = tpsPattern.matcher(msg);
		while(tpsMatcher.find()) {
			tps = Double.parseDouble(tpsMatcher.group(1));
		}
	}
}
