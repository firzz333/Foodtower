package me.dev.foodtower.module.modules.ghost;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventRender2D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.utils.normal.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class LegitScaffold extends Module {
    public static List<Block> blacklistedBlocks = Arrays.asList(new Block[]{Blocks.air, Blocks.water, Blocks.fire, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.chest, Blocks.enchanting_table, Blocks.sand, Blocks.tnt, Blocks.ladder, Blocks.jukebox, Blocks.noteblock, Blocks.furnace, Blocks.crafting_table, Blocks.anvil, Blocks.ender_chest, Blocks.trapped_chest});

    public LegitScaffold() {
        super("LegitScaffold", "合法桥", new String[]{"lsc"}, ModuleType.Ghost);
    }

    @NMSL
    public void onRender2D(EventRender2D event) {

    }
    public int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if (!(is.getItem() instanceof ItemBlock) || this.blacklistedBlocks.contains(((ItemBlock)item).getBlock())) continue;
            blockCount += is.stackSize;
        }
        return blockCount;
    }
    public Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return getBlock(new BlockPos(player.posX, player.posY - 1.0d, player.posZ));
    }

    @NMSL
    public void onUpdate(EventPostUpdate event) {
        if (getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir) {
            if (mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }
        } else {
            if (mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
        }
    }

    @Override
    public void onEnable() {
        mc.thePlayer.setSneaking(false);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        super.onDisable();
    }
}
