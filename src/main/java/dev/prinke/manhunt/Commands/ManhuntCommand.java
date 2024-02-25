package dev.prinke.manhunt.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ManhuntCommand implements CommandExecutor {

    // give a player a tracking compass
    public void giveTrackingCompass(Player player) {
        ItemStack compass = createTrackingCompass();
        player.getInventory().addItem(compass);
    }

    // create a tracking compass
    private ItemStack createTrackingCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        compassMeta.setDisplayName("§ePlayer Tracker");
        compass.setItemMeta(compassMeta);
        return compass;
    }

    public ArrayList<Player> runners = new ArrayList<>();

    public ArrayList<Player> getRunners() {
        return this.runners;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // check if strings is empty
        if (strings.length == 0) {
            commandSender.sendMessage("§c§lError: §7Please specify a subcommand");
            return true;
        }

        // if subcommand is start
        if (strings[0].equalsIgnoreCase("start")) {

            if (runners.isEmpty()) {
                commandSender.sendMessage("§c§lError: §7No runners have been added");
                return true;
            }

            commandSender.sendMessage("§a§lSuccess: §7The game has started");

            // get all players in the server
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!runners.contains(p)) {
                    giveTrackingCompass(p);
                }
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("§c§lThe hunt has begun!", "§7Good luck!", 0, 70, 0);
                if (!runners.contains(p)) {
                    // give blindness, mining fatigue, and slowness for 2 minutes
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS, 2400, 0));
                } else {
                    // give permanent Resistance IV and Strength II
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.DAMAGE_RESISTANCE, 999999, 3));
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE, 999999, 1));
                }
            }
        } else if (strings[0].equalsIgnoreCase("runner")) {
            if (strings[1].equalsIgnoreCase("add")) {
                Player p = Bukkit.getPlayer(strings[2]);

                if (p == null) {
                    commandSender.sendMessage("§c§lError: §7" + strings[2] + " is not online");
                    return true;
                }

                runners.add(p);

                commandSender.sendMessage("§a§lSuccess: §7Added " + p.getName() + " to the runners list");
            } else if (strings[1].equalsIgnoreCase("remove")) {
                Player p = Bukkit.getPlayer(strings[2]);

                if (!runners.contains(p)) {
                    commandSender.sendMessage("§c§lError: §7" + p.getName() + " is not a runner");
                    return true;
                }

                runners.remove(p);

                commandSender.sendMessage("§a§lSuccess: §7Removed " + p.getName() + " from the runners list");
            } else {
                commandSender.sendMessage("§c§lError: §7Invalid subcommand");
            }
        } else {
            commandSender.sendMessage("§c§lError: §7Invalid subcommand");
        }

        return true;
    }
}
