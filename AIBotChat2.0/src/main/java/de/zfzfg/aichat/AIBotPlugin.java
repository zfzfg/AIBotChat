package de.zfzfg.aichat;

import org.bukkit.plugin.java.JavaPlugin;

public final class AIBotPlugin extends JavaPlugin {

    private static AIBotPlugin instance;
    private TextGenAPI textGenAPI;

    @Override
    public void onEnable() {
        instance = this;
        
        // Config erstellen falls nicht vorhanden
        saveDefaultConfig();
        
        // API initialisieren
        textGenAPI = new TextGenAPI(this);
        
        // Commands registrieren
        getCommand("ai").setExecutor(new AICommand(this));
        getCommand("aireload").setExecutor(new AICommand(this));  // Neu: Reload-Command
        
        getLogger().info("════════════════════════════════════════");
        getLogger().info("  AI Chat Plugin erfolgreich geladen!");
        getLogger().info("  API URL: " + getConfig().getString("api.url"));
        getLogger().info("  Nutze: /ai <deine Frage>");
        getLogger().info("════════════════════════════════════════");
    }

    @Override
    public void onDisable() {
        getLogger().info("AI Chat Plugin deaktiviert.");
    }

    public static AIBotPlugin getInstance() {
        return instance;
    }

    public TextGenAPI getTextGenAPI() {
        return textGenAPI;
    }

    public void reloadPlugin() {
        reloadConfig();
        textGenAPI = new TextGenAPI(this);  // Re-initialisiere API mit neuer Config
        getLogger().info("AIBotChat config reloaded.");
    }
}