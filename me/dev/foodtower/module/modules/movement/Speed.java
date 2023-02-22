/*
Author:SuMuGod
Date:2022/7/10 4:32
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.utils.normal.PacketUtils;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Speed extends Module {
    public Mode mode = new Mode("Mode", "mode", SpeedMode.values(), SpeedMode.NCPBhop);
    private Numbers<Double> customSpeedBoost = new Numbers<>("HypixelBoost", "hypixelboost", 0.1, 0.0, 0.4, 0.1);
    private int level = 1;
    public double moveSpeed = 0.2873;
    private double lastDist;
    private int timerDelay;

    public Speed() {
        super("Speed", "急行以驰", new String[]{"zoom"}, ModuleType.Movement);
        this.setColor(new Color(99, 248, 91).getRGB());
        setKey(Keyboard.KEY_V);
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        if (MoveUtils.isMoving()) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "hypixellatest":
                    PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(-1, -1, -1), EnumFacing.UP));

                    if (mc.thePlayer.onGround) {
                        MoveUtils.strafe(MoveUtils.defaultSpeed());
                        mc.thePlayer.jump();
                        MoveUtils.strafe(MoveUtils.getSpeed() * 1.005);

                        if (MoveUtils.getSpeed() < 0.43) {
                            MoveUtils.strafe(0.43);
                        }
                    } else {
                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            mc.thePlayer.motionX *= (1.0003 + 0.0015 * customSpeedBoost.getValue() * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
                            mc.thePlayer.motionZ *= (1.0003 + 0.0015 * customSpeedBoost.getValue() * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
                        }

                        double oldMotionX = mc.thePlayer.motionX;
                        double oldMotionZ = mc.thePlayer.motionZ;

                        MoveUtils.strafe(MoveUtils.getSpeed());
                        mc.thePlayer.motionX = (mc.thePlayer.motionX + oldMotionX * 5) / 6;
                        mc.thePlayer.motionZ = (mc.thePlayer.motionZ + oldMotionZ * 5) / 6;
                    }
                    break;
                case "dcj":
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.37;
                    }

                    if (!mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        mc.thePlayer.motionX *= 4;
                        mc.thePlayer.motionZ *= 4;
                        MoveUtils.strafe(MoveUtils.getBaseMoveSpeed() * 2.4);
                    } else if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        mc.thePlayer.motionX *= 4;
                        mc.thePlayer.motionZ *= 4;
                        MoveUtils.strafe(MoveUtils.getBaseMoveSpeed() * 2.8);
                    }
                    break;
                case "jump":
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    break;
            }
        }
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        return baseSpeed;
    }

    private double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(3, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    @Override
    public void onEnable() {
        if (mode.getValue() == SpeedMode.NCPBhop) {
            mc.timer.timerSpeed = 1F;
            level = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically ? 1 : 4;
        }
    }

    @Override
    public void onDisable() {
        if (mode.getValue() == SpeedMode.NCPBhop) {
            mc.timer.timerSpeed = 1F;
            moveSpeed = getBaseMoveSpeed();
            level = 0;
        }
    }

    @NMSL
    private void onMove(EventMove e) {
        if (mode.getValue() == SpeedMode.NCPBhop) {
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
            ++timerDelay;
            timerDelay %= 5;

            if (timerDelay != 0) {
                mc.timer.timerSpeed = 1F;
            } else {
                if (MoveUtils.isMoving()) mc.timer.timerSpeed = 32767F;

                if (MoveUtils.isMoving()) {
                    mc.timer.timerSpeed = 1.3F;
                    mc.thePlayer.motionX *= 1.0199999809265137;
                    mc.thePlayer.motionZ *= 1.0199999809265137;
                }
            }
            if (mc.thePlayer.onGround && MoveUtils.isMoving()) level = 2;

            if (round(mc.thePlayer.posY - (double) ((int) mc.thePlayer.posY)) == round(0.138)) {
                EntityPlayerSP thePlayer = mc.thePlayer;
                thePlayer.motionY -= 0.08;
                e.setY(e.getY() - 0.09316090325960147);
                thePlayer.posY -= 0.09316090325960147;
            }
            if (level == 1 && (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f)) {
                level = 2;
                moveSpeed = 1.35 * getBaseMoveSpeed() - 0.01;
            } else if (level == 2) {
                level = 3;
                mc.thePlayer.motionY = 0.399399995803833;
                e.setY(0.399399995803833);
                moveSpeed *= 2.149;
            } else if (level == 3) {
                level = 4;
                double difference = 0.66 * (lastDist - getBaseMoveSpeed());
                moveSpeed = lastDist - difference;
            } else {
                if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 || mc.thePlayer.isCollidedVertically)
                    level = 1;

                moveSpeed = lastDist - lastDist / 159.0;
            }
            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
            final MovementInput movementInput = mc.thePlayer.movementInput;
            float forward = MovementInput.moveForward;
            float strafe = MovementInput.moveStrafe;
            float yaw = mc.thePlayer.rotationYaw;

            if (forward == 0.0f && strafe == 0.0f) {
                e.setX(0.0);
                e.setZ(0.0);
            } else if (forward != 0.0f) {
                if (strafe >= 1.0f) {
                    yaw += (float) (forward > 0.0f ? -45 : 45);
                    strafe = 0.0f;
                } else if (strafe <= -1.0f) {
                    yaw += (float) (forward > 0.0f ? 45 : -45);
                    strafe = 0.0f;
                }
                if (forward > 0.0f) {
                    forward = 1.0f;
                } else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            final double mx2 = Math.cos(Math.toRadians(yaw + 90.0f));
            final double mz2 = Math.sin(Math.toRadians(yaw + 90.0f));
            e.setX((double) forward * moveSpeed * mx2 + (double) strafe * moveSpeed * mz2);
            e.setZ((double) forward * moveSpeed * mz2 - (double) strafe * moveSpeed * mx2);
            mc.thePlayer.stepHeight = 0.6F;

            if (forward == 0.0F && strafe == 0.0F) {
                e.setX(0.0);
                e.setZ(0.0);
            }
        }
    }

    enum SpeedMode {
        NCPBhop, DCJ, HypixelLatest, Jump
    }
}