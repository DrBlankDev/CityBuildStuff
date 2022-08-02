package at.blank.citybuildstuff.Commands.Coins;

import at.blank.citybuildstuff.CityBuildStuff;
import at.blank.citybuildstuff.Commands.CommandVanish;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandCoins implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(CityBuildStuff.getInstance(), () -> {
            if (args.length == 0) {
                if (s instanceof Player) {
                    Player p = (Player) s;
                    CoinApi Coins = new CoinApi(p.getUniqueId());

                    p.sendMessage((ChatColor.translateAlternateColorCodes('&',
                            CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Du hast§2 " + Coins.format(Coins.getCoins())
                            + " " + (ChatColor.translateAlternateColorCodes('&',
                            CityBuildStuff.getInstance().getConfig().getString("Coins.prefix")))));
                } else {
                    s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                            CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§cSyntax: §4/" + label + " <Spieler>"));
                }

            } else if (args.length == 1) {
                OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);

                if (!t.isOnline()) {
                    s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                            CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Der Spieler §2 " + args[0] + " §7ist offline"));
                    return;
                }

                if (s instanceof Player)
                    if (!((Player) s).canSee((Player) t)) {
                        s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Der Spieler §2 " + args[0] + " §7ist offline"));
                        return;
                    }

                CoinApi Coins = new CoinApi(t.getUniqueId());

                s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Der Spieler §6" + t.getPlayer().getName() + " §7hat§6" + "§2 " + Coins.format(Coins.getCoins()) + " " + " " + (ChatColor.translateAlternateColorCodes('&',
                        CityBuildStuff.getInstance().getConfig().getString("Coins.prefix")))));


            } else if (args.length == 3) {
                double CoinsAmount;

                try {
                    CoinsAmount = Double.parseDouble(args[2].replace(".", ""));
                } catch (NumberFormatException ignored) {
                    s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                            CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§4" + args[2] + " §cist keine gültige Zahl!"));
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "set":
                        if (s.hasPermission(CityBuildStuff.getInstance().getConfig().getString("Permissions.setcoins"))) {
                            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);

                            CoinApi Coins = new CoinApi(t.getUniqueId());

                            Coins.setCoins(CoinsAmount);
                            s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                    CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Du hast die " + (ChatColor.translateAlternateColorCodes('&',
                                    CityBuildStuff.getInstance().getConfig().getString("Coins.prefix"))) + " von §2" + t.getName() + " §7auf §2" + Coins.format(CoinsAmount) + " §7gesetzt!"));
                        } else {
                            s.sendMessage(CityBuildStuff.getInstance().getConfig().getString("Prefix.noperm"));
                        }
                        break;
                    case "remove":
                        if (s.hasPermission(CityBuildStuff.getInstance().getConfig().getString("Permissions.removecoins"))) {
                            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);

                            CoinApi Coins = new CoinApi(t.getUniqueId());
                            if (Coins.removeCoins(CoinsAmount)) {
                                s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Du hast dem Spieler§2 " + t.getName() + " " + Coins.format(CoinsAmount) + " " + (ChatColor.translateAlternateColorCodes('&',
                                        CityBuildStuff.getInstance().getConfig().getString("Coins.prefix"))) + " §7entfernt"));
                            } else {
                                s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Der Spieler §2 " + t.getName() + " §7hat nicht genügend " + (ChatColor.translateAlternateColorCodes('&',
                                        CityBuildStuff.getInstance().getConfig().getString("Coins.prefix")))));
                            }


                        } else {
                            s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                    CityBuildStuff.getInstance().getConfig().getString("Prefix.noperm"))));
                        }
                        break;
                    case "add":
                        if (s.hasPermission(CityBuildStuff.getInstance().getConfig().getString("Permissions.addcoins"))) {
                            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
                            if (t == null) {
                                s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                        CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Der Spieler §2" + args[0] + "§7ist offline"));
                                return;
                            }

                            CoinApi Coins = new CoinApi(t.getUniqueId());

                            Coins.addCoins(CoinsAmount);

                            s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                    CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§7Du hast dem Spieler§2 " + t.getName() + " " + Coins.format(CoinsAmount) + " " + (ChatColor.translateAlternateColorCodes('&',
                                    CityBuildStuff.getInstance().getConfig().getString("Coins.prefix"))) + " §7gegeben"));


                        } else {
                            s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                    CityBuildStuff.getInstance().getConfig().getString("Prefix.noperm"))));
                        }
                        break;

                    default:
                        s.sendMessage((ChatColor.translateAlternateColorCodes('&',
                                CityBuildStuff.getInstance().getConfig().getString("Prefix.prefix")) + " " + "§cFalscher Syntax!"));
                        break;
                }

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
                if (CommandVanish.v.contains(players[i].getName()))
                    continue;
                playerNames.add(players[i].getName());
            }
            return playerNames;

        }
        return null;
    }
}
