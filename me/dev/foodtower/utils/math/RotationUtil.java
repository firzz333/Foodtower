/*
Author:SuMuGod
Date:2022/7/10 4:14
Project:foodtower Reborn
*/
package me.dev.foodtower.utils.math;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.utils.normal.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.optifine.vecmath.Vector3d;

import java.util.Random;

public class RotationUtil {
    public static float pitch() {
        return Helper.mc.thePlayer.rotationPitch;
    }
    private static int keepLengt;
    public static void pitch(float pitch) {
        Helper.mc.thePlayer.rotationPitch = pitch;
    }
    public static Rotation targetRotation;
    private static Random random = new Random();
    private static double x = random.nextDouble();
    private static double y = random.nextDouble();
    private static double z = random.nextDouble();
    public static float yaw() {
        return Helper.mc.thePlayer.rotationYaw;
    }

    public static void yaw(float yaw) {
        Helper.mc.thePlayer.rotationYaw = yaw;
    }
    public static void reset() {
        keepLengt = 0;
        targetRotation = null;
    }
    public static float getSensitivityMultiplier() {
        float SENSITIVITY = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        return (SENSITIVITY * SENSITIVITY * SENSITIVITY * 8.0F) * 0.15F;
    }

    @NMSL
    public void onTick(final EventTick event) {
        if(targetRotation != null) {
            keepLengt--;

            if (keepLengt<= 0)
                reset();
        }

        if(random.nextGaussian() > 0.8D) x = Math.random();
        if(random.nextGaussian() > 0.8D) y = Math.random();
        if(random.nextGaussian() > 0.8D) z = Math.random();
    }
    public static void setTargetRotation(final Rotation rotation) {
        setTargetRotation(rotation, 0);
    }
    public static void setTargetRotation(final Rotation rotation, int keepLength) {
        if(Double.isNaN(rotation.getYaw()) || Double.isNaN(rotation.getPitch())
                || rotation.getPitch() > 90 || rotation.getPitch() < -90)
            return;

        targetRotation = rotation;
        keepLengt = keepLength;
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapAngleTo180_float(to - from);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return from + f;
    }

    public static float[] faceTarget(Entity target, float p_706252, float p_706253, boolean miss) {
        double var6;
        double var4 = target.posX - Helper.mc.thePlayer.posX;
        double var8 = target.posZ - Helper.mc.thePlayer.posZ;
        if (target instanceof EntityLivingBase) {
            EntityLivingBase var10 = (EntityLivingBase) target;
            var6 = var10.posY + (double) var10.getEyeHeight() - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        } else {
            var6 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        }
        Random rnd = new Random();
        double var14 = MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float) (Math.atan2(var8, var4) * 180.0 / 3.141592653589793) - 90.0f;
        float var13 = (float) (-Math.atan2(var6 - (target instanceof EntityPlayer ? 0.25 : 0.0), var14) * 180.0 / 3.141592653589793);
        float pitch = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationPitch, var13, p_706253);
        float yaw = RotationUtil.changeRotation(Helper.mc.thePlayer.rotationYaw, var12, p_706252);
        return new float[]{yaw, pitch};
    }


    public static float[] getPredictedRotations(Entity ent) {
        double x = ent.posX + (ent.posX - ent.lastTickPosX);
        double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static float[] getNeededRotations(final Vector3d current, final Vector3d target) {
        final double diffX = target.x - current.x;
        final double diffY = target.y - current.y;
        final double diffZ = target.z - current.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

    public static float changeRotation(float p_706631, float p_706632, float p_706633) {
        float var4 = MathHelper.wrapAngleTo180_float(p_706632 - p_706631);
        if (var4 > p_706633) {
            var4 = p_706633;
        }
        if (var4 < -p_706633) {
            var4 = -p_706633;
        }
        return p_706631 + var4;
    }

    public static double[] getRotationToEntity(Entity entity) {
        double pX = Helper.mc.thePlayer.posX;
        double pY = Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight();
        double pZ = Helper.mc.thePlayer.posZ;
        double eX = entity.posX;
        double eY = entity.posY + (double) (entity.height / 2.0f);
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new double[]{yaw, 90.0 - pitch};
    }

    public static float[] getRotations(Entity entity) {
        double diffY;
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - Helper.mc.thePlayer.posX;
        double diffZ = entity.posZ - Helper.mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) entity;
            diffY = elb.posY + ((double) elb.getEyeHeight() - 0.4) - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (Helper.mc.thePlayer.posY + (double) Helper.mc.thePlayer.getEyeHeight());
        }
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    public static float[] grabBlockRotations(BlockPos pos) {
        return RotationUtil.getVecRotation(Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0), new Vec3((double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5));
    }

    public static float[] getVecRotation(Vec3 position) {
        return RotationUtil.getVecRotation(Helper.mc.thePlayer.getPositionVector().addVector(0.0, Helper.mc.thePlayer.getEyeHeight(), 0.0), position);
    }

    public static float[] getVecRotation(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        double distance = difference.flat().lengthVector();
        float yaw = (float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[]{yaw, pitch};
    }

    public static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int) ((double) (yaw + (float) (360 / (2 * zones))) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }
}
