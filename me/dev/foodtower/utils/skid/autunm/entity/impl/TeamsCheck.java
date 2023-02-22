/*
 * Decompiled with CFR 0.150.
 */
package me.dev.foodtower.utils.skid.autunm.entity.impl;

import me.dev.foodtower.module.modules.world.Teams;
import me.dev.foodtower.utils.skid.autunm.entity.ICheck;
import me.dev.foodtower.value.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public final class TeamsCheck implements ICheck {
    private final Option<Boolean> teams;

    public TeamsCheck(Option<Boolean> teams) {
        this.teams = teams;
    }

    @Override
    public boolean validate(Entity entity) {
        return !(entity instanceof EntityPlayer) || !Teams.isOnSameTeam((EntityPlayer) entity) || this.teams.getValue() == false;
    }
}

