/*
Author:SuMuGod
Date:2022/7/10 3:51
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.Client;
import me.dev.foodtower.command.Command;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.utils.normal.Helper;
import net.minecraft.util.EnumChatFormatting;

public class Toggle
        extends Command {
    public Toggle() {
        super("t", new String[]{"toggle", "togl", "turnon", "enable"}, "", "Toggles a specified Module");
    }

    @Override
    public String execute(String[] args) {
        String modName = "";
        if (args.length > 1) {
            modName = args[1];
        } else if (args.length < 1) {
            Helper.sendMessageWithoutPrefix("\u00a7bCorrect usage:\u00a77 .t <module>");
        }
        boolean found = false;
        Module m = Client.instance.getModuleManager().getAlias(args[0]);
        if (m != null) {
            if (!m.isEnabled()) {
                m.setEnabled(true);
            } else {
                m.setEnabled(false);
            }
            found = true;
            if (m.isEnabled()) {
                Helper.sendMessage("> " + m.getName() + (Object) ((Object) EnumChatFormatting.GRAY) + " was" + (Object) ((Object) EnumChatFormatting.GREEN) + " enabled");
            } else {
                Helper.sendMessage("> " + m.getName() + (Object) ((Object) EnumChatFormatting.GRAY) + " was" + (Object) ((Object) EnumChatFormatting.RED) + " disabled");
            }
        }
        if (!found) {
            Helper.sendMessage("> Module name " + (Object) ((Object) EnumChatFormatting.RED) + args[0] + (Object) ((Object) EnumChatFormatting.GRAY) + " is invalid");
        }
        return null;
    }
}


