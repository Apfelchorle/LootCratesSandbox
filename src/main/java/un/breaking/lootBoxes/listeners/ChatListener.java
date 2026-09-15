//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.listeners;

import un.breaking.lootBoxes.LootCrates;
import un.breaking.lootBoxes.models.CustomCrate;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final LootCrates plugin;

    public ChatListener(LootCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();
        if (this.plugin.getCrateEditGUI().hasPendingCreation(uuid)) {
            event.setCancelled(true);
            if (message.equalsIgnoreCase("cancel")) {
                this.plugin.getCrateEditGUI().removePendingCreation(uuid);
                String var14 = this.plugin.getPrefix();
                player.sendMessage(var14 + LootCrates.colorize("&cCrate creation cancelled."));
            } else {
                String color = this.plugin.getCrateEditGUI().getPendingColor(uuid);
                this.plugin.getCrateEditGUI().removePendingCreation(uuid);
                String crateId = this.generateCrateId(message);
                if (this.plugin.getCustomCrateManager().getCrate(crateId) != null) {
                    int i;
                    for(i = 2; this.plugin.getCustomCrateManager().getCrate(crateId + i) != null; ++i) {
                    }

                    crateId = crateId + i;
                }
                final String finalCrateId = crateId;

                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    this.plugin.getCustomCrateManager().createCrate(finalCrateId, message, color);
                    String var10001 = this.plugin.getPrefix();
                    player.sendMessage(var10001 + LootCrates.colorize("&aCreated crate: " + message));
                    this.plugin.getCrateEditGUI().openEditGUI(player, finalCrateId);
                });
            }
        } else if (this.plugin.getCrateEditGUI().hasRenamingSession(uuid)) {
            event.setCancelled(true);
            if (message.equalsIgnoreCase("cancel")) {
                String crateId = this.plugin.getCrateEditGUI().getRenamingCrate(uuid);
                this.plugin.getCrateEditGUI().removeRenamingSession(uuid);
                String var13 = this.plugin.getPrefix();
                player.sendMessage(var13 + LootCrates.colorize("&cRename cancelled."));
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getCrateEditGUI().openEditGUI(player, crateId));
            } else {
                String crateId = this.plugin.getCrateEditGUI().getRenamingCrate(uuid);
                this.plugin.getCrateEditGUI().removeRenamingSession(uuid);
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
                    if (crate != null) {
                        crate.setDisplayName(message);
                        this.plugin.getCustomCrateManager().saveCrates();
                        this.plugin.getHologramManager().refreshHolograms(crateId);
                        String var10001 = this.plugin.getPrefix();
                        player.sendMessage(var10001 + LootCrates.colorize("&aCrate renamed to: " + message));
                        this.plugin.getCrateEditGUI().openEditGUI(player, crateId);
                    }

                });
            }
        } else if (this.plugin.getCrateEditGUI().hasKeyRenamingSession(uuid)) {
            event.setCancelled(true);
            if (message.equalsIgnoreCase("cancel")) {
                String crateId = this.plugin.getCrateEditGUI().getKeyRenamingCrate(uuid);
                this.plugin.getCrateEditGUI().removeKeyRenamingSession(uuid);
                String var10001 = this.plugin.getPrefix();
                player.sendMessage(var10001 + LootCrates.colorize("&cKey rename cancelled."));
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getCrateEditGUI().openKeyCustomizeGUI(player, crateId));
            } else {
                String crateId = this.plugin.getCrateEditGUI().getKeyRenamingCrate(uuid);
                this.plugin.getCrateEditGUI().removeKeyRenamingSession(uuid);
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
                    if (crate != null) {
                        crate.setKeyName(message);
                        this.plugin.getCustomCrateManager().saveCrates();
                        String var10001 = this.plugin.getPrefix();
                        player.sendMessage(var10001 + LootCrates.colorize("&b\ud83d\udd11 Key renamed to: " + message));
                        this.plugin.getCrateEditGUI().openKeyCustomizeGUI(player, crateId);
                    }

                });
            }
        }
    }

    private String generateCrateId(String displayName) {
        String stripped = LootCrates.stripColor(displayName);
        return stripped.toLowerCase().replaceAll("[^a-z0-9]", "_").replaceAll("_+", "_").replaceAll("^_|_$", "");
    }
}
