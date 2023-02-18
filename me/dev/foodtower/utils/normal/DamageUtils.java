package me.dev.foodtower.utils.normal;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;

import static me.dev.foodtower.utils.normal.PacketUtils.sendPacketNoEvent;


public class DamageUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void hypixelDamage() {
        for(int i = 0; i < 50; i++) {
            sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
        //sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        sendPacketNoEvent(new C03PacketPlayer(true));
    }

    public static void hypixelDamage(double x, double y, double z) {
        for(int i = 0; i < 50; i++) {
            sendPacketNoEvent(new C04PacketPlayerPosition(x, y + 0.0625, z, false));
            sendPacketNoEvent(new C04PacketPlayerPosition(x, y, z, false));
        }
        //sendPacketNoEvent(new C04PacketPlayerPosition(x, y, z, true));
        sendPacketNoEvent(new C03PacketPlayer(true));
    }

    public static void legitDamage() {
        double offsets [] = {0.41999998688698, 0.7531999805212, 1.00133597911215, 1.166109260938214, 1.24918707874468, 1.170787077218804, 1.015555072702206, 0.78502770378924, 0.4807108763317, 0.10408037809304, 0};
        sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
        for(int i = 0; i < 3; i++) {
            for(double offset : offsets) {
                sendPacketNoEvent(new C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, mc.thePlayer.rotationYaw + (float) Math.random(), (float) Math.random(), false));
            }
        }
        sendPacketNoEvent(new C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
    }

}