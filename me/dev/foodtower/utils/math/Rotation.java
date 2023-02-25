package me.dev.foodtower.utils.math;

import me.dev.foodtower.utils.normal.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class Rotation {
    private float yaw;
    private static float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotation(Entity ent) {
        this.yaw = ent.rotationYaw;
        this.pitch = ent.rotationPitch;
    }

    public void add(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;
    }

    public void remove(float yaw, float pitch) {
        this.yaw -= yaw;
        this.pitch -= pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}

