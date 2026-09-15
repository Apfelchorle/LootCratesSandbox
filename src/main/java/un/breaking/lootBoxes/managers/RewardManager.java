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
import java.util.Random;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import un.breaking.lootBoxes.LootCrates;

public class RewardManager {
    private final LootCrates plugin;
    private final File rewardsFile;
    private FileConfiguration rewardsConfig;
    private final Map<String, List<Reward>> rewards = new HashMap();
    private final Random random = new Random();

    public RewardManager(LootCrates plugin) {
        this.plugin = plugin;
        this.rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        this.loadRewards();
    }

    public void addReward(String crateId, ItemStack item, int weight) {
        List<Reward> crateRewards = (List)this.rewards.computeIfAbsent(crateId.toLowerCase(), (k) -> new ArrayList());
        crateRewards.add(new Reward(item.clone(), weight));
        this.saveRewards();
    }

    public void removeReward(String crateId, int index) {
        List<Reward> crateRewards = (List)this.rewards.get(crateId.toLowerCase());
        if (crateRewards != null && index >= 0 && index < crateRewards.size()) {
            crateRewards.remove(index);
            this.saveRewards();
        }

    }

    public void updateRewardWeight(String crateId, int index, int newWeight) {
        List<Reward> crateRewards = (List)this.rewards.get(crateId.toLowerCase());
        if (crateRewards != null && index >= 0 && index < crateRewards.size()) {
            ((Reward)crateRewards.get(index)).setWeight(newWeight);
            this.saveRewards();
        }

    }

    public List<Reward> getRewards(String crateId) {
        return (List)this.rewards.getOrDefault(crateId.toLowerCase(), new ArrayList());
    }

    public Reward rollReward(String crateId) {
        List<Reward> crateRewards = this.getRewards(crateId);
        if (crateRewards.isEmpty()) {
            return null;
        } else {
            int totalWeight = crateRewards.stream().mapToInt(Reward::getWeight).sum();
            if (totalWeight <= 0) {
                return (Reward)crateRewards.get(0);
            } else {
                int roll = this.random.nextInt(totalWeight);
                int currentWeight = 0;

                for(Reward reward : crateRewards) {
                    currentWeight += reward.getWeight();
                    if (roll < currentWeight) {
                        return reward;
                    }
                }

                return (Reward)crateRewards.get(crateRewards.size() - 1);
            }
        }
    }

    public void clearRewards(String crateId) {
        this.rewards.remove(crateId.toLowerCase());
        this.saveRewards();
    }

    private void loadRewards() {
        if (!this.rewardsFile.exists()) {
            try {
                this.rewardsFile.getParentFile().mkdirs();
                this.rewardsFile.createNewFile();
            } catch (IOException var9) {
                this.plugin.getLogger().severe("Could not create rewards.yml");
            }

        } else {
            this.rewardsConfig = YamlConfiguration.loadConfiguration(this.rewardsFile);
            if (this.rewardsConfig.contains("rewards")) {
                for(String crateId : this.rewardsConfig.getConfigurationSection("rewards").getKeys(false)) {
                    List<Reward> crateRewards = new ArrayList();

                    for(String key : this.rewardsConfig.getConfigurationSection("rewards." + crateId).getKeys(false)) {
                        try {
                            String path = "rewards." + crateId + "." + key;
                            ItemStack item = this.rewardsConfig.getItemStack(path + ".item");
                            int weight = this.rewardsConfig.getInt(path + ".weight", 10);
                            if (item != null) {
                                crateRewards.add(new Reward(item, weight));
                            }
                        } catch (Exception var10) {
                            this.plugin.getLogger().warning("Failed to load reward: " + crateId + "." + key);
                        }
                    }

                    if (!crateRewards.isEmpty()) {
                        this.rewards.put(crateId.toLowerCase(), crateRewards);
                    }
                }

                this.plugin.getLogger().info("Loaded rewards for " + this.rewards.size() + " crates");
            }
        }
    }

    public void saveRewards() {
        this.rewardsConfig = new YamlConfiguration();

        for(Map.Entry<String, List<Reward>> entry : this.rewards.entrySet()) {
            String crateId = (String)entry.getKey();
            List<Reward> crateRewards = (List)entry.getValue();

            for(int i = 0; i < crateRewards.size(); ++i) {
                Reward reward = (Reward)crateRewards.get(i);
                String path = "rewards." + crateId + "." + i;
                this.rewardsConfig.set(path + ".item", reward.getItem());
                this.rewardsConfig.set(path + ".weight", reward.getWeight());
            }
        }

        try {
            this.rewardsConfig.save(this.rewardsFile);
        } catch (IOException var8) {
            this.plugin.getLogger().severe("Could not save rewards.yml");
        }

    }

    public void reloadRewards() {
        this.rewards.clear();
        this.loadRewards();
    }

    public static class Reward {
        private final ItemStack item;
        private int weight;

        public Reward(ItemStack item, int weight) {
            this.item = item;
            this.weight = weight;
        }

        public ItemStack getItem() {
            return this.item.clone();
        }

        public int getWeight() {
            return this.weight;
        }

        public void setWeight(int weight) {
            this.weight = Math.max(1, weight);
        }

        public String getDisplayName() {
            return this.item.getItemMeta() != null && this.item.getItemMeta().hasDisplayName() ? this.item.getItemMeta().getDisplayName() : this.item.getType().name().replace("_", " ");
        }
    }
}
