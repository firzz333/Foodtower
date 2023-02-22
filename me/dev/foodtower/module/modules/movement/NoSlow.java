/*
Author:SuMuGod
Date:2022/7/10 4:30
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.Killaura;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.utils.normal.PacketUtils;
import me.dev.foodtower.value.Mode;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.*;

public class NoSlow extends Module {
    private TimerUtil time = new TimerUtil();
    private boolean nextTemp = false;
    private LinkedList<Packet<INetHandlerPlayServer>> packetBuf;
    private final Mode mode = new Mode("Mode", "mode", NoSlowMode.values(), NoSlowMode.Vanilla);

    public NoSlow() {
        super("NoSlow", "无迟", new String[]{"NoSlow"}, ModuleType.Movement);
        this.setColor(new Color(255, 255, 255).getRGB());
    }

    @Override
    public void onDisable() {
        nextTemp = false;
        packetBuf.clear();
    }

    @NMSL
    private void onUpdate(EventPreUpdate eventPreUpdate) {
        setSuffix(mode.getValue());
    }

    @NMSL
    private void onPacket(EventPacketRecieve e) {
        if (mode.getValue() == NoSlowMode.Hypixel && e.getPacket() instanceof S30PacketWindowItems && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking())) {
            e.setCancelled(true);
        }
    }

    @NMSL
    private void onMove(EventPreUpdate eventPreUpdate) {
        if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || Killaura.isBlocking) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "ncp":
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                    break;
                case "aac":
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                    }
                    break;
                case "hypixel":
                    if (mc.thePlayer.isBlocking() || Killaura.isBlocking) {
                        if (time.hasReached(230) && nextTemp) {
                            nextTemp = false;
                            PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                        }
                        if (!packetBuf.isEmpty()) {
                            boolean canAttack = false;
                            for (Packet packet : packetBuf) {
                                if (packet instanceof C03PacketPlayer) {
                                    canAttack = true;
                                }
                                if (!(packet instanceof C0APacketAnimation || packet instanceof C02PacketUseEntity) && !canAttack) {
                                    PacketUtils.sendPacketNoEvent(packet);
                                }
                            }
                            packetBuf.clear();
                        }
                        if (!nextTemp) {
                            if (!Killaura.isBlocking || !mc.thePlayer.isBlocking()) {
                                return;
                            }
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                            nextTemp = true;
                            time.reset();
                        }
                    }
            }
        }
    }

    @NMSL
    private void onMove(EventPostUpdate eventPostUpdate) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "ncp":
                    if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || Killaura.isBlocking) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                    break;
                case "aac":
                    if (mc.thePlayer.ticksExisted % 3 != 0 && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || Killaura.isBlocking)) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                    break;
            }
        }

        @NMSL
        private void onPacket(EventPacketSend e) {
        Packet packet = e.getPacket();
        if (mode.getValue() == NoSlowMode.Hypixel && nextTemp) {
            if ((e.getPacket() instanceof C07PacketPlayerDigging || e.getPacket() instanceof C08PacketPlayerBlockPlacement) && (mc.thePlayer.isBlocking() || Killaura.isBlocking)) {
                e.setCancelled(true);
            } else if (packet instanceof C03PacketPlayer || packet instanceof C0APacketAnimation || packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity || packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement) {
                packetBuf.add((Packet<INetHandlerPlayServer>) packet);
                e.setCancelled(true);
            }
        }
    }

    enum NoSlowMode {
        Vanilla, Hypixel, NCP, AAC
    }
}
