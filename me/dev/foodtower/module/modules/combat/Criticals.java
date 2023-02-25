/*
Author:SuMuGod
Date:2022/7/10 4:08
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.EventHandle;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventAttack;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.movement.Flight;
import me.dev.foodtower.module.modules.movement.Speed;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class Criticals extends Module {
    private final Mode mode = new Mode("Mode", "mode", CritMode.values(), CritMode.Packet);
    private final Numbers<Double> delay = new Numbers<>("Delay", "delay", 0.0, 0.0, 500.0, 10.0);
    private Option<Boolean> ground = new Option<>("GroundCheck", "groundcheck", true);
    private final TimerUtil timer = new TimerUtil();
    boolean edit;

    public Criticals() {
        super("Criticals", "重击", new String[]{"crits", "crit"}, ModuleType.Combat);
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());
    }

    private boolean canCrit() {
        return !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWeb && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && mc.thePlayer.ridingEntity == null && !Client.instance.getModuleManager().getModuleByClass(Flight.class).isEnabled() && timer.hasReached(delay.getValue());
    }

    private boolean groundcheck() {
        if (ground.getValue()) {
            if (!mc.thePlayer.onGround) return true;
        }

        return false;
    }

    @Override
    public void onEnable() {
        timer.reset();
    }

    @NMSL
    private void onAttack(EventAttack event) {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if (groundcheck()) {
            return;
        }

        if (canCrit()) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "packet":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0100013579, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0110013579, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1100019359, z, false));
                    break;
                case "hypixel":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, true));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    break;
                case "hop":
                    mc.thePlayer.motionY = 0.2;
                    break;
                case "jumps":
                    mc.thePlayer.jump();
                    break;
                case "fall":
                    mc.thePlayer.setPosition(x, y + 1, z);
                    break;
                case "packetground":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.41999998688698, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.7531999805212, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.00133597911214, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.16610926093821, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.24918707874468, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.1707870772188, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.0155550727022, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.78502770378924, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.4807108763317, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.4807108763317, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0, z, true));
            }
            timer.reset();
            edit = true;
        }
    }

    @NMSL
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            if (mode.getValue() == CritMode.Edit) {
                if (edit) {
                    e.setPacket(new C03PacketPlayer(false));
                }
                edit = false;
            }
        }
    }

    enum CritMode {
        Packet, Edit, PacketGround, Hypixel, Hop, Jumps, Fall
    }
}

