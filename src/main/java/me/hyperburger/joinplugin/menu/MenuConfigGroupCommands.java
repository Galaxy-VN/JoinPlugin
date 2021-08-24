package me.hyperburger.joinplugin.menu;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import featherpowders.ui.PlayerUI;
import featherpowders.ui.chest.ChestUI;
import featherpowders.utils.ItemBuilder;
import me.hyperburger.joinplugin.JoinPlugin;
import me.hyperburger.joinplugin.JoinPluginHelper;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class MenuConfigGroupCommands extends ChestUI {

    private int page;

    public MenuConfigGroupCommands(Player player, MenuConfigGroup menuConfigGroup, HashMap<String, Object> groups) {
        super(player, "Config Group > " + groups.get("Name") + " > Commands", 6);
        this.page = page;

        for (int i = 1; i < 9; i++) set(i, 5, MenuGUI.BORDER, null);
        set(0, 5, new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial(), 1, SkullUtils.applySkin(XMaterial.PLAYER_HEAD.parseItem().getItemMeta(), "http://textures.minecraft.net/texture/8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f"))
                .name("§e <- Go Back ").getItem(), event -> {
            PlayerUI.openUI(player, menuConfigGroup);
        });
        set(7, 5, new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial(), 1, SkullUtils.applySkin(XMaterial.PLAYER_HEAD.parseItem().getItemMeta(), "http://textures.minecraft.net/texture/b056bc1244fcff99344f12aba42ac23fee6ef6e3351d27d273c1572531f"))
                .name("§a   Create Commands   ")
                .lore(
                        "",
                        "§7Create new command",
                        "",
                        "§7Click to§e create"
                )
                .getItem(), event -> {
            event.setCancelled(true);
            setCancelDragEvent(true);
            player.closeInventory();
            player.sendMessage(new String[]{
                    "",
                    "§7Please type in chat how much player can in server",
                    "§7Type §ccancel §7to cancel",
                    ""
            });
        });
        renderPage();
    }

    private void renderPage() {
        if (page > 0) {
            set(2, 5, new ItemBuilder(XMaterial.ARROW.parseMaterial(), 1).name("§e <- Previous Page ").getItem(), event -> {
                event.setCancelled(true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setCancelDragEvent(true);
                    }
                }.runTaskLater(JoinPlugin.getPlugin(JoinPlugin.class), 1);
                page--;
                renderPage();
            });
        } else {
            set(2, 5, MenuGUI.BORDER, null);
        }
    }

    @Override
    public void failback(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
