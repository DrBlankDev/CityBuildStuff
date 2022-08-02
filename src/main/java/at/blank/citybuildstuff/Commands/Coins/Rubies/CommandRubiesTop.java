package at.blank.citybuildstuff.Commands.Coins.Rubies;

import at.blank.citybuildstuff.CityBuildStuff;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.LinkedList;
import java.util.UUID;

public class CommandRubiesTop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(CityBuildStuff.getInstance(), () -> {
            LinkedList<UUID> topTen = RubieAPI.getTopTen();

            if (topTen.size() <= 0) {
                s.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§cEs konnte bisher keine Top Ten zusammengestellt werden!");
                return;
            }

            s.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    CityBuildStuff.getInstance().getConfig().getString("Taller.topten")));

            s.sendMessage(" ");

            for (int i = 0, i1 = 1; i < topTen.size(); i++, i1++) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(topTen.get(i));
                RubieAPI coinAPI = new RubieAPI(player.getUniqueId());
                s.sendMessage("§c" + i1 + ". §6" + player.getName() + " §c➤ §6" + coinAPI.format(coinAPI.getRubies()));
                s.sendMessage(" ");
            }

            s.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    CityBuildStuff.getInstance().getConfig().getString("Taller.topten")));
        });

        return true;
    }
}
