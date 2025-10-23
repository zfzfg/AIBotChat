package de.zfzfg.aichat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AICommand implements CommandExecutor {

    private final AIBotPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public AICommand(AIBotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("aireload")) {
            if (!sender.hasPermission("aibotchat.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reload.");
                return true;
            }
            plugin.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "AIBotChat config reloaded successfully.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Permissions-Check basierend auf Config
        boolean requirePermission = plugin.getConfig().getBoolean("require-permission", false);
        if (requirePermission && !player.hasPermission("aibotchat.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        // Check cooldown
        if (!checkCooldown(player)) {
            return true;
        }

        // Check arguments
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /ai <your question>");
            player.sendMessage(ChatColor.GRAY + "Example: /ai How do I set a home?");
            return true;
        }

        String userInput = String.join(" ", args);
        
        // Status message (ohne Emoji)
        player.sendMessage(ChatColor.GRAY + "[AI] Thinking...");

        // Async API call
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getTextGenAPI().generateResponse(userInput)
                .thenAccept(response -> {
                    // Back to main thread for Minecraft API
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        sendFormattedResponse(player, response);
                    });
                })
                .exceptionally(throwable -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage(ChatColor.RED + "❌ [AI] Error: " + throwable.getMessage());
                        player.sendMessage(ChatColor.YELLOW + "Tip: Check if text-generation-webui is running and a model is loaded.");
                    });
                    plugin.getLogger().severe("API Error: " + throwable.getMessage());
                    return null;
                });
        });

        return true;
    }

    private boolean checkCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldownMs = plugin.getConfig().getLong("cooldown-seconds", 3) * 1000;

        if (cooldowns.containsKey(playerId)) {
            long timeLeft = (cooldowns.get(playerId) + cooldownMs - now);
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "⏳ Please wait " + 
                    (timeLeft / 1000) + " more seconds.");
                return false;
            }
        }

        cooldowns.put(playerId, now);
        return true;
    }

    private void sendFormattedResponse(Player player, String response) {
        player.sendMessage(ChatColor.AQUA + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage(ChatColor.GREEN + "[AI] " + ChatColor.WHITE + response);  // Ohne Emoji
        player.sendMessage(ChatColor.AQUA + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}