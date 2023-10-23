package me.mats.advancementinteraction;


import me.mats.advancementinteraction.testing.testCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public final class AdvancementInteraction extends JavaPlugin {

    private final List<Player> bingoPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info(ChatColor.GREEN+"AdvancementInteraction API loaded");
        Objects.requireNonNull(getCommand("adv_test")).setExecutor(new testCommand());

        //ProtocolLib stuff
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
            ProtocolLibHook.register();
        } else Bukkit.getLogger().warning("ProtocolLib is not installed...Plugin may not have full functionality");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AdvancementInteraction getInstance() {
        return getPlugin(AdvancementInteraction.class);
    }

    public void addBingoPlayer(Player p) {
        bingoPlayers.add(p);
    }

    public void removeBingoPlayer(Player p) {
        bingoPlayers.remove(p);
    }

    public boolean isPlayingBingo(Player p) {
        return bingoPlayers.contains(p);
    }

}
