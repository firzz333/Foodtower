/*
Author:SuMuGod
Date:2022/7/10 3:16
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class EventCollideWithBlock
        extends Event {
    public AxisAlignedBB boundingBox;
    private Block block;
    private BlockPos blockPos;

    public EventCollideWithBlock(Block block, BlockPos pos, AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPos getPos() {
        return this.blockPos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
