//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.listeners;

import un.breaking.lootBoxes.LootCrates;
import un.breaking.lootBoxes.animation.AnimationType;
import un.breaking.lootBoxes.managers.RewardManager;
import un.breaking.lootBoxes.models.CustomCrate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIListener implements Listener {
    private final LootCrates plugin;
    private final Map<UUID, WeightEditSession> weightEditSessions = new HashMap();
    private final Map<UUID, ItemStack> pendingNewItems = new HashMap();

    public GUIListener(LootCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity var3 = event.getWhoClicked();
        if (var3 instanceof Player player) {
            String title = event.getView().getTitle();
            if (!title.contains("Opening...") && !title.contains("- Classic") && !title.contains("- Slow Reveal") && !title.contains("- Fast Spin") && !title.contains("- Bounce") && !title.contains("- Spiral") && !title.contains("- Pulse") && !title.contains("- Wave") && !title.contains("- Cascade") && !title.contains("- Explosion") && !title.contains("- Vortex") && !title.contains("- Rainbow") && !title.contains("- Meteor") && !title.contains("- Lightning") && !title.contains("- Firework") && !title.contains("- Galaxy") && !title.contains("- Portal") && !title.contains("- Tornado") && !title.contains("- Earthquake") && !title.contains("- Bubble") && !title.contains("- Crystal") && !title.contains("- Phoenix") && !title.contains("- Dragon") && !title.contains("- Mystic") && !title.contains("- Neon") && !title.contains("- Glitch")) {
                if (title.contains("Select Crate to Edit")) {
                    event.setCancelled(true);
                    this.handleCrateSelect(player, event);
                } else if (title.startsWith(LootCrates.colorize("&6Edit:"))) {
                    event.setCancelled(true);
                    this.handleCrateEdit(player, event);
                } else if (title.contains("Set Weight")) {
                    event.setCancelled(true);
                    this.handleWeightEdit(player, event);
                } else if (title.contains("Select Animation Style")) {
                    event.setCancelled(true);
                    this.handleAnimationSelect(player, event);
                } else if (title.contains("Customize Key")) {
                    event.setCancelled(true);
                    this.handleKeyCustomize(player, event);
                } else if (title.contains("Create New Crate")) {
                    event.setCancelled(true);
                    this.handleCreateCrate(player, event);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private void handleCrateSelect(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null && clicked.getType() != Material.GRAY_STAINED_GLASS_PANE) {
            if (clicked.getType() == Material.EMERALD) {
                this.plugin.getCrateEditGUI().openCreateCrateGUI(player);
            } else {
                for(CustomCrate crate : this.plugin.getCustomCrateManager().getAllCrates()) {
                    if (crate.isCrateItem(clicked)) {
                        this.plugin.getCrateEditGUI().openEditGUI(player, crate.getId());
                        return;
                    }
                }

            }
        }
    }

    private void handleCrateEdit(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getRawSlot();
        String crateId = this.plugin.getCrateEditGUI().getEditingCrate(player.getUniqueId());
        if (crateId != null) {
            if (event.isShiftClick() && slot >= 54) {
                ItemStack itemToAdd = event.getCurrentItem();
                if (itemToAdd != null && itemToAdd.getType() != Material.AIR) {
                    int defaultWeight = this.plugin.getConfig().getInt("settings.default-reward-weight", 10);
                    ItemStack rewardItem = itemToAdd.clone();
                    this.plugin.getRewardManager().addReward(crateId, rewardItem, defaultWeight);
                    String var17 = this.plugin.getPrefix();
                    player.sendMessage(var17 + LootCrates.colorize("&a✓ Quick-added reward with weight " + defaultWeight + "!"));
                    this.plugin.getCrateEditGUI().openEditGUI(player, crateId);
                    return;
                }
            }

            if (clicked != null) {
                if (slot == 45) {
                    this.plugin.getCrateEditGUI().openCrateSelectGUI(player);
                    return;
                }

                if (slot == 53 && clicked.getType() == Material.BARRIER) {
                    if (event.isShiftClick()) {
                        this.plugin.getCustomCrateManager().deleteCrate(crateId);
                        this.plugin.getRewardManager().clearRewards(crateId);
                        String var15 = this.plugin.getPrefix();
                        player.sendMessage(var15 + LootCrates.colorize("&cCrate deleted!"));
                        this.plugin.getCrateEditGUI().openCrateSelectGUI(player);
                    } else {
                        String var16 = this.plugin.getPrefix();
                        player.sendMessage(var16 + LootCrates.colorize("&eShift+Click to confirm deletion!"));
                    }

                    return;
                }

                if (slot == 51 && clicked.getType() == Material.NETHER_STAR) {
                    this.plugin.getCrateEditGUI().openAnimationSelectGUI(player, crateId);
                    return;
                }

                if (slot == 46 && clicked.getType() == Material.NAME_TAG) {
                    this.plugin.getCrateEditGUI().startRename(player, crateId);
                    return;
                }

                if (slot == 48) {
                    this.plugin.getCrateEditGUI().openKeyCustomizeGUI(player, crateId);
                    return;
                }

                if (slot == 49) {
                    ItemStack heldItem = player.getItemOnCursor();
                    if (heldItem == null || heldItem.getType() == Material.AIR) {
                        heldItem = player.getInventory().getItemInMainHand();
                    }

                    if (heldItem != null && heldItem.getType() != Material.AIR) {
                        this.pendingNewItems.put(player.getUniqueId(), heldItem.clone());
                        this.weightEditSessions.put(player.getUniqueId(), new WeightEditSession(crateId, -1, 10, true));
                        this.plugin.getCrateEditGUI().openWeightEditGUI(player, heldItem.clone(), 10);
                        return;
                    }

                    String var14 = this.plugin.getPrefix();
                    player.sendMessage(var14 + LootCrates.colorize("&cHold an item first!"));
                    return;
                }

                if (slot == 47) {
                    return;
                }
            }

            if (this.isRewardSlot(slot) && clicked != null && clicked.getType() != Material.AIR && clicked.getType() != Material.GRAY_STAINED_GLASS_PANE && !this.isGlassPane(clicked.getType())) {
                Map<Integer, Integer> indexMap = this.plugin.getCrateEditGUI().getSlotToIndexMap(player.getUniqueId());
                if (indexMap == null || !indexMap.containsKey(slot)) {
                    return;
                }

                int rewardIndex = (Integer)indexMap.get(slot);
                List<RewardManager.Reward> rewards = this.plugin.getRewardManager().getRewards(crateId);
                if (rewardIndex >= rewards.size()) {
                    return;
                }

                int currentWeight = ((RewardManager.Reward)rewards.get(rewardIndex)).getWeight();
                if (event.isRightClick()) {
                    this.plugin.getRewardManager().removeReward(crateId, rewardIndex);
                    String var10001 = this.plugin.getPrefix();
                    player.sendMessage(var10001 + LootCrates.colorize("&cReward removed!"));
                    this.plugin.getCrateEditGUI().openEditGUI(player, crateId);
                } else if (event.isLeftClick()) {
                    this.weightEditSessions.put(player.getUniqueId(), new WeightEditSession(crateId, rewardIndex, currentWeight, false));
                    this.plugin.getCrateEditGUI().openWeightEditGUI(player, clicked, currentWeight);
                }
            }

        }
    }

    private void handleWeightEdit(Player player, InventoryClickEvent event) {
        int slot = event.getRawSlot();
        WeightEditSession session = (WeightEditSession)this.weightEditSessions.get(player.getUniqueId());
        if (session != null) {
            Inventory inv = event.getInventory();
            ItemStack weightDisplay = inv.getItem(13);
            if (slot == 10) {
                session.weight -= 10;
            } else if (slot == 11) {
                session.weight -= 5;
            } else if (slot == 12) {
                --session.weight;
            } else if (slot == 14) {
                ++session.weight;
            } else if (slot == 15) {
                session.weight += 5;
            } else if (slot == 16) {
                session.weight += 10;
            } else if (slot == 22) {
                if (session.isNew) {
                    ItemStack newItem = (ItemStack)this.pendingNewItems.remove(player.getUniqueId());
                    if (newItem != null) {
                        this.plugin.getRewardManager().addReward(session.crateId, newItem, session.weight);
                        String var10001 = this.plugin.getPrefix();
                        player.sendMessage(var10001 + LootCrates.colorize("&aReward added with weight: " + session.weight));
                    }
                } else {
                    this.plugin.getRewardManager().updateRewardWeight(session.crateId, session.rewardIndex, session.weight);
                    String var9 = this.plugin.getPrefix();
                    player.sendMessage(var9 + LootCrates.colorize("&aWeight updated to: " + session.weight));
                }

                this.weightEditSessions.remove(player.getUniqueId());
                this.plugin.getCrateEditGUI().openEditGUI(player, session.crateId);
                return;
            }

            session.weight = Math.max(1, Math.min(100, session.weight));
            if (weightDisplay != null) {
                ItemMeta meta = weightDisplay.getItemMeta();
                meta.setDisplayName(LootCrates.colorize("&eWeight: &6" + session.weight));
                weightDisplay.setItemMeta(meta);
            }

        }
    }

    private void handleCreateCrate(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked != null) {
            Material type = clicked.getType();
            String colorName = null;
            if (type == Material.WHITE_STAINED_GLASS_PANE) {
                colorName = "WHITE";
            } else if (type == Material.RED_STAINED_GLASS_PANE) {
                colorName = "RED";
            } else if (type == Material.ORANGE_STAINED_GLASS_PANE) {
                colorName = "ORANGE";
            } else if (type == Material.YELLOW_STAINED_GLASS_PANE) {
                colorName = "YELLOW";
            } else if (type == Material.LIME_STAINED_GLASS_PANE) {
                colorName = "LIME";
            } else if (type == Material.CYAN_STAINED_GLASS_PANE) {
                colorName = "CYAN";
            } else if (type == Material.BLUE_STAINED_GLASS_PANE) {
                colorName = "BLUE";
            } else if (type == Material.PURPLE_STAINED_GLASS_PANE) {
                colorName = "PURPLE";
            } else if (type == Material.PINK_STAINED_GLASS_PANE) {
                colorName = "PINK";
            }

            if (colorName != null) {
                this.plugin.getCrateEditGUI().startCrateCreation(player, colorName);
            }

        }
    }

    private void handleAnimationSelect(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getRawSlot();
        String crateId = this.plugin.getCrateEditGUI().getEditingCrate(player.getUniqueId());
        if (crateId != null) {
            if (slot == 45) {
                this.plugin.getCrateEditGUI().openEditGUI(player, crateId);
            } else {
                if (slot >= 9 && slot < 45 && clicked != null && clicked.getType() != Material.AIR && clicked.getType() != Material.MAGENTA_STAINED_GLASS_PANE) {
                    int animIndex = slot - 9;
                    AnimationType[] animations = AnimationType.values();
                    if (animIndex >= 0 && animIndex < animations.length) {
                        AnimationType selected = animations[animIndex];
                        CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
                        if (crate != null) {
                            crate.setAnimationType(selected);
                            this.plugin.getCustomCrateManager().saveCrates();
                            String var10001 = this.plugin.getPrefix();
                            player.sendMessage(var10001 + LootCrates.colorize("&d✦ Animation set to: &e" + selected.getDisplayName()));
                            this.plugin.getCrateEditGUI().openEditGUI(player, crateId);
                        }
                    }
                }

            }
        }
    }

    private void handleKeyCustomize(Player player, InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        int slot = event.getRawSlot();
        String crateId = this.plugin.getCrateEditGUI().getEditingCrate(player.getUniqueId());
        if (crateId != null) {
            if (slot == 36) {
                this.plugin.getCrateEditGUI().openEditGUI(player, crateId);
            } else if (slot == 40 && clicked != null && clicked.getType() == Material.NAME_TAG) {
                this.plugin.getCrateEditGUI().startKeyRename(player, crateId);
            } else {
                if (slot >= 9 && slot < 36 && clicked != null && clicked.getType() != Material.AIR && clicked.getType() != Material.CYAN_STAINED_GLASS_PANE) {
                    Material selectedMaterial = clicked.getType();
                    CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
                    if (crate != null) {
                        crate.setKeyMaterial(selectedMaterial);
                        this.plugin.getCustomCrateManager().saveCrates();
                        String var10001 = this.plugin.getPrefix();
                        player.sendMessage(var10001 + LootCrates.colorize("&b\ud83d\udd11 Key material set to: &f" + selectedMaterial.name()));
                        this.plugin.getCrateEditGUI().openKeyCustomizeGUI(player, crateId);
                    }
                }

            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity var3 = event.getPlayer();
        if (var3 instanceof Player player) {
            String title = event.getView().getTitle();
            if (title.startsWith(LootCrates.colorize("&6Edit:")) && !this.weightEditSessions.containsKey(player.getUniqueId())) {
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                    String newTitle = player.getOpenInventory() != null ? player.getOpenInventory().getTitle() : "";
                    if (!newTitle.contains("Animation") && !newTitle.contains("Weight") && !newTitle.contains("Edit") && !newTitle.contains("Key") && !newTitle.contains("Crate")) {
                        this.plugin.getCrateEditGUI().removeEditingSession(player.getUniqueId());
                    }

                }, 5L);
            }

        }
    }

    private boolean isRewardSlot(int slot) {
        if (slot >= 10 && slot <= 43) {
            int col = slot % 9;
            return col >= 1 && col <= 7;
        } else {
            return false;
        }
    }

    private boolean isGlassPane(Material mat) {
        return mat.name().contains("GLASS_PANE");
    }

    private String capitalize(String str) {
        if (str != null && !str.isEmpty()) {
            String var10000 = str.substring(0, 1).toUpperCase();
            return var10000 + str.substring(1);
        } else {
            return str;
        }
    }

    private static class WeightEditSession {
        String crateId;
        int rewardIndex;
        int weight;
        boolean isNew;

        WeightEditSession(String crateId, int rewardIndex, int weight, boolean isNew) {
            this.crateId = crateId;
            this.rewardIndex = rewardIndex;
            this.weight = weight;
            this.isNew = isNew;
        }
    }
}
