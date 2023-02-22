/*
 * Decompiled with CFR 0.150.
 */
package me.dev.foodtower.utils.skid.autunm.entity.impl;

import me.dev.foodtower.utils.skid.autunm.entity.ICheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public final class AliveCheck
implements ICheck {
    @Override
    public boolean validate(Entity entity) {
        return entity.isEntityAlive() || Minecraft.getMinecraft().getCurrentServerData().serverIP.contains("mineplex");
    }
}

