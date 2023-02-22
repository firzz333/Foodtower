/*
 * Decompiled with CFR 0.150.
 */
package me.dev.foodtower.utils.skid.autunm.entity.impl;

import java.util.function.Supplier;

import me.dev.foodtower.other.FriendManager;
import me.dev.foodtower.utils.skid.autunm.entity.ICheck;
import me.dev.foodtower.value.Option;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public final class EntityCheck
implements ICheck {
    private final Option<Boolean> players;
    private final Option<Boolean> animals;
    private final Option<Boolean> monsters;
    private final Option<Boolean> invisibles;
    private final Supplier<Boolean> friend;

    public EntityCheck(Option<Boolean> players, Option<Boolean> animals, Option<Boolean> monsters, Option<Boolean> invisibles, Supplier<Boolean> friend) {
        this.players = players;
        this.animals = animals;
        this.monsters = monsters;
        this.invisibles = invisibles;
        this.friend = friend;
    }

    @Override
    public boolean validate(Entity entity) {
        if (entity instanceof EntityPlayerSP) {
            return false;
        }
        if (!this.invisibles.getValue().booleanValue() && entity.isInvisible()) {
            return false;
        }
        if (this.animals.getValue().booleanValue() && entity instanceof EntityAnimal) {
            return true;
        }
        if (this.players.getValue().booleanValue() && entity instanceof EntityPlayer) {
            return !FriendManager.isFriend(entity.getName()) || this.friend.get() == false;
        }
        return this.monsters.getValue() != false && (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityDragon || entity instanceof EntityGolem);
    }
}

