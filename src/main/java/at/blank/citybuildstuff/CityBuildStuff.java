package at.blank.citybuildstuff;

import at.blank.citybuildstuff.Commands.BackPack.Manager.BackPackManager;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.CommandBan;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.CommandCheck;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.CommandTempBan;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.CommandUnban;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.Mute.CommandCheckMute;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.Mute.CommandMute;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.Mute.CommandTempMute;
import at.blank.citybuildstuff.Commands.BanSystem.Commands.Mute.CommandUnMute;
import at.blank.citybuildstuff.Commands.BanSystem.FileManager;
import at.blank.citybuildstuff.Commands.BlackMarket.BlackMarketEntity;
import at.blank.citybuildstuff.Commands.Clans.Clan_command;
import at.blank.citybuildstuff.Commands.Coins.CommandCoins;
import at.blank.citybuildstuff.Commands.Coins.CommandCoinsTop;
import at.blank.citybuildstuff.Commands.Coins.CommandPay;
import at.blank.citybuildstuff.Commands.Coins.Rubies.CommandRubies;
import at.blank.citybuildstuff.Commands.Coins.Rubies.CommandRubiesPay;
import at.blank.citybuildstuff.Commands.Coins.Rubies.CommandRubiesTop;
import at.blank.citybuildstuff.Commands.*;
import at.blank.citybuildstuff.Commands.DailyRewards.CommandDailyRewards;
import at.blank.citybuildstuff.Commands.DailyRewards.RewardFileManager;
import at.blank.citybuildstuff.Commands.DailyRewards.RewardManager;
import at.blank.citybuildstuff.Commands.Glow.CommandGlow;
import at.blank.citybuildstuff.Commands.Glow.CommandUnGlow;
import at.blank.citybuildstuff.Commands.HelpCommand.*;
import at.blank.citybuildstuff.Commands.Home.CommandDelHome;
import at.blank.citybuildstuff.Commands.Home.CommandHome;
import at.blank.citybuildstuff.Commands.Home.CommandSetHome;
import at.blank.citybuildstuff.Commands.Msg.CommandMsg;
import at.blank.citybuildstuff.Commands.Msg.CommandReply;
import at.blank.citybuildstuff.Commands.Msg.MessageManager;
import at.blank.citybuildstuff.Commands.RangShop.RangVillager;
import at.blank.citybuildstuff.Commands.RangUpgrades.RangUpgrades;
import at.blank.citybuildstuff.Commands.Shop.IngameSell.CommandSell;
import at.blank.citybuildstuff.Commands.Shop.IngameSell.VillagerSellerHandler;
import at.blank.citybuildstuff.Commands.Shop.IngameSell.build.*;
import at.blank.citybuildstuff.Commands.Shop.IngameShop.CommandBuy;
import at.blank.citybuildstuff.Commands.Shop.IngameShop.Special.Specialinv;
import at.blank.citybuildstuff.Commands.Shop.IngameShop.VillagerHandler;
import at.blank.citybuildstuff.Commands.Shop.IngameShop.spawnegg.SpawnEgginv;
import at.blank.citybuildstuff.Commands.SkillMenu.Listener.*;
import at.blank.citybuildstuff.Commands.SkillMenu.SkillGui;
import at.blank.citybuildstuff.Commands.SkillMenu.Skill_Command;
import at.blank.citybuildstuff.Commands.Spawn.CommandSetSpawn;
import at.blank.citybuildstuff.Commands.Spawn.CommandSpawn;
import at.blank.citybuildstuff.Commands.Spawn.RespawnListener;
import at.blank.citybuildstuff.Commands.Teleport.CommandTeleport;
import at.blank.citybuildstuff.Commands.Teleport.CommandTpa;
import at.blank.citybuildstuff.Commands.Teleport.CommandTphere;
import at.blank.citybuildstuff.Commands.Teleport.RandomTeleport.CommandPlayerRtp;
import at.blank.citybuildstuff.Commands.Teleport.RandomTeleport.CommandRtp;
import at.blank.citybuildstuff.Commands.Warp.CommandDelWarp;
import at.blank.citybuildstuff.Commands.Warp.CommandSetWarp;
import at.blank.citybuildstuff.Commands.Warp.CommandWarp;
import at.blank.citybuildstuff.Listener.*;
import at.blank.citybuildstuff.Utils.Configs.MySqlConfig;
import at.blank.citybuildstuff.Utils.MySql.MySql;
import at.blank.citybuildstuff.Utils.Statsboard;
import at.blank.citybuildstuff.Utils.Vault.Vault;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CityBuildStuff extends JavaPlugin {

    public static CityBuildStuff instance;
    public static FileConfiguration cfg;
    public static MessageManager messageManager;
    private static MySql mySQL;
    private static List<Entity> entityList = new ArrayList<>();
    public String prefix = "";
    public RewardManager rewardManager;
    public boolean broadcast;
    private BackPackManager backPackManager;
    private NamespacedKey shopItemId;
    private YamlConfiguration shopConfig;
    private YamlConfiguration sellShopConfig;

    public static MySql getMySQL() {
        return mySQL;
    }

    public static CityBuildStuff getInstance() {
        return instance;
    }

    public static MessageManager getMessageManager() {
        return messageManager;
    }

    public static List<Entity> getEntityList() {
        return entityList;
    }

    public NamespacedKey getShopItemId() {
        return shopItemId;
    }

    public YamlConfiguration getShopConfig() {
        return shopConfig;
    }

    public YamlConfiguration getSellShopConfig() {
        return sellShopConfig;
    }

    @Override
    public void onEnable() {
        instance = this;

        try {
            backPackManager = new BackPackManager();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MySqlConfig file = new MySqlConfig();
        file.setStandard();
        file.readData();
        FileManager.setStandardMySQL();
        FileManager.readMySQL();

        messageManager = new MessageManager();

        RewardFileManager.setDefault();
        RewardFileManager.loadConfig();
        this.rewardManager = new RewardManager(this);

        shopItemId = new NamespacedKey(this, "shop-item-id");

        loadConfig();
        CityBuildStuff.cfg = this.getConfig();
        mySQL = new MySql();
        mySQL.connect();


        this.rewardManager = new RewardManager(this);


        Statsboard.startScheduler();

        PluginManager pm = Bukkit.getPluginManager();

        new Vault();
        pm.registerEvents(new General(), this);
        pm.registerEvents(new Plot(), this);
        pm.registerEvents(new Rang(), this);
        pm.registerEvents(new Support(), this);
        pm.registerEvents(new ChatListener(), this);
        pm.registerEvents(new ElevatorListener(), this);
        pm.registerEvents(new SignListener(), this);
        pm.registerEvents(new JoinQuitListener(), this);
        pm.registerEvents(new CommandListener(), this);
        pm.registerEvents(new CommandDailyRewards(), this);
        pm.registerEvents(new CommandHelp(), this);
        pm.registerEvents(new RangVillager(), this);
        pm.registerEvents(new BlackMarketEntity(), this);

        pm.registerEvents(new SkillGui(), this);
        pm.registerEvents(new Skill_Command(), this);
        pm.registerEvents(new RepairSkillListener(), this);
        pm.registerEvents(new XPSkillListener(), this);
        pm.registerEvents(new FastSellListener(), this);
        pm.registerEvents(new ThunderSkillListener(), this);
        pm.registerEvents(new GrowthSkillListener(), this);

        pm.registerEvents(new CommandSell(), this);
        pm.registerEvents(new CommandBuy(), this);

        pm.registerEvents(new Erzstuffinv(), this);
        pm.registerEvents(new Breakstuffinv(), this);
        pm.registerEvents(new Farmerstuffinv(), this);
        pm.registerEvents(new Fishstuffinv(), this);
        pm.registerEvents(new LightstuffInv(), this);
        pm.registerEvents(new Logstuffinv(), this);
        pm.registerEvents(new Moblootinv(), this);
        pm.registerEvents(new Rarestuffinv(), this);
        pm.registerEvents(new ShovelstuffInv(), this);

        pm.registerEvents(new SpawnEgginv(), this);
        pm.registerEvents(new Specialinv(), this);

        pm.registerEvents(new VillagerHandler(), this);
        pm.registerEvents(new VillagerSellerHandler(), this);
        pm.registerEvents(new RespawnListener(), this);

        getCommand("setshop").setExecutor(new VillagerHandler());
        getCommand("setsellshop").setExecutor(new VillagerSellerHandler());
        getCommand("setrangshop").setExecutor(new RangVillager());
        getCommand("skills").setExecutor(new Skill_Command());
        getCommand("shop").setExecutor(new CommandBuy());
        getCommand("sell").setExecutor(new CommandSell());
        getCommand("rename").setExecutor(new CommandRename());
        getCommand("addlore").setExecutor(new CommandFeed.CommandAddLore());
        getCommand("removelore").setExecutor(new CommandRemoveLore());
        getCommand("rtp").setExecutor(new CommandRtp());
        getCommand("prtp").setExecutor(new CommandPlayerRtp());
        getCommand("help").setExecutor(new CommandHelp());
        getCommand("msg").setExecutor(new CommandMsg());
        getCommand("reply").setExecutor(new CommandReply());
        getCommand("dailyrewards").setExecutor(new CommandDailyRewards());
        getCommand("cc").setExecutor(new CommandChatClear());
        getCommand("dev").setExecutor(new CommandDev());
        getCommand("ec").setExecutor(new CommandEnderChest());
        getCommand("feed").setExecutor(new CommandFeed());
        getCommand("fly").setExecutor(new CommandFly());
        getCommand("gm").setExecutor(new CommandGameMode());
        getCommand("globalmute").setExecutor(new CommandGlobalMute());
        getCommand("hat").setExecutor(new CommandHat());
        getCommand("head").setExecutor(new CommandHead());
        getCommand("heal").setExecutor(new CommandHeal());
        getCommand("clear").setExecutor(new CommandInvClear());
        getCommand("invsee").setExecutor(new CommandInvsee());
        getCommand("sign").setExecutor(new CommandSign());
        getCommand("cspy").setExecutor(new CommandSpy());
        getCommand("unsign").setExecutor(new CommandUnsign());
        getCommand("vanish").setExecutor(new CommandVanish());
        getCommand("coins").setExecutor(new CommandCoins());
        getCommand("pay").setExecutor(new CommandPay());
        getCommand("top").setExecutor(new CommandCoinsTop());
        getCommand("setwarp").setExecutor(new CommandSetWarp());
        getCommand("delwarp").setExecutor(new CommandDelWarp());
        getCommand("warp").setExecutor(new CommandWarp());
        getCommand("tp").setExecutor(new CommandTeleport());
        getCommand("tpa").setExecutor(new CommandTpa());
        getCommand("tphere").setExecutor(new CommandTphere());
        getCommand("setspawn").setExecutor(new CommandSetSpawn());
        getCommand("spawn").setExecutor(new CommandSpawn());
        getCommand("sethome").setExecutor(new CommandSetHome());
        getCommand("delhome").setExecutor(new CommandDelHome());
        getCommand("home").setExecutor(new CommandHome());
        getCommand("clan").setExecutor(new Clan_command());
        getCommand("mute").setExecutor(new CommandMute());
        getCommand("unmute").setExecutor(new CommandUnMute());
        getCommand("tempmute").setExecutor(new CommandTempMute());
        getCommand("mutehistory").setExecutor(new CommandCheckMute());
        getCommand("ban").setExecutor(new CommandBan());
        getCommand("tempban").setExecutor(new CommandTempBan());
        getCommand("unban").setExecutor(new CommandUnban());
        getCommand("banhistory").setExecutor(new CommandCheck());
        getCommand("glow").setExecutor(new CommandGlow());
        getCommand("unglow").setExecutor(new CommandUnGlow());
        getCommand("rangupgrade").setExecutor(new RangUpgrades());
        getCommand("premium").setExecutor(new CommandPremium());
        getCommand("rubies").setExecutor(new CommandRubies());
        getCommand("rubiespay").setExecutor(new CommandRubiesPay());
        getCommand("rubiestop").setExecutor(new CommandRubiesTop());
        getCommand("setrangshop").setExecutor(new RangVillager());
        getCommand("premium").setTabCompleter(new CommandPremium());
        getCommand("msg").setTabCompleter(new CommandMsg());
        getCommand("reply").setTabCompleter(new CommandReply());
        getCommand("ban").setTabCompleter(new CommandBan());
        getCommand("tempban").setTabCompleter(new CommandTempBan());
        getCommand("unban").setTabCompleter(new CommandUnban());
        getCommand("banhistory").setTabCompleter(new CommandCheck());
        getCommand("mute").setTabCompleter(new CommandMute());
        getCommand("unmute").setTabCompleter(new CommandUnMute());
        getCommand("tempmute").setTabCompleter(new CommandTempMute());
        getCommand("mutehistory").setTabCompleter(new CommandCheckMute());
        getCommand("delhome").setTabCompleter(new CommandDelHome());
        getCommand("home").setTabCompleter(new CommandHome());
        getCommand("ec").setTabCompleter(new CommandEnderChest());
        getCommand("feed").setTabCompleter(new CommandFeed());
        getCommand("gm").setTabCompleter(new CommandGameMode());
        getCommand("head").setTabCompleter(new CommandHead());
        getCommand("heal").setTabCompleter(new CommandHeal());
        getCommand("clear").setTabCompleter(new CommandInvClear());
        getCommand("invsee").setTabCompleter(new CommandInvsee());
        getCommand("vanish").setTabCompleter(new CommandVanish());
        getCommand("coins").setTabCompleter(new CommandCoins());
        getCommand("pay").setTabCompleter(new CommandPay());
        getCommand("delwarp").setTabCompleter(new CommandDelWarp());
        getCommand("warp").setTabCompleter(new CommandWarp());
        getCommand("tp").setTabCompleter(new CommandTeleport());
        getCommand("tpa").setTabCompleter(new CommandTpa());
        getCommand("tphere").setTabCompleter(new CommandTphere());


    }

    @Override
    public void onDisable() {
        mySQL.disconnect();
        instance = this;
        for (Entity entity : entityList) {
            entity.remove();
        }

    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
