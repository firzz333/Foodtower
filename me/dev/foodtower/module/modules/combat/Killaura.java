package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.world.Teams;
import me.dev.foodtower.other.FriendManager;
import me.dev.foodtower.utils.math.MathUtil;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.utils.normal.Helper;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Killaura extends Module {

    public static EntityLivingBase curTarget;
    public static float[] rotations;
    private final Mode<Enum> mode = new Mode<Enum>("Mode", "mode", AuraMode.values(), AuraMode.Switch);
    private final Mode<Enum> attacktime = new Mode<>("AttackMode", "attackmode", attackMode.values(), attackMode.Post);
    private final Numbers<Double> cps = new Numbers<Double>("CPS", "cps", 10.0, 1.0, 20.0, 0.5);
    public static Numbers<Double> range = new Numbers<Double>("Range", "range", 4.5, 1.0, 6.0, 0.1);
    public static boolean isBlocking;
    float anim = 100;
    private List<Entity> targets = new ArrayList();
    private int index;
    private final Numbers<Double> switchDelay = new Numbers<Double>("SwitchDelay", "switchdelay", 500.0, 100.0, 5000.0, 100.0);
    private final Option<Boolean> rot = new Option<Boolean>("Rotation", "rotation", true);
    private final Option<Boolean> esp = new Option<Boolean>("ESP", "esp", false);
    private final Option<Boolean> autoblock = new Option<Boolean>("Autoblock", "autoblock", true);
    private final Option<Boolean> random = new Option<>("RandomCPS", "ramdomcps", false);
    private Option<Boolean> breaker = new Option<>("ArmorBreaker", "armorbreaker", false);
    private final Option<Boolean> players = new Option<Boolean>("Players", "players", true);
    private final Option<Boolean> friend = new Option<Boolean>("FriendFliter", "friendfliter", true);
    private final Option<Boolean> animals = new Option<Boolean>("Animals", "animals", true);
    private final Option<Boolean> mobs = new Option<Boolean>("Mobs", "mobs", false);
    private final Option<Boolean> invis = new Option<Boolean>("Invisibles", "invisibles", false);
    private final Option<Boolean> death = new Option<Boolean>("DeathCheck", "deathcheck", true);
    private final Comparator<Entity> angleComparator = Comparator.comparingDouble(e2 -> e2.getDistanceToEntity(mc.thePlayer));
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil switchTimer = new TimerUtil();
    private Object texture;

    public Killaura() {
        super("Killaura", "戮死光环", new String[]{"ka", "aura", "killa"}, ModuleType.Combat);
    }

    public static double random(double min, double max) {
        Random random = new Random();
        return min + (double) ((int) (random.nextDouble() * (max - min)));
    }

    public static float[] getRotationsToEnt(Entity ent) {
        final double differenceX = ent.posX - mc.thePlayer.posX;
        final double differenceY = (ent.posY + ent.height) - (mc.thePlayer.posY + mc.thePlayer.height) - 0.5;
        final double differenceZ = ent.posZ - mc.thePlayer.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, mc.thePlayer.getDistanceToEntity(ent)) * 180.0D
                / Math.PI);
        final float finishedYaw = mc.thePlayer.rotationYaw
                + MathHelper.wrapAngleTo180_float(rotationYaw - mc.thePlayer.rotationYaw);
        final float finishedPitch = mc.thePlayer.rotationPitch
                + MathHelper.wrapAngleTo180_float(rotationPitch - mc.thePlayer.rotationPitch);
        return new float[]{finishedYaw, -MathHelper.clamp_float(finishedPitch, -90, 90)};
    }

    @Override
    public void onDisable() {
        curTarget = null;
        this.targets.clear();
        if (this.autoblock.getValue().booleanValue() && this.hasSword() && mc.thePlayer.isBlocking()) {
            this.unBlock();
        }
    }

    @Override
    public void onEnable() {
        curTarget = null;
        this.index = 0;
    }

    private boolean shouldAttack() {
        return (random.getValue() && this.attackTimer.hasReached(1000.0 / (this.cps.getValue() + MathUtil.randomDouble(-1.0, 1.0)))) || (!random.getValue() && this.attackTimer.hasReached(1000.0 / (this.cps.getValue())));
    }

    @NMSL
    public void onRender(EventRender3D event) {
        if (Killaura.curTarget == null)
            return;
        Color color = new Color(100, 30, 30, 10);

        if (Killaura.curTarget.hurtResistantTime > 0) {
            color = new Color(190, 30, 30, 30);
        }

        if (this.esp.getValue()) {
            for (Entity ent : this.targets) {
                Color col = new Color(170, 30, 30, 30);
                if (ent == curTarget) {
                    col = new Color(30, 200, 30, 50);
                }
                if (ent.hurtResistantTime > 0) {
                    col = new Color(255, 30, 30, 50);
                }
                RenderUtil.drawFilledESP(ent, col);
            }
            Entity player = Killaura.curTarget;
            float partialTicks = event.getPartialTicks();
            GL11.glPushMatrix();
            GL11.glDisable(3553);
            RenderUtil.startDrawing();
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(4.0f);
            GL11.glBegin(3);
            final double pix2 = 6.283185307179586;
            GL11.glEnd();
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
            RenderUtil.stopDrawing();
            GL11.glEnable(3553);
            GL11.glPopMatrix();
        }
    }

    private boolean hasSword() {
        if (mc.thePlayer.inventory.getCurrentItem() != null) {
            return mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
        } else {
            return false;
        }
    }

    @NMSL
    private void onTick(EventTick event) {
        if (this.death.getValue() && mc.thePlayer != null) {
            if ((!mc.thePlayer.isEntityAlive() || (Minecraft.currentScreen != null && Minecraft.currentScreen instanceof GuiGameOver))) {
                this.setEnabled(false);
                Helper.sendMessage("[Auto Disable] Killaura Disabled For Death.");
                return;
            }
            if (mc.thePlayer.ticksExisted <= 1) {
                this.setEnabled(false);
                Helper.sendMessage("[Auto Disable] Killaura Disabled For Death.");
            }
        }
    }

    private void block() {
        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            if (breaker.getValue()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(curTarget, C02PacketUseEntity.Action.INTERACT));
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(curTarget, C02PacketUseEntity.Action.ATTACK));
            }

            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            Minecraft.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
            if (Minecraft.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
                mc.getItemRenderer().resetEquippedProgress2();
            }
            isBlocking = true;
        }
    }

    private void unBlock() {
        if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && isBlocking) {
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
            Minecraft.playerController.onStoppedUsingItem(mc.thePlayer);
            isBlocking = false;
        }
    }

    @NMSL
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix(this.mode.getValue());

        if (curTarget != null) {
            mc.thePlayer.setSprinting(Client.instance.getModuleManager().getModuleByClass(KeepSprint.class).isEnabled());
        }

        if (curTarget == null && autoblock.getValue()) {
            if (hasSword()) {
                unBlock();
            }
        }
        if (hasSword() && curTarget != null && autoblock.getValue() && !isBlocking) {
            this.block();
        }
        this.targets = this.getTargets(range.getValue());

        targets.sort(this.angleComparator);

        if (this.targets.size() > 1 && this.mode.getValue() == AuraMode.Switch) {
            if (mc.thePlayer.ticksExisted % 2 == 1) {

                if (curTarget == null) {
                    curTarget = (EntityLivingBase) this.targets.get(0);
                }

                if (curTarget.hurtTime > 0 || switchTimer.hasReached(200)) {
                    ++this.index;
                    this.switchTimer.reset();
                }
            }
        }

        if (mc.thePlayer.ticksExisted % switchDelay.getValue().intValue() == 0 && this.targets.size() > 1
                && this.mode.getValue() == AuraMode.Single) {

            if (curTarget.getDistanceToEntity(mc.thePlayer) > range.getValue()) {
                ++index;
            } else if (curTarget.isDead) {
                ++index;
            }
        }

        if (curTarget != null) {
            if (attacktime.getValue() == attackMode.Pre && this.shouldAttack()) {
                if (this.hasSword() && mc.thePlayer.isBlocking() && this.isValidEntity(curTarget)) {
                    unBlock();
                }
                this.attack();
                this.attackTimer.reset();
                if (!mc.thePlayer.isBlocking() && this.hasSword() && autoblock.getValue().booleanValue()) {
                    this.block();
                }
            }
            curTarget = null;
        }

        if (!this.targets.isEmpty()) {
            if (this.index >= this.targets.size()) {
                this.index = 0;
            }
            curTarget = (EntityLivingBase) this.targets.get(this.index);
            if (rot.getValue()) {
                rotations = getRotationsToEnt(curTarget);
                mc.thePlayer.rotationYawHead = getRotationsToEnt(curTarget)[0];
                mc.thePlayer.renderYawOffset = getRotationsToEnt(curTarget)[0];
                mc.thePlayer.prevRenderYawOffset = getRotationsToEnt(curTarget)[0];
                event.setYaw(getRotationsToEnt(curTarget)[0]);
                event.setPitch(getRotationsToEnt(curTarget)[1]);
            } else {
                event.setYaw(event.getYaw());
                event.setPitch(event.getPitch());
            }
        }
    }

    @NMSL
    private void onUpdatePost(EventPostUpdate e) {
        if (attacktime.getValue() == attackMode.Post) {
            if (curTarget != null) {
                if (this.shouldAttack()) {
                    if (this.hasSword() && mc.thePlayer.isBlocking() && this.isValidEntity(curTarget)) {
                        unBlock();
                    }
                    this.attack();
                    this.attackTimer.reset();
                }
                if (!mc.thePlayer.isBlocking() && this.hasSword() && autoblock.getValue().booleanValue()) {
                    this.block();
                }
            }
        }
    }

    private void attack() {
        mc.thePlayer.swingItem();
        mc.thePlayer.onEnchantmentCritical(curTarget);
        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(curTarget, C02PacketUseEntity.Action.ATTACK));
    }

    public List<Entity> getTargets(Double value) {
        return mc.theWorld.loadedEntityList.stream()
                .filter(e -> (double) mc.thePlayer.getDistanceToEntity(e) <= value && isValidEntity(e))
                .collect(Collectors.toList());
    }

    private boolean isValidEntity(Entity ent) {

        if (ent == mc.thePlayer)
            return false;

        if (mc.thePlayer.getDistanceToEntity(ent) > range.getValue())
            return false;

        if (ent.isInvisible() && !invis.getValue())
            return false;

        if (!ent.isEntityAlive())
            return false;

        if (FriendManager.isFriend(ent.getName()))
            return false;

        if (ent instanceof EntityPlayer && FriendManager.isFriend(ent.getDisplayName().getUnformattedText())
                && this.friend.getValue())
            return false;

        if ((ent instanceof EntityMob || ent instanceof EntityGhast || ent instanceof EntityGolem
                || ent instanceof EntityDragon || ent instanceof EntitySlime) && mobs.getValue())
            return true;

        if ((ent instanceof EntitySquid || ent instanceof EntityBat || ent instanceof EntityVillager)
                && animals.getValue())
            return true;

        if (ent instanceof EntityAnimal && animals.getValue())
            return true;

        AntiBot ab = (AntiBot) Client.instance.getModuleManager().getModuleByClass(AntiBot.class);
        if (AntiBot.isServerBot(ent))
            return false;

        return ent instanceof EntityPlayer && players.getValue() && !Teams.isOnSameTeam(ent);
    }

    enum AuraMode {
        Switch, Single,
    }

    enum attackMode {
        Pre, Post
    }
}
