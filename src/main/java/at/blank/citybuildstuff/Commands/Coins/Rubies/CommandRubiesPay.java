package at.blank.citybuildstuff.Commands.Coins.Rubies;

import at.blank.citybuildstuff.CityBuildStuff;
import at.blank.citybuildstuff.Commands.CommandVanish;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandRubiesPay implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(CityBuildStuff.getInstance(), () -> {
            if (!(cs instanceof Player)) {
                cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "§cDafür musst du ein Spieler sein!");
                return;
            }

            if (args.length <= 0) {
                cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + label + " <Spieler> <Betrag>");
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "§cDer Spieler §4" + args[0] + " §cist nicht online!");
                return;
            }

            if (!((Player) cs).canSee(target)) {
                cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "§cDer Spieler §4" + args[0] + " §cist nicht online!");
                return;
            }

            double coins;

            try {
                coins = Double.parseDouble(args[1].replace(".", ""));
            } catch (NumberFormatException ignored) {
                cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "§4" + args[1] + " §cist keine gültige Zahl!");
                return;
            }

            if (coins <= 0) {
                cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "§4" + coins + " §cist keine gültige Zahl, bitte gib eine positive Zahl über 0 ein!");
                return;
            }

            RubieAPI coinAPISender = new RubieAPI(((Player) cs).getUniqueId());

            if (coinAPISender.getRubies() < coins) {
                cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "§cDafür hast du nicht genügend " + (ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Taller.prefix"))));
                return;
            }

            cs.sendMessage((ChatColor.translateAlternateColorCodes('&',
                    CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "Du hast §2" + coinAPISender.format(coins) + " " + (ChatColor.translateAlternateColorCodes('&',
                    CityBuildStuff.getInstance().getConfig().getString("Taller.prefix"))) + " " + "an §2" + target.getName() + " §7überwiesen!");

            RubieAPI coinAPITarget = new RubieAPI(target.getUniqueId());

            target.sendMessage((ChatColor.translateAlternateColorCodes('&',
                    CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix"))) + " " + "§2" + cs.getName() + " §7hat dir §2" + coinAPITarget.format(coins) + " " + (ChatColor.translateAlternateColorCodes('&',
                    CityBuildStuff.getInstance().getConfig().getString("Taller.prefix"))) + "" + " überwiesen!");

            if (target != cs) {
                coinAPISender.removeRubies(coins);
                coinAPITarget.addRubies(coins);
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (int i = 0; i < players.length; i++) {
                if(CommandVanish.v.contains(players[i].getName()))
                    continue;
                playerNames.add(players[i].getName());
            }
            return playerNames;

        }
        return null;
    }
}
