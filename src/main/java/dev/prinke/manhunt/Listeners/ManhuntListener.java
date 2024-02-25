package dev.prinke.manhunt.Listeners;

import dev.prinke.manhunt.Commands.ManhuntCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ManhuntListener implements Listener {

    private ManhuntCommand manhunt;
    private ArrayList<Player> joined = new ArrayList<Player>();
    private ArrayList<Player> runners;
    private HashMap<UUID, Player> closestRunners = new HashMap<>();

    public ManhuntListener(ManhuntCommand manhunt) {
        this.manhunt = manhunt;
        this.runners = this.manhunt.getRunners();
    }

    private Player getNearestPlayer(Player player) {

        double closestDistance = Double.MAX_VALUE;
        Player closestPlayer = null;

        for (Player runner : runners) {
            // Check if the runner is in the same world as the player
            if (runner.getWorld().equals(player.getWorld())) {
                double distance = runner.getLocation().distance(player.getLocation());

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = runner;
                }
            }
        }

        return closestPlayer;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory inventory = player.getInventory();

            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() == Material.COMPASS) {
                    ItemMeta meta = item.getItemMeta();

                    if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals("§ePlayer Tracker")) {
                        Player closestPlayer = getNearestPlayer(player);

                        if (closestPlayer != null) {
                            player.setCompassTarget(closestPlayer.getLocation());
                            closestRunners.put(player.getUniqueId(), closestPlayer);
                        } else {
                            // Make the compass spin in a circle
                            player.setCompassTarget(player.getLocation());
                        }
                    }
                }
            }
        }

        Player p = e.getPlayer();
        if (p.getWorld().getEnvironment() == World.Environment.NETHER) {
            if (runners.contains(p)) {
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.GLOWING, 999999, 1));
            }
        } else {
            try {
                p.removePotionEffect(org.bukkit.potion.PotionEffectType.GLOWING);
            } catch (Exception ignored) {}
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!joined.contains(p)) {
            p.teleport(p.getWorld().getSpawnLocation());
            joined.add(p);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (runners.contains(e.getEntity())) {
            Player p = (Player) e.getEntity();
            if (p.getHealth() - e.getFinalDamage() <= 4) {
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, 999999, 3));
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE, 999999, 1));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (runners.contains(p)) {
            runners.remove(p);
            e.setDeathMessage("§c" + e.getDeathMessage());
            p.setGameMode(org.bukkit.GameMode.SPECTATOR);
            if (runners.isEmpty()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle("§6§lHunters win!", "§7All runners have been eliminated", 0, 70, 0);
                }
            }

        }
    }

}
