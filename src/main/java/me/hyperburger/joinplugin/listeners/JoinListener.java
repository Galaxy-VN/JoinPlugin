package me.hyperburger.joinplugin.listeners;

import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import de.myzelyam.api.vanish.VanishAPI;
import me.hyperburger.joinplugin.JoinPlugin;
import me.hyperburger.joinplugin.manager.FileManager;
import me.hyperburger.joinplugin.utilis.Placeholders;
import me.hyperburger.joinplugin.utilis.Ucolor;
import me.hyperburger.joinplugin.utilis.Utilis;
import me.hyperburger.joinplugin.utilis.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final JoinPlugin plugin;
    public JoinListener(JoinPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        Configuration config = plugin.getConfig();
        ConfigurationSection rewardSection = config.getConfigurationSection("Groups");
        FileConfiguration data = plugin.getFileManager().getFileConfig(FileManager.Files.PLAYERS_CUSTOM);

        if (!player.hasPlayedBefore()) return;
        if (!config.getBoolean("Enable Join Message By Groups")) return;
        if (rewardSection == null) return;

        if (data.contains(player.getUniqueId().toString()) && data.getString(player.getUniqueId().toString() + ".Join Message") != null) {
            // EssentialsX Vanish Support
            if (config.getBoolean("SupportEssentialXVanish") && plugin.checkEssentials()) {
                if (JoinPlugin.essentials.getUser(player.getUniqueId()).isVanished()) {
                    event.setJoinMessage("");
                } else {
                    event.setJoinMessage(Ucolor.translateColorCodes(Placeholders.replace(player, data.getString(player.getUniqueId().toString() + ".Join Message"))
                            .replace("%player%", player.getName()
                                    .replace("%playerdisplayname%", player.getDisplayName()))));
                }
            }
            if (config.getBoolean("SupportPluginVanish") && plugin.checkPluginVanish()) {
                if (VanishAPI.isInvisible(player)) {
                    event.setJoinMessage("");

                } else {
                    event.setJoinMessage(Ucolor.translateColorCodes(Placeholders.replace(player, data.getString(player.getUniqueId().toString() + ".Join Message"))
                            .replace("%player%", player.getName()
                                    .replace("%playerdisplayname%", player.getDisplayName()))));
                }
            }
        } else {
            for (String key : rewardSection.getKeys(false)) {

                ConfigurationSection idSection = rewardSection.getConfigurationSection(key);     /* We now have idSection and can access it. */
                String permission = idSection.getString("permission");

                if (permission != null && player.hasPermission((permission))) {

                    // EssentialsX Vanish Support
                    if (config.getBoolean("SupportEssentialXVanish") && plugin.checkEssentials()) {
                        if (JoinPlugin.essentials.getUser(player.getUniqueId()).isVanished()) {
                            event.setJoinMessage("");

                        } else {
                            setCustomJoinMessage(event, player, idSection); // Perform this if essentials is enabled.
                        }
                    }

                    if (config.getBoolean("SupportPluginVanish") && plugin.checkPluginVanish()) {
                        if (VanishAPI.isInvisible(player)) {
                            event.setJoinMessage("");

                        } else {
                            setCustomJoinMessage(event, player, idSection); // Perform this if premiumvanish|supervanish is enabled.
                        }
                    }

                    setCustomJoinMessage(event, player, idSection);  // Perform this if essentials is disabled.

                    // Perform Sounds
                    for (Player allPlayers : Bukkit.getOnlinePlayers()) {
                        try {
                            if (!idSection.getString("Sound").equalsIgnoreCase("")) {
                                allPlayers.playSound(player.getLocation(), Sound.valueOf(idSection.getString("Sound")), 1, 1);
                            }
                        } catch (IllegalArgumentException e) {
                            Utilis.logMessage(this.getClass(), "[JoinPlugin] The sound " + idSection.getString("Sound") + " doesn't exist in your server version!");
                        }
                    }

                    // Perform Fireworks
                    if (idSection.getBoolean("Firework")) {
                        Utilis.spawnFireworks(player.getLocation(), 1);
                    }

                    // Perform Commands
                    for (String s : idSection.getStringList("commands")) {
                        Utilis.configCommand(s, player);
                    }
                }
            }
        }

        // Perform Titles
        if (plugin.getConfig().getBoolean("Join Title.Enabled")) {
            Titles.sendTitle(player,
                    config.getInt("Join Title.fadeIn") * 20,
                    config.getInt("Join Title.Stay") * 20,
                    config.getInt("Join Title.fadeOut") * 20,
                    Ucolor.translateColorCodes(Placeholders.replace(player, String.valueOf(config.getString("Join Title.Title")))),
                    Ucolor.translateColorCodes(Placeholders.replace(player, String.valueOf(config.getString("Join Title.SubTitle")))));
        }

        // Perform ActionBar
        if (plugin.getConfig().getBoolean("Join ActionBar.Enabled")) {
            ActionBar.sendActionBar(plugin, player, Ucolor.translateColorCodes(Placeholders.replace(player, String.valueOf(config.getString("Join ActionBar.Message"))
                    .replace("%player%", player.getName()
                            .replace("%playerdisplayname%", player.getDisplayName())))), config.getInt("Join ActionBar.Duration") * 10L);
        }

        // Perform BossBar
        if (plugin.getConfig().getBoolean("Join BossBar.Enabled")) {
            BossBar.sendBossbar(player,
                    Ucolor.translateColorCodes(Placeholders.replace(player, String.valueOf(config.getString("Join BossBar.Message")))),
                    config.getInt("Join BossBar.Duration"),
                    config.getString("Join BossBar.Style"),
                    config.getString("Join BossBar.Color"));
        }

        // Join Discord
    }

    /**
     * Set the join message to a custom one and support Hex Colors and Placeholders.
     * Use in this class only to avoid confusion.
     *
     * @param event     The player join event.
     * @param player    The player to send the message to.
     * @param idSection The Configuration Section of the config.yml I.G: Groups:
     */
    private void setCustomJoinMessage(PlayerJoinEvent event, Player player, ConfigurationSection idSection) {
        event.setJoinMessage(Ucolor.translateColorCodes(Placeholders.replace(player, String.valueOf(idSection.getString("Join Message"))
                .replace("%player%", player.getName()
                .replace("%playerdisplayname%", player.getDisplayName())))));

    }
}


