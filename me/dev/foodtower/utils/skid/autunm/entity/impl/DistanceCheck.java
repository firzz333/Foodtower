/*
 * Decompiled with CFR 0.150.
 */
package me.dev.foodtower.utils.skid.autunm.entity.impl;

import me.dev.foodtower.value.Numbers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import me.dev.foodtower.utils.skid.autunm.entity.ICheck;

public final class DistanceCheck
implements ICheck {
    private final Numbers<Double> distance;

    public DistanceCheck(Numbers<Double> distance) {
        this.distance = distance;
    }

    @Override
    public boolean validate(Entity entity) {
        return (double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) <= (Double)this.distance.getValue();
    }
}

