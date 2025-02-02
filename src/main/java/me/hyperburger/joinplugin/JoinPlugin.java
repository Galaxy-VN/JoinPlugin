package me.hyperburger.joinplugin;

import com.earth2me.essentials.Essentials;
import featherpowders.implementations.events.InventoryEventsHandler;
import me.hyperburger.joinplugin.commands.CommandManager;
import me.hyperburger.joinplugin.commands.TabCompletion;
import me.hyperburger.joinplugin.events.ChatEventsHandler;
import me.hyperburger.joinplugin.listeners.*;
import me.hyperburger.joinplugin.listeners.motd.JoinMotd;
import me.hyperburger.joinplugin.listeners.motd.ServerMOTD;
import me.hyperburger.joinplugin.manager.FileManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * @author HyperBurger
 */

public final class JoinPlugin extends JavaPlugin {

    public static final double version = 2.4;
    public static Essentials essentials;

    @Override
    public void onEnable() {
        new Metrics(this, 12590);

        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.reloadConfig();

        filemanager = new FileManager(this);

        startMsg();
        checkEssentials();
        checkPluginVanish();

        registerEvents();
        registerCommands();
    }

    public void registerEvents(){
        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new JoinListener(this), this);
        pluginManager.registerEvents(new FirstJoinListener(this), this);
        pluginManager.registerEvents(new JoinMotd(this), this);
        pluginManager.registerEvents(new QuitListener(this), this);
        pluginManager.registerEvents(new MaintenanceListener(this), this);
        pluginManager.registerEvents(new ServerMOTD(this),this);
        pluginManager.registerEvents(new InventoryEventsHandler(), this);
        pluginManager.registerEvents(new ChatEventsHandler(this), this);
    }

    public void registerCommands() {
        this.getCommand("joinplugin").setExecutor(new CommandManager(this));
        this.getCommand("joinplugin").setTabCompleter(new TabCompletion());
    }

    public boolean checkEssentials(){
        if (this.getServer().getPluginManager().getPlugin("Essentials") != null){
            essentials = (Essentials) this.getServer().getPluginManager().getPlugin("Essentials");
            return true;
        }
        return false;
    }

    public boolean checkPluginVanish() {
        if (this.getServer().getPluginManager().getPlugin("PremiumVanish") != null || this.getServer().getPluginManager().getPlugin("SuperVanish") != null) {
            return true;
        }
        return false;
    }

    public void startMsg(){
            System.out.println("  ------------(Join Plugin)------------  ");
            System.out.println(" ");
            System.out.println("    JoinPlugin: Successfully Loaded!");
            System.out.println("    Author: HyperBurger, GalaxyVN");
            System.out.println("    version = " + version);
            System.out.println(" ");
            System.out.println("  -------------------------------------  ");
    }

    public static boolean mc18 (){
        return Bukkit.getServer().getVersion().contains("1.8");
    }
    public static boolean mc116 (){
        return Bukkit.getServer().getVersion().contains("1.16");
    }
    public static boolean mc117 (){
        return Bukkit.getServer().getVersion().contains("1.17");
    }

    private FileManager filemanager;

    public FileManager getFileManager() {
        return this.filemanager;
    }

}
