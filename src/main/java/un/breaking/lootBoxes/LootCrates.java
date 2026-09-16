//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import un.breaking.lootBoxes.animation.CrateAnimationManager;
import un.breaking.lootBoxes.commands.LootCrateAdminCommand;
import un.breaking.lootBoxes.commands.LootCrateCommand;
import un.breaking.lootBoxes.gui.CrateEditGUI;
import un.breaking.lootBoxes.hologram.HologramManager;
import un.breaking.lootBoxes.listeners.ChatListener;
import un.breaking.lootBoxes.listeners.CratePlaceListener;
import un.breaking.lootBoxes.listeners.GUIListener;
import un.breaking.lootBoxes.managers.CustomCrateManager;
import un.breaking.lootBoxes.managers.DataManager;
import un.breaking.lootBoxes.managers.HistoryManager;
import un.breaking.lootBoxes.managers.RewardManager;

public class LootCrates extends JavaPlugin {
    private static LootCrates instance;
    private CustomCrateManager customCrateManager;
    private RewardManager rewardManager;
    private DataManager dataManager;
    private HistoryManager historyManager;
    private HologramManager hologramManager;
    private CrateEditGUI crateEditGUI;
    private CrateAnimationManager animationManager;

    public LootCrates() {
    }

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.dataManager = new DataManager(this);
        this.customCrateManager = new CustomCrateManager(this);
        this.rewardManager = new RewardManager(this);
        this.historyManager = new HistoryManager(this);
        this.hologramManager = new HologramManager(this);

// purge before load
        Bukkit.getScheduler().runTask(this, () -> {
            this.hologramManager.purgeOrphans();
            this.hologramManager.loadHolograms();
        });


        this.crateEditGUI = new CrateEditGUI(this);
        this.animationManager = new CrateAnimationManager(this);
        this.getCommand("lootcrate").setExecutor(new LootCrateCommand(this));
        this.getCommand("lcadmin").setExecutor(new LootCrateAdminCommand(this));
        this.getServer().getPluginManager().registerEvents(new CratePlaceListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.getLogger().info("LootCrates has been enabled!");
        this.getLogger().info("Loaded " + this.customCrateManager.getAllCrates().size() + " crate types.");
    }

    public void onDisable() {
        if (this.dataManager != null) {
            this.dataManager.saveAll();
        }

        if (this.historyManager != null) {
            this.historyManager.saveAll();
        }

        if (this.rewardManager != null) {
            this.rewardManager.saveRewards();
        }

        if (this.hologramManager != null) {
            this.hologramManager.removeAllHolograms();
        }

        this.getLogger().info("LootCrates has been disabled!");
    }

    public static LootCrates getInstance() {
        return instance;
    }

    public CustomCrateManager getCustomCrateManager() {
        return this.customCrateManager;
    }

    public RewardManager getRewardManager() {
        return this.rewardManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public HistoryManager getHistoryManager() {
        return this.historyManager;
    }

    public HologramManager getHologramManager() {
        return this.hologramManager;
    }

    public CrateEditGUI getCrateEditGUI() {
        return this.crateEditGUI;
    }

    public CrateAnimationManager getAnimationManager() {
        return this.animationManager;
    }

    public void reloadPlugin() {
        this.reloadConfig();
        this.customCrateManager.reloadCrates();
        this.rewardManager.reloadRewards();
    }

    public String getPrefix() {
        return colorize(this.getConfig().getString("settings.prefix", "&6&lLootCrates &8» &r"));
    }

    public String getMessage(String path) {
        return colorize(this.getConfig().getString("messages." + path, "&cMessage not found: " + path));
    }

    public static String colorize(String text) {
        return text == null ? "" : text.replace("&", "§");
    }

    public static String stripColor(String text) {
        return text == null ? "" : text.replaceAll("&[0-9a-fk-or]", "").replaceAll("§[0-9a-fk-or]", "");
    }
}
