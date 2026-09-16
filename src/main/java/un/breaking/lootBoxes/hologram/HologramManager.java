//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.hologram;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import un.breaking.lootBoxes.LootCrates;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HologramManager {
    private final un.breaking.lootBoxes.LootCrates plugin;
    private final Map<Location, CrateHologram> holograms = new HashMap();
    private final File dataFile;
    private FileConfiguration dataConfig;

    public HologramManager(un.breaking.lootBoxes.LootCrates plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "crate_locations.yml");
    }

    public void createHologram(Location blockLocation, String crateId) {
        this.removeHologram(blockLocation);
        un.breaking.lootBoxes.models.CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
        if (crate != null) {
            World world = blockLocation.getWorld();
            if (world != null) {
                List<ArmorStand> stands = new ArrayList();
                Location hologramLoc = blockLocation.clone().add(0.5F, 2.5F, 0.5F);
                ArmorStand line1 = this.createArmorStand(hologramLoc, un.breaking.lootBoxes.LootCrates.colorize(crate.getHologramLine1()));
                stands.add(line1);
                ArmorStand line2 = this.createArmorStand(hologramLoc.clone().subtract(0.0F, 0.3, 0.0F), LootCrates.colorize(crate.getHologramLine2()));
                stands.add(line2);
                ArmorStand line3 = this.createArmorStand(hologramLoc.clone().subtract(0.0F, 0.6, 0.0F), LootCrates.colorize(crate.getHologramLine3()));
                stands.add(line3);
                CrateHologram hologram = new CrateHologram(blockLocation, crateId, stands);
                this.holograms.put(blockLocation, hologram);
                hologram.startParticles(this.plugin, crate);
                this.saveHolograms();
            }
        }
    }

    private ArmorStand createArmorStand(Location location, String name) {
        ArmorStand stand = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.addScoreboardTag("lootcrates_holo");
        stand.setCustomName(name);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        stand.setSmall(true);
        stand.setPersistent(true);
        return stand;
    }

    public void removeHologram(Location blockLocation) {
        CrateHologram hologram = this.holograms.remove(blockLocation);
        if (hologram != null) {
            hologram.remove();
            this.saveHolograms();
        }

    }

    public CrateHologram getHologram(Location location) {
        return this.holograms.get(location);
    }

    public boolean isCrateLocation(Location location) {
        return this.holograms.containsKey(location);
    }

    public String getCrateIdAt(Location location) {
        CrateHologram hologram = this.holograms.get(location);
        return hologram != null ? hologram.getCrateId() : null;
    }

    public void refreshHolograms(String crateId) {
        List<Location> locations = new ArrayList<>();

        for(Map.Entry<Location, CrateHologram> entry : this.holograms.entrySet()) {
            if (entry.getValue().getCrateId().equals(crateId)) {
                locations.add(entry.getKey());
            }
        }

        for(Location loc : locations) {
            this.removeHologram(loc);
            this.createHologram(loc, crateId);
        }

    }

    public void loadHolograms() {
        if (!this.dataFile.exists()) {
            try {
                this.dataFile.getParentFile().mkdirs();
                this.dataFile.createNewFile();
            } catch (IOException var14) {
                this.plugin.getLogger().severe("Could not create crate_locations.yml");
            }

        } else {
            YamlConfiguration loadedConfig = YamlConfiguration.loadConfiguration(this.dataFile);
            this.dataConfig = loadedConfig;
            if (loadedConfig.contains("crates")) {
                for(String key : loadedConfig.getConfigurationSection("crates").getKeys(false)) {
                    try {
                        String path = "crates." + key;
                        String worldName = loadedConfig.getString(path + ".world");
                        double x = loadedConfig.getDouble(path + ".x");
                        double y = loadedConfig.getDouble(path + ".y");
                        double z = loadedConfig.getDouble(path + ".z");
                        String crateId = loadedConfig.getString(path + ".crate-id");
                        World world = this.plugin.getServer().getWorld(worldName);
                        if (world != null) {
                            Location loc = new Location(world, x, y, z);
                            if (crateId != null && this.plugin.getCustomCrateManager().getCrate(crateId) != null) {
                                this.createHologram(loc, crateId);
                            }
                        }
                    } catch (Exception var15) {
                        this.plugin.getLogger().warning("Failed to load crate hologram: " + key);
                        var15.printStackTrace();
                    }
                }

                this.plugin.getLogger().info("Loaded " + this.holograms.size() + " crate holograms");
            }
        }
    }

    public void saveHolograms() {
        this.dataConfig = new YamlConfiguration();
        int index = 0;

        for(Map.Entry<Location, CrateHologram> entry : this.holograms.entrySet()) {
            Location loc = entry.getKey();
            CrateHologram hologram = entry.getValue();
            String path = "crates." + index;
            this.dataConfig.set(path + ".world", loc.getWorld().getName());
            this.dataConfig.set(path + ".x", loc.getBlockX());
            this.dataConfig.set(path + ".y", loc.getBlockY());
            this.dataConfig.set(path + ".z", loc.getBlockZ());
            this.dataConfig.set(path + ".crate-id", hologram.getCrateId());
            ++index;
        }

        try {
            this.dataConfig.save(this.dataFile);
        } catch (IOException var7) {
            this.plugin.getLogger().severe("Could not save crate_locations.yml");
        }

    }

    public void purgeOrphans() {
        if (!this.dataFile.exists()) return;
        YamlConfiguration loadedConfig = YamlConfiguration.loadConfiguration(this.dataFile);
        if (!loadedConfig.contains("crates")) {
            plugin.getLogger().warning("loadedconfig doesnt contain crates");
            return;
        }

        int removed = 0;
        for (String key : loadedConfig.getConfigurationSection("crates").getKeys(false)) {
            String path = "crates." + key;
            String worldName = loadedConfig.getString(path + ".world");
            double x = loadedConfig.getDouble(path + ".x");
            double y = loadedConfig.getDouble(path + ".y");
            double z = loadedConfig.getDouble(path + ".z");

            World world = this.plugin.getServer().getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("WorldName is NULL: KEY: " + key + " WORLD NAME: " + worldName);
                continue;
            }

            Location loc = new Location(world, x, y, z);
            world.getChunkAt(loc).load();
            Location center = loc.clone().add(0.5, 2.0, 0.5);

            for (org.bukkit.entity.Entity e : world.getNearbyEntities(center, 2.0, 4.0, 2.0)) {
                if (e.getType() == org.bukkit.entity.EntityType.ARMOR_STAND) {
                    org.bukkit.entity.ArmorStand stand = (org.bukkit.entity.ArmorStand) e;

                    if (!stand.getScoreboardTags().contains("lootcrates_holo")) continue;
                    stand.remove();
                    this.plugin.getLogger().info("Purging Orphaned " + stand.getName());
                    removed++;
                }
            }
        }

        this.plugin.getLogger().info("Purged " + removed + " orphaned crate hologram armor stand(s).");
    }

    public void removeAllHolograms() {
        for(CrateHologram hologram : this.holograms.values()) {
            hologram.remove();
        }

        this.holograms.clear();
    }

    public Collection<CrateHologram> getAllHolograms() {
        return this.holograms.values();
    }
}
