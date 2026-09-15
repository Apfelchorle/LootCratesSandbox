//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import un.breaking.lootBoxes.LootCrates;
import un.breaking.lootBoxes.managers.RewardManager;
import un.breaking.lootBoxes.models.CustomCrate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CratePlaceListener implements Listener {
    private final LootCrates plugin;
    private final Set<UUID> openingCrate = new HashSet();

    public CratePlaceListener(LootCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity var3 = event.getPlayer();
        if (var3 instanceof Player player) {
            String title = event.getView().getTitle();
            if (title.contains("- Classic") || title.contains("- Slow Reveal") || title.contains("- Fast Spin") || title.contains("- Bounce") || title.contains("- Spiral") || title.contains("- Pulse") || title.contains("- Wave") || title.contains("- Cascade") || title.contains("- Explosion") || title.contains("- Vortex") || title.contains("- Rainbow") || title.contains("- Meteor") || title.contains("- Lightning") || title.contains("- Firework") || title.contains("- Galaxy") || title.contains("- Portal") || title.contains("- Tornado") || title.contains("- Earthquake") || title.contains("- Bubble") || title.contains("- Crystal") || title.contains("- Phoenix") || title.contains("- Dragon") || title.contains("- Mystic") || title.contains("- Neon") || title.contains("- Glitch") || title.contains("Opening...")) {
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.openingCrate.remove(player.getUniqueId()), 5L);
            }

        }
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        CustomCrate crate = this.plugin.getCustomCrateManager().getCrateFromItem(item);
        if (crate != null) {
            if (!player.hasPermission("lootcrates.admin.place")) {
                event.setCancelled(true);
                String var7 = this.plugin.getPrefix();
                player.sendMessage(var7 + LootCrates.colorize("&cYou don't have permission to place crates!"));
            } else {
                Block placedBlock = event.getBlock();
                if (this.wouldFormDoubleChest(placedBlock)) {
                    event.setCancelled(true);
                    String var10001 = this.plugin.getPrefix();
                    player.sendMessage(var10001 + LootCrates.colorize("&cCannot place crate next to another chest!"));
                } else {
                    Location loc = placedBlock.getLocation();
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        BlockData patt0$temp = placedBlock.getBlockData();
                        if (patt0$temp instanceof Chest chestData) {
                            chestData.setType(Type.SINGLE);
                            placedBlock.setBlockData(chestData);
                        }

                        this.plugin.getHologramManager().createHologram(loc, crate.getId());
                        String var10001 = this.plugin.getPrefix();
                        player.sendMessage(var10001 + LootCrates.colorize("&aCrate placed! &7(" + crate.getDisplayName() + "&7)"));
                    }, 1L);
                }
            }
        }
    }

    private boolean wouldFormDoubleChest(Block block) {
        BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

        for(BlockFace face : faces) {
            Block adjacent = block.getRelative(face);
            if (adjacent.getType() == Material.CHEST || adjacent.getType() == Material.TRAPPED_CHEST) {
                return true;
            }
        }

        return false;
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (this.plugin.getHologramManager().isCrateLocation(loc)) {
            Player player = event.getPlayer();
            if (!player.hasPermission("lootcrates.admin.break")) {
                event.setCancelled(true);
                String var6 = this.plugin.getPrefix();
                player.sendMessage(var6 + LootCrates.colorize("&cYou don't have permission to break crates!"));
            } else if (player.getGameMode() == GameMode.CREATIVE && !player.isSneaking()) {
                event.setCancelled(true);
                String crateId = this.plugin.getHologramManager().getCrateIdAt(loc);
                if (crateId != null) {
                    this.plugin.getCrateEditGUI().openEditGUI(player, crateId);
                }

            } else if (!player.isSneaking()) {
                event.setCancelled(true);
                String var5 = this.plugin.getPrefix();
                player.sendMessage(var5 + LootCrates.colorize("&eSneak + break to remove this crate."));
            } else {
                this.plugin.getHologramManager().removeHologram(loc);
                String var10001 = this.plugin.getPrefix();
                player.sendMessage(var10001 + LootCrates.colorize("&eCrate removed."));
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getHand() == EquipmentSlot.HAND) {
                Block block = event.getClickedBlock();
                if (block != null) {
                    Location loc = block.getLocation();
                    String crateId = this.plugin.getHologramManager().getCrateIdAt(loc);
                    if (crateId != null) {
                        event.setCancelled(true);
                        Player player = event.getPlayer();
                        CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
                        if (crate != null) {
                            ItemStack mainHand = player.getInventory().getItemInMainHand();
                            ItemStack offHand = player.getInventory().getItemInOffHand();
                            ItemStack keyItem = null;
                            if (crate.isKeyItem(mainHand)) {
                                keyItem = mainHand;
                            } else if (crate.isKeyItem(offHand)) {
                                keyItem = offHand;
                            }

                            if (keyItem == null) {
                                String message = this.plugin.getMessage("no-key").replace("%key%", crate.getKeyName());
                                String var16 = this.plugin.getPrefix();
                                player.sendMessage(var16 + message);
                            } else if (!player.hasPermission("lootcrates.use")) {
                                String var15 = this.plugin.getPrefix();
                                player.sendMessage(var15 + this.plugin.getMessage("no-permission"));
                            } else if (this.openingCrate.contains(player.getUniqueId())) {
                                String var14 = this.plugin.getPrefix();
                                player.sendMessage(var14 + LootCrates.colorize("&cPlease wait for your current crate to finish!"));
                            } else {
                                List<RewardManager.Reward> rewards = this.plugin.getRewardManager().getRewards(crateId);
                                if (rewards.isEmpty()) {
                                    String var13 = this.plugin.getPrefix();
                                    player.sendMessage(var13 + LootCrates.colorize("&cThis crate has no rewards configured!"));
                                } else {
                                    this.openingCrate.add(player.getUniqueId());
                                    keyItem.setAmount(keyItem.getAmount() - 1);
                                    RewardManager.Reward reward = this.plugin.getRewardManager().rollReward(crateId);
                                    if (reward == null) {
                                        this.openingCrate.remove(player.getUniqueId());
                                        String var10001 = this.plugin.getPrefix();
                                        player.sendMessage(var10001 + LootCrates.colorize("&cError rolling reward!"));
                                    } else {
                                        this.plugin.getAnimationManager().playAnimation(player, crate, crate.getAnimationType(), reward, (wonItem) -> {
                                            this.openingCrate.remove(player.getUniqueId());
                                            if (player.getInventory().firstEmpty() == -1) {
                                                player.getWorld().dropItemNaturally(player.getLocation(), wonItem);
                                                String var10001 = this.plugin.getPrefix();
                                                player.sendMessage(var10001 + LootCrates.colorize("&eInventory full! Dropped on ground."));
                                            } else {
                                                player.getInventory().addItem(wonItem);
                                            }

                                            String rewardName = reward.getDisplayName();
                                            String rewardDisplay = rewardName + " &rx" + wonItem.getAmount();
                                            String var12 = this.plugin.getPrefix();
                                            String rawmsg = this.plugin.getMessage("reward-won");
                                            String formattedRaw = rawmsg.replace("%reward%", rewardDisplay);
                                            Component Rewardmsg = LegacyComponentSerializer.legacyAmpersand().deserialize(var12 + formattedRaw);
                                            player.sendMessage(Rewardmsg);
                                            this.plugin.getHistoryManager().addEntry(player.getUniqueId(), crateId, rewardName, wonItem.getAmount());
                                            int broadcastThreshold = this.plugin.getConfig().getInt("settings.broadcast-rarity-threshold", 5);
                                            if (this.plugin.getConfig().getBoolean("settings.broadcast-legendary", true) && (reward.getWeight() <= broadcastThreshold || crateId.toLowerCase().contains("legendary") || crateId.toLowerCase().contains("mythic"))) {
                                                String broadcast = var12 + this.plugin.getMessage("legendary-broadcast").replace("%player%", player.getName()).replace("%reward%", rewardDisplay).replace("%crate%", crate.getDisplayName());

                                                Component fullmsg = LegacyComponentSerializer.legacyAmpersand().deserialize(broadcast);
                                                for(Player online : Bukkit.getOnlinePlayers()) {
                                                    online.sendMessage(var12 + broadcast);
                                                }
                                            }

                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
