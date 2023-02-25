/*
Author:SuMuGod
Date:2022/7/10 5:11
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.AutoPot;
import me.dev.foodtower.utils.math.MathUtils;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChestStealer extends Module {
    private final Numbers<Double> delay = new Numbers<>("Delay", "delay", 100.0,0.0,500.0,10.0);
    private final Option<Boolean> menuCheck = new Option<>("MenuCheck", "menucheck", true);
    private final Option<Boolean> autoClose = new Option<>("AutoClose", "autoclose", true);
    private final TimerUtil timerUtils = new TimerUtil();

    public ChestStealer() {
        super("ChestStealer", "自动搜箱", new String[]{"cheststeal", "chests", "stealer"}, ModuleType.World);
        setKey(Keyboard.KEY_C);
    }

    @NMSL
    private void onTick(EventTick e) {
        if (mc.thePlayer.openContainer instanceof ContainerChest) {
            final ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;

            if (menuCheck.getValue() && !StatCollector.translateToLocal("container.chest").equalsIgnoreCase(container.getLowerChestInventory().getDisplayName().getUnformattedText())) {
                if (!container.getLowerChestInventory().getDisplayName().getFormattedText().contains("étoiles dans ce coffre")) {
                    return;
                }
            }

            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                if (container.getLowerChestInventory().getStackInSlot(i) == null || !timerUtils.hasReached(delay.getValue())) continue;

                Minecraft.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer);
            }

            if (autoClose.getValue() && isEmpty()) {
                mc.thePlayer.closeScreen();
            }
        }
    }

    private boolean isEmpty() {
        if (mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)mc.thePlayer.openContainer;
            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
                if (itemStack == null || itemStack.getItem() == null) continue;
                return false;
            }
        }
        return true;
    }
}
