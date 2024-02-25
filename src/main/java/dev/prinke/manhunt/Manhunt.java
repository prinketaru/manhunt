package dev.prinke.manhunt;

import dev.prinke.manhunt.Commands.ManhuntCommand;
import dev.prinke.manhunt.Listeners.ManhuntListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Manhunt extends JavaPlugin {

    @Override
    public void onEnable() {
        ManhuntCommand manhuntCommand = new ManhuntCommand();

        this.getCommand("manhunt").setExecutor(manhuntCommand);
        getServer().getPluginManager().registerEvents(new ManhuntListener(manhuntCommand), this);

        Bukkit.getLogger().info("[Manhunt] The Manhunt plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
