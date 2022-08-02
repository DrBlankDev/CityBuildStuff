package at.blank.citybuildstuff.Commands.Coins.Rubies;

import at.blank.citybuildstuff.CityBuildStuff;
import at.blank.citybuildstuff.Events.AsyncOrSyncCoinEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RubieAPI {

    private final UUID uuid;
    private static LuckPerms api;


    public RubieAPI(UUID uuid) {
        this.uuid = uuid;
        if (api == null) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                api = provider.getProvider();
            }
        }
    }

    public static LinkedList getTopTen() {
        ResultSet resultSet = CityBuildStuff.getMySQL().executeQuery("SELECT * FROM taller ORDER BY Balance DESC LIMIT 30");
        if (resultSet == null)
            return new LinkedList();
        try {
            LinkedList<UUID> topTen = new LinkedList();
            int i = 0;
            while (resultSet.next() && i != 10) {
                UUID uuid = UUID.fromString(resultSet.getString("UUID"));
                UserManager userManager = api.getUserManager();
                CompletableFuture<User> userFuture = userManager.loadUser(uuid);
                AtomicBoolean team = new AtomicBoolean(false);
                userFuture.thenAcceptAsync(user -> {
                    Stream<Node> var10000 = user.getNodes().stream();
                    NodeType<PermissionNode> var10001 = NodeType.PERMISSION;
                    Objects.requireNonNull(var10001);
                    Objects.requireNonNull(var10001);
                    var10000 = var10000.filter(var10001::matches);
                    var10001 = NodeType.PERMISSION;
                    Objects.requireNonNull(var10001);
                    Objects.requireNonNull(var10001);
                    Set<String> permissions = (Set<String>)var10000.map(var10001::cast).map(PermissionNode::getPermission).collect(Collectors.toSet());
                    team.set(permissions.stream().anyMatch(s -> s.equalsIgnoreCase(CityBuildStuff.getInstance().getConfig().getString("Permissions.removeteamtaller"))));
                }).get();
                if (!team.get()) {
                    i++;
                    topTen.add(uuid);
                }
            }
            return topTen;
        } catch (ExecutionException |InterruptedException| SQLException var7) {
            var7.printStackTrace();
            return new LinkedList();
        }
    }

    public String format(double rubies) {
        //BigDecimal bd = new BigDecimal(300000);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("de_DE"));
        return formatter.format(rubies).replace(",", ".");
    }

    public double getRubies() {
        if (!hasAccount()) {
            return 0;
        }
        ResultSet resultSet = CityBuildStuff.getMySQL().executeQuery("SELECT Balance FROM taller WHERE UUID = '" + uuid.toString() + "'");
        try {
            resultSet.next();
            return Double.parseDouble(resultSet.getString("Balance"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    public void setRubies(double taller2) {
        if (!hasAccount()) {
            CityBuildStuff.getMySQL().execute("INSERT INTO taller (UUID, Balance) VALUES ('" + uuid.toString() + "','1000')");
        }

        CityBuildStuff.getMySQL().execute("UPDATE taller SET Balance='" + taller2 + "' WHERE UUID='" + uuid.toString() + "'");

        AsyncOrSyncCoinEvent coinEvent = new AsyncOrSyncCoinEvent(Bukkit.getPlayer(uuid), taller2, !Bukkit.isPrimaryThread());
        Bukkit.getPluginManager().callEvent(coinEvent);
    }

    public boolean hasAccount() {
        ResultSet resultSet = CityBuildStuff.getMySQL().executeQuery("SELECT Balance FROM taller WHERE UUID = '" + uuid.toString() + "'");
        try {
            if (!resultSet.next()) {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean removeRubies(double Rubies) {
        double difference = getRubies() - Rubies;

        if (difference >= 0) {
            setRubies(difference);
            return true;
        }

        return false;
    }

    public boolean addRubies(double rubies) {
        setRubies(getRubies() + rubies);
        return true;
    }

}
