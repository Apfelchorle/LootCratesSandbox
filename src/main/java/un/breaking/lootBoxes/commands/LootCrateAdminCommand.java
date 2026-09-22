//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package un.breaking.lootBoxes.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import un.breaking.lootBoxes.LootCrates;
import un.breaking.lootBoxes.models.CustomCrate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LootCrateAdminCommand implements CommandExecutor, TabCompleter {
    private final LootCrates plugin;

    public LootCrateAdminCommand(LootCrates plugin) {
        this.plugin = plugin;
        plugin.getCommand("lcadmin").setTabCompleter(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lootcrates.admin")) {
            String var10001 = this.plugin.getPrefix();
            sender.sendMessage(var10001 + this.plugin.getMessage("no-permission"));
            return true;
        } else if (args.length == 0) {
            this.sendHelp(sender);
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "help" -> this.sendHelp(sender);
                case "give" -> this.handleGive(sender, args);
                case "givekey" -> this.handleGiveKey(sender, args);
                case "edit" -> this.handleEdit(sender);
                case "list" -> this.handleList(sender);
                case "reload" -> this.handleReload(sender);
                case "refresh" -> this.handleRefresh(sender);
                case "purge" -> this.handlePurge(sender);
                default -> this.sendHelp(sender);
            }

            return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(LootCrates.colorize("&6&l=== LootCrates Admin Help ==="));
        sender.sendMessage(LootCrates.colorize("&e/lcadmin give <player> <crateId> [amount] &7- Give a crate"));
        sender.sendMessage(LootCrates.colorize("&e/lcadmin givekey <player> <crateId> [amount] &7- Give a key"));
        sender.sendMessage(LootCrates.colorize("&e/lcadmin edit &7- Open crate editor GUI"));
        sender.sendMessage(LootCrates.colorize("&e/lcadmin list &7- List all crate types"));
        sender.sendMessage(LootCrates.colorize("&e/lcadmin reload &7- Reload config"));
        sender.sendMessage(LootCrates.colorize("&e/lcadmin refresh &7- refresh holograms"));
        sender.sendMessage(LootCrates.colorize("&e/lcadmin purge &7- purge and recreate all holograms"));
        sender.sendMessage(LootCrates.colorize("&7"));
        sender.sendMessage(LootCrates.colorize("&ePlacing Crates:"));
        sender.sendMessage(LootCrates.colorize("&7- Get a crate with /lcadmin give"));
        sender.sendMessage(LootCrates.colorize("&7- Place it as a block to create a crate station"));
        sender.sendMessage(LootCrates.colorize("&7- Holograms and particles will appear!"));
    }

    private void handleRefresh(CommandSender sender) {
        if (!sender.hasPermission("lootcrates.admin")) {
            String prefix = this.plugin.getPrefix();
            sender.sendMessage(prefix, this.plugin.getMessage("no-permission"));
        } else {
            this.plugin.getHologramManager().removeAllHolograms();
        }
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lootcrates.admin.give")) {
            String var16 = this.plugin.getPrefix();
            sender.sendMessage(var16 + this.plugin.getMessage("no-permission"));
        } else if (args.length < 3) {
            String var15 = this.plugin.getPrefix();
            sender.sendMessage(var15 + LootCrates.colorize("&cUsage: /lcadmin give <player> <crateId> [amount]"));
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                String var14 = this.plugin.getPrefix();
                sender.sendMessage(var14 + this.plugin.getMessage("player-not-found"));
            } else {
                String crateId = args[2].toLowerCase();
                CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
                if (crate == null) {
                    String var12 = this.plugin.getPrefix();
                    sender.sendMessage(var12 + LootCrates.colorize("&cCrate not found: " + crateId));
                    var12 = this.plugin.getPrefix();
                    sender.sendMessage(var12 + LootCrates.colorize("&7Use /lcadmin list to see available crates"));
                } else {
                    int amount = 1;
                    if (args.length > 3) {
                        try {
                            amount = Integer.parseInt(args[3]);
                            amount = Math.clamp(amount, 1, 64);
                        } catch (NumberFormatException var8) {
                            amount = 1;
                        }
                    }

                    ItemStack crateItem = crate.createCrateItem(amount);
                    if (target.getInventory().firstEmpty() == -1) {
                        target.getWorld().dropItemNaturally(target.getLocation(), crateItem);
                        String var10001 = this.plugin.getPrefix();
                        sender.sendMessage(var10001 + LootCrates.colorize("&ePlayer's inventory was full, dropped on ground."));
                    } else {
                        target.getInventory().addItem(crateItem);
                    }

                    String var10 = this.plugin.getPrefix();
                    target.sendMessage(var10 + this.plugin.getMessage("crate-received").replace("%crate%", crate.getDisplayName()));
                    var10 = this.plugin.getPrefix();
                    sender.sendMessage(var10 + LootCrates.colorize("&aGave " + amount + "x " + crate.getDisplayName() + " &ato " + target.getName()));
                }
            }
        }
    }

    private void handleGiveKey(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lootcrates.admin.givekey")) {
            String var15 = this.plugin.getPrefix();
            sender.sendMessage(var15 + this.plugin.getMessage("no-permission"));
        } else if (args.length < 3) {
            String var14 = this.plugin.getPrefix();
            sender.sendMessage(var14 + LootCrates.colorize("&cUsage: /lcadmin givekey <player> <crateId> [amount]"));
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                String var13 = this.plugin.getPrefix();
                sender.sendMessage(var13 + this.plugin.getMessage("player-not-found"));
            } else {
                String crateId = args[2].toLowerCase();
                CustomCrate crate = this.plugin.getCustomCrateManager().getCrate(crateId);
                if (crate == null) {
                    String var11 = this.plugin.getPrefix();
                    sender.sendMessage(var11 + LootCrates.colorize("&cCrate not found: " + crateId));
                    var11 = this.plugin.getPrefix();
                    sender.sendMessage(var11 + LootCrates.colorize("&7Use /lcadmin list to see available crates"));
                } else {
                    int amount = 1;
                    if (args.length > 3) {
                        try {
                            amount = Integer.parseInt(args[3]);
                            amount = Math.max(1, Math.min(amount, 64));
                        } catch (NumberFormatException var8) {
                            amount = 1;
                        }
                    }

                    ItemStack keyItem = crate.createKeyItem(amount);
                    if (target.getInventory().firstEmpty() == -1) {
                        target.getWorld().dropItemNaturally(target.getLocation(), keyItem);
                        String var10001 = this.plugin.getPrefix();
                        sender.sendMessage(var10001 + LootCrates.colorize("&ePlayer's inventory was full, dropped on ground."));
                    } else {
                        target.getInventory().addItem(keyItem);
                    }

                    target.sendMessage(this.plugin.getPrefix() + this.plugin.getMessage("key-received").replace("%amount%", String.valueOf(amount)).replace("%key%", crate.getKeyName()));
                    String var10 = this.plugin.getPrefix();
                    sender.sendMessage(var10 + LootCrates.colorize("&aGave " + amount + "x " + crate.getKeyName() + " &ato " + target.getName()));
                }
            }
        }
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage(LootCrates.colorize("&6&l=== Available Crates ==="));

        for(CustomCrate crate : this.plugin.getCustomCrateManager().getAllCrates()) {
            int rewardCount = this.plugin.getRewardManager().getRewards(crate.getId()).size();
            String var10001 = crate.getId();
            sender.sendMessage(LootCrates.colorize("&e" + var10001 + " &7- " + crate.getDisplayName() + " &8(" + rewardCount + " rewards)"));
        }

        if (this.plugin.getCustomCrateManager().getAllCrates().isEmpty()) {
            sender.sendMessage(LootCrates.colorize("&7No crates configured! Use /lcadmin edit to create some."));
        }

    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("lootcrates.admin.reload")) {
            String var2 = this.plugin.getPrefix();
            sender.sendMessage(var2 + this.plugin.getMessage("no-permission"));
        } else {
            this.plugin.reloadPlugin();
            String var10001 = this.plugin.getPrefix();
            sender.sendMessage(var10001 + this.plugin.getMessage("reload-success"));
        }
    }

    private void handlePurge(CommandSender sender) {
        if (!sender.hasPermission("lootcrates.admin.hollowpurple")) {
            String var2 = this.plugin.getPrefix();
            sender.sendMessage(var2 + this.plugin.getMessage("no-permission"));
        } else {
            try {
                sender.sendMessage("Purging Orphans..");
                this.plugin.getHologramManager().purgeOrphans();
                this.plugin.getHologramManager().removeAllHolograms();
                this.plugin.getHologramManager().loadHolograms();
                sender.sendMessage("Recreated All Holograms!");
            } catch (Exception e) {
                sender.sendMessage(LootCrates.colorize("&cError: &e" + e.getMessage()));
                sender.sendMessage(LootCrates.colorize("&cOperation Failed!"));
            }
        }
    }

    private void handleEdit(CommandSender sender) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("lootcrates.admin.edit")) {
                String var3 = this.plugin.getPrefix();
                player.sendMessage(var3 + this.plugin.getMessage("no-permission"));
            } else {
                this.plugin.getCrateEditGUI().openCrateSelectGUI(player);
            }
        } else {
            String var10001 = this.plugin.getPrefix();
            sender.sendMessage(var10001 + LootCrates.colorize("&cThis command can only be used by players!"));
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return this.filterCompletions(Arrays.asList("help", "give", "givekey", "edit", "list", "reload", "refresh", "purge"), args[0]);
        } else if (args.length != 2 || !args[0].equalsIgnoreCase("give") && !args[0].equalsIgnoreCase("givekey")) {
            return (List<String>)(args.length != 3 || !args[0].equalsIgnoreCase("give") && !args[0].equalsIgnoreCase("givekey") ? new ArrayList() : this.filterCompletions(this.plugin.getCustomCrateManager().getAllCrates().stream().map(CustomCrate::getId).collect(Collectors.toList()), args[2]));
        } else {
            return this.filterCompletions(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
        }
    }

    private List<String> filterCompletions(List<String> completions, String input) {
        return completions.stream().filter((s) -> s.toLowerCase().startsWith(input.toLowerCase())).collect(Collectors.toList());
    }
}
