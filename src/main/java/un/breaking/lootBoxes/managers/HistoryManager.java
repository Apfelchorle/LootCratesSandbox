//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import un.breaking.lootBoxes.LootCrates;
import un.breaking.lootBoxes.models.HistoryEntry;

public class HistoryManager {
    private final LootCrates plugin;
    private final File historyFile;
    private FileConfiguration historyConfig;
    private final Map<UUID, List<HistoryEntry>> playerHistory;
    private static final int MAX_HISTORY_PER_PLAYER = 50;

    public HistoryManager(LootCrates plugin) {
        this.plugin = plugin;
        this.historyFile = new File(plugin.getDataFolder(), "history.yml");
        this.playerHistory = new HashMap();
        this.loadHistory();
    }

    private void loadHistory() {
        if (!this.historyFile.exists()) {
            try {
                this.historyFile.getParentFile().mkdirs();
                this.historyFile.createNewFile();
            } catch (IOException e) {
                this.plugin.getLogger().severe("Could not create history.yml: " + e.getMessage());
            }
        }

        this.historyConfig = YamlConfiguration.loadConfiguration(this.historyFile);
        if (this.historyConfig.contains("history")) {
            for(String uuidStr : this.historyConfig.getConfigurationSection("history").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    List<String> entries = this.historyConfig.getStringList("history." + uuidStr);
                    List<HistoryEntry> historyEntries = new ArrayList();

                    for(String entry : entries) {
                        HistoryEntry historyEntry = HistoryEntry.deserialize(entry);
                        if (historyEntry != null) {
                            historyEntries.add(historyEntry);
                        }
                    }

                    this.playerHistory.put(uuid, historyEntries);
                } catch (IllegalArgumentException var10) {
                    this.plugin.getLogger().warning("Invalid UUID in history: " + uuidStr);
                }
            }
        }

    }

    public void saveAll() {
        for(Map.Entry<UUID, List<HistoryEntry>> entry : this.playerHistory.entrySet()) {
            List<String> serialized = new ArrayList();

            for(HistoryEntry historyEntry : entry.getValue()) {
                serialized.add(historyEntry.serialize());
            }

            this.historyConfig.set("history." + ((UUID)entry.getKey()).toString(), serialized);
        }

        try {
            this.historyConfig.save(this.historyFile);
        } catch (IOException e) {
            this.plugin.getLogger().severe("Could not save history.yml: " + e.getMessage());
        }

    }

    public void addEntry(UUID uuid, String crateId, String rewardName, int amount) {
        List<HistoryEntry> entries = (List)this.playerHistory.computeIfAbsent(uuid, (k) -> new ArrayList());
        entries.add(0, new HistoryEntry(crateId, rewardName, amount));

        while(entries.size() > 50) {
            entries.remove(entries.size() - 1);
        }

    }

    public List<HistoryEntry> getHistory(UUID uuid) {
        return (List)this.playerHistory.getOrDefault(uuid, new ArrayList());
    }

    public List<HistoryEntry> getHistory(UUID uuid, int limit) {
        List<HistoryEntry> entries = this.getHistory(uuid);
        return entries.size() <= limit ? entries : entries.subList(0, limit);
    }
}
