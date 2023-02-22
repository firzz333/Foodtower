package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class KeepSprint extends Module {
    public KeepSprint() {
        super("KeepSprint", "持续奔放", new String[]{"kp"}, ModuleType.Combat);
    }

    @NMSL
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C0BPacketEntityAction && ((C0BPacketEntityAction) e.getPacket()).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
            e.setCancelled(true);
        }
    }
}
