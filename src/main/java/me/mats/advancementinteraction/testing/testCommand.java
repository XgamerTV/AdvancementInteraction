package me.mats.advancementinteraction.testing;

import me.mats.advancementinteraction.AdvancementInteraction;
import me.mats.advancementinteraction.TeamAdvancements;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class testCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (commandSender instanceof Player p) {
            TeamAdvancements teamAdvancements = new TeamAdvancements("bingo", "white_concrete");
            teamAdvancements.sendRootAdvancement(p);
            AdvancementInteraction.getInstance().addBingoPlayer(p);
        }
        return true;
    }
}
