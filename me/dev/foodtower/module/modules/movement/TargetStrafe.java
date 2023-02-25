/*
Author:SuMuGod
Date:2022/7/10 4:33
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;


import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.Killaura;
import me.dev.foodtower.utils.math.MathUtil;
import me.dev.foodtower.utils.math.MathUtils;
import me.dev.foodtower.utils.math.RotationUtil;
import me.dev.foodtower.utils.math.gl.GLUtils;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetStrafe extends Module {
    public static Numbers<Double> range;
    public Entity target;
    public static float hue;
    private double degree;
    private float groundY;
    public static  Option<Boolean> esp = new Option<Boolean>("Render", "render", true);
    public static  Option<Boolean> check = new Option<Boolean>("Check", "check", true);
    public static  Option<Boolean> behind = new Option<>("Behind", "behind", false);
    public static int index;
    private boolean left;
    private float rAnims;
    private List<Entity> targets;

    public TargetStrafe() {
        super("TargetStrafe", "转圈一圈", new String[] { "TargetStrafe" }, ModuleType.Movement);
        this.degree = 0.0;
        this.left = true;
        this.targets = new ArrayList<Entity>();
    }

    @Override
    public void onEnable() {
        this.targets.clear();
        this.degree = 0.0;
        this.left = true;
        this.target = null;
        this.rAnims = 0.0f;
    }

    @NMSL
    public void on3D(final EventRender3D e) {
        final Killaura ka = (Killaura) Client.instance.getModuleManager().getModuleByClass(Killaura.class);
        if (this.esp.getValue() && ka.isEnabled()) {
            this.drawESP(e);
        }
    }

    private void drawESP(final EventRender3D render) {
        this.esp(Killaura.curTarget, render.getPartialTicks(), TargetStrafe.range.getValue());
    }
    public static double interpolate(final double current, final double old, final double scale) {
        return old + (current - old) * scale;
    }

    @NMSL
    public void esp(final Entity player, final float partialTicks, final double rad) {
        float points = 90F;
        GlStateManager.enableDepth();
        if (Killaura.curTarget == null) {
            return;
        }
        final Minecraft mc = TargetStrafe.mc;
        if (Minecraft.getMinecraft().thePlayer.onGround) {
            final Minecraft mc2 = TargetStrafe.mc;
            this.groundY = (float)Minecraft.getMinecraft().thePlayer.posY;
        }
        RenderUtil.drawCircle(player, partialTicks, rad);
    }

    @NMSL
    private void onUpdate(final EventPreUpdate e) {
        final Client instance3 = Client.instance;
        Client.instance.getModuleManager();
        final Killaura ka = (Killaura) Client.instance.getModuleManager().getModuleByClass(Killaura.class);
        final Client instance4 = Client.instance;
        Client.instance.getModuleManager();
        final Speed speed = (Speed) Client.instance.getModuleManager().getModuleByClass(Speed.class);
        if (ka.isEnabled()) {
            this.target = Killaura.curTarget;
        } else {
            this.target = null;
        }
    }

    @NMSL(priority = 2)
    private void onMove(final EventMove e) {
        if (this.canStrafe()) {
            final Client instance = Client.instance;
            Client.instance.getModuleManager();
            final Speed speedM = (Speed) Client.instance.getModuleManager().getModuleByClass(Speed.class);
            final double speed = MoveUtils.getSpeed();
            final Minecraft mc = TargetStrafe.mc;
            final double n = Minecraft.getMinecraft().thePlayer.posZ - this.target.posZ;
            final Minecraft mc2 = TargetStrafe.mc;
            this.degree = Math.atan2(n, Minecraft.getMinecraft().thePlayer.posX - this.target.posX);
            final double degree = this.degree;
            double n3;
            if (this.left) {
                final double n2 = speed;
                final Minecraft mc3 = TargetStrafe.mc;
                n3 = n2 / Minecraft.getMinecraft().thePlayer.getDistanceToEntity(this.target);
            } else {
                final double n4 = speed;
                final Minecraft mc4 = TargetStrafe.mc;
                n3 = -(n4 / Minecraft.getMinecraft().thePlayer.getDistanceToEntity(this.target));
            }
            this.degree = degree + n3;
            double x = this.target.posX + TargetStrafe.range.getValue() * Math.cos(this.degree);
            double z = this.target.posZ + TargetStrafe.range.getValue() * Math.sin(this.degree);
            if ((boolean) this.check.getValue() && this.needToChange(x, z)) {
                this.left = !this.left;
                final double degree2 = this.degree;
                final double n5 = 2.0;
                double n7;
                if (this.left) {
                    final double n6 = speed;
                    final Minecraft mc5 = TargetStrafe.mc;
                    n7 = n6 / mc.thePlayer.getDistanceToEntity(this.target);
                } else {
                    final double n8 = speed;
                    final Minecraft mc6 = TargetStrafe.mc;
                    n7 = -(n8 / mc.thePlayer.getDistanceToEntity(this.target));
                }
                this.degree = degree2 + n5 * n7;
                x = this.target.posX + TargetStrafe.range.getValue() * Math.cos(this.degree);
                z = this.target.posZ + TargetStrafe.range.getValue() * Math.sin(this.degree);
            }
            if (!behind.getValue()) {
                e.setX(speed * -Math.sin((float) Math.toRadians(MathUtil.toDegree(x, z))));
                e.setZ(speed * Math.cos((float) Math.toRadians(MathUtil.toDegree(x, z))));
            } else if (behind.getValue()) {
                double xPos = target.posX + -Math.sin(Math.toRadians(target.rotationYaw)) * -2;
                double zPos = target.posZ + Math.cos(Math.toRadians(target.rotationYaw)) * -2;
                e.setX(speed * -Math.sin(Math.toRadians(getRotations1(xPos, target.posY, zPos)[0])));
                e.setZ(speed * Math.cos(Math.toRadians(getRotations1(xPos, target.posY, zPos)[0])));
            }
        }
    }

    public static float[] getRotations1(double posX, double posY, double posZ) {
        EntityPlayerSP player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(y, dist) * 180.0D / Math.PI);
        return new float[]{yaw, pitch};
    }

    public boolean canStrafe() {
        final Killaura ka = (Killaura)Client.instance.getModuleManager().getModuleByClass(Killaura.class);
        final Speed speed = (Speed)Client.instance.getModuleManager().getModuleByClass(Speed.class);
        final Flight Flight = (Flight)Client.instance.getModuleManager().getModuleByClass(Flight.class);
        return ka.isEnabled() && Killaura.curTarget != null && target != null && (speed.isEnabled() || Flight.isEnabled());
    }

    public boolean needToChange(final double x, final double z) {
        final Minecraft mc = TargetStrafe.mc;
        if (mc.thePlayer.isCollidedHorizontally) {
            final Minecraft mc2 = TargetStrafe.mc;
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                return true;
            }
        }
        final Minecraft mc3 = TargetStrafe.mc;
        int i = (int)(mc.thePlayer.posY + 4.0);
        while (i >= 0) {
            final BlockPos playerPos = new BlockPos(x, i, z);
            final Minecraft mc4 = TargetStrafe.mc;
            if (!mc.theWorld.getBlockState(playerPos).getBlock().equals(Blocks.lava)) {
                final Minecraft mc5 = TargetStrafe.mc;
                if (!mc.theWorld.getBlockState(playerPos).getBlock().equals(Blocks.fire)) {
                    final Minecraft mc6 = TargetStrafe.mc;
                    if (!mc.theWorld.isAirBlock(playerPos)) {
                        return false;
                    }
                    --i;
                    continue;
                }
            }
            return true;
        }
        return true;
    }

    static {
        TargetStrafe.range = new Numbers<Double>("Range", "range", 3.0, 0.1, 6.0, 0.1);
        TargetStrafe.hue = 0.0f;
    }
}

