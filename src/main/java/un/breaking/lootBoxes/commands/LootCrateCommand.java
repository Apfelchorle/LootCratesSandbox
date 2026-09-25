//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.commands;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import un.breaking.lootBoxes.LootCrates;
import un.breaking.lootBoxes.models.CustomCrate;
import un.breaking.lootBoxes.models.HistoryEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LootCrateCommand implements CommandExecutor, TabCompleter {
    private final LootCrates plugin;

    public LootCrateCommand(LootCrates plugin) {
        this.plugin = plugin;
        plugin.getCommand("lootcrate").setTabCompleter(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                this.sendHelp(player);
                return true;
            } else {
                switch (args[0].toLowerCase()) {
                    case "help" -> this.sendHelp(player);
                    case "daily" -> this.handleDaily(player);
                    case "history" -> this.handleHistory(player, args);
                    case "list" -> this.handleList(player);
                    default -> this.sendHelp(player);
                }

                return true;
            }
        } else {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(LootCrates.colorize("&6&l=== LootCrates Help ==="));
        player.sendMessage(LootCrates.colorize("&e/lootcrate daily &7- Claim your daily reward"));
        player.sendMessage(LootCrates.colorize("&e/lootcrate history [page] &7- View your loot history"));
        player.sendMessage(LootCrates.colorize("&e/lootcrate list &7- View available crate types"));
        player.sendMessage(LootCrates.colorize("&7"));
        player.sendMessage(LootCrates.colorize("&7Right-click a placed crate with the matching key to open!"));
    }

    private void handleDaily(Player player) {
        if (!player.hasPermission("lootcrates.daily")) {
            String var13 = this.plugin.getPrefix();
            player.sendMessage(var13 + this.plugin.getMessage("no-permission"));
        } else if (!this.plugin.getConfig().getBoolean("daily-reward.enabled", true)) {
            String var12 = this.plugin.getPrefix();
            player.sendMessage(var12 + LootCrates.colorize("&cDaily rewards are disabled!"));
        } else if (!this.plugin.getDataManager().canClaimDaily(player.getUniqueId())) {
            String time = this.plugin.getDataManager().getRemainingCooldownFormatted(player.getUniqueId());
            String var11 = this.plugin.getPrefix();
            player.sendMessage(var11 + this.plugin.getMessage("daily-cooldown").replace("%time%", time));
        } else {
            String crateId = this.plugin.getConfig().getString("daily-reward.reward-crate", "common");
            CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
            if (crate == null && !this.plugin.getCustomCrateManager().getAllCrates().isEmpty()) {
                crate = this.plugin.getCustomCrateManager().getAllCrates().iterator().next();
            }

            if (crate == null) {
                String var10 = this.plugin.getPrefix();
                player.sendMessage(var10 + LootCrates.colorize("&cNo crates configured!"));
            } else {
                int amount = this.plugin.getConfig().getInt("daily-reward.amount-key", 1);
                String amount_string = String.valueOf(amount);
                ItemStack keyItem = crate.createKeyItem(amount);
                if (player.getInventory().firstEmpty() == -1) {
                    String var9 = this.plugin.getPrefix();
                    player.sendMessage(var9 + LootCrates.colorize("&cYour inventory is full!"));
                } else {
//                    player.getInventory().addItem(new ItemStack[]{crateItem});
                    player.getInventory().addItem(keyItem);
                    this.plugin.getDataManager().setDailyCooldown(player.getUniqueId(), System.currentTimeMillis());
                    String var10001 = this.plugin.getPrefix();
                    player.sendMessage(var10001 + this.plugin.getMessage("daily-claimed"));
                    var10001 = this.plugin.getPrefix();
//                    player.sendMessage(var10001 + this.plugin.getMessage("crate-received").replace("%crate%", crate.getDisplayName()));
                    String rawmsg = this.plugin.getMessage("key-received").replace("%amount%", amount_string).replace("%key%", crate.getKeyName());
                    Component coloredmsg = LegacyComponentSerializer.legacyAmpersand().deserialize(rawmsg);
                    player.sendMessage(var10001 + coloredmsg);
                }
            }
        }
    }

    private void handleHistory(Player player, String[] args) {
        if (!player.hasPermission("lootcrates.history")) {
            String var17 = this.plugin.getPrefix();
            player.sendMessage(var17 + this.plugin.getMessage("no-permission"));
        } else {
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException var14) {
                    page = 1;
                }
            }

            List<HistoryEntry> history = this.plugin.getHistoryManager().getHistory(player.getUniqueId());
            if (history.isEmpty()) {
                String var16 = this.plugin.getPrefix();
                player.sendMessage(var16 + this.plugin.getMessage("history-empty"));
            } else {
                int entriesPerPage = 10;
                int totalPages = (int)Math.ceil((double)history.size() / (double)entriesPerPage);
                page = Math.max(1, Math.min(page, totalPages));
                int startIndex = (page - 1) * entriesPerPage;
                int endIndex = Math.min(startIndex + entriesPerPage, history.size());
                String var10001 = this.plugin.getPrefix();
                player.sendMessage(var10001 + this.plugin.getMessage("history-header") + LootCrates.colorize(" &7(Page " + page + "/" + totalPages + ")"));

                for(int i = startIndex; i < endIndex; ++i) {
                    HistoryEntry entry = history.get(i);
                    CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(entry.getCrateId());
                    String crateName = crate != null ? crate.getDisplayName() : entry.getCrateId();
                    String var10000 = this.plugin.getMessage("history-entry").replace("%date%", entry.getFormattedDate()).replace("%tier%", crateName);
                    String var10002 = entry.getRewardName();
                    String message = var10000.replace("%reward%", var10002 + " x" + entry.getAmount());
                    player.sendMessage(LootCrates.colorize(message));
                }

            }
        }
    }

    private void handleList(Player player) {
        player.sendMessage(LootCrates.colorize("&6&l=== Available Crate Types ==="));

        for(CustomCrate crate : this.plugin.getCustomCrateManager().getAllCrates()) {
            int rewardCount = this.plugin.getRewardManager().getRewards(crate.getId()).size();
            String var10001 = crate.getDisplayName();
            player.sendMessage(LootCrates.colorize("&7- " + var10001 + " &8(" + rewardCount + " rewards)"));
        }

        if (this.plugin.getCustomCrateManager().getAllCrates().isEmpty()) {
            player.sendMessage(LootCrates.colorize("&7No crates available yet!"));
        }

    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return (List<String>)(args.length == 1 ? this.filterCompletions(Arrays.asList("help", "daily", "history", "list"), args[0]) : new ArrayList());
    }

    private List<String> filterCompletions(List<String> completions, String input) {
        return completions.stream().filter((s) -> s.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());
    }
}
