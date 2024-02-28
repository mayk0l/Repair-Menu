package org.gelic.us.repairmenu;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class RepairMenu extends JavaPlugin implements Listener {

    private Economy economy;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault not found! This plugin requires Vault to work.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("repairmenu")) {
            openRepairMenu(player);
            return true;
        }
        return false;
    }

    private void openRepairMenu(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&', "&3&lRepair Menu");
        Inventory menu = Bukkit.createInventory(player, 9, title);

        ItemStack blackGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta glassMeta = blackGlass.getItemMeta();
        glassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b"));
        blackGlass.setItemMeta(glassMeta);

        // Add black glass panels
        menu.setItem(0, blackGlass);
        menu.setItem(1, blackGlass);
        menu.setItem(2, blackGlass);
        menu.setItem(3, blackGlass);
        menu.setItem(5, blackGlass);
        menu.setItem(6, blackGlass);
        menu.setItem(7, blackGlass);
        menu.setItem(8, blackGlass);

        // Add repair item
        ItemStack repairItem = new ItemStack(Material.ANVIL);
        ItemMeta repairMeta = repairItem.getItemMeta();
        repairMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lRepair Armor"));


        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to repair your armor.");
        lore.add(ChatColor.YELLOW + "Cost: $10.0");

        repairMeta.setLore(lore);
        repairItem.setItemMeta(repairMeta);

        menu.setItem(4, repairItem);

        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = ChatColor.translateAlternateColorCodes('&', "&3&lRepair Menu");
        if (event.getView().getTitle().equals(title)) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() == Material.ANVIL) {
                double repairCost = calculateRepairCost(player);
                if (economy.has(player, repairCost)) {
                    economy.withdrawPlayer(player, repairCost);
                    int repaired = 0;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (repairArmor(item)) repaired++;
                }   if (repaired > 0) {
                        player.sendMessage(ChatColor.AQUA + "Inventory successfully repaired for $" + repairCost + ".");
                    } else {
                        player.sendMessage(ChatColor.AQUA + "You don't have items to repair.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have enough coins to repair your armor and tools.");
                }
            }
        }
    }

    private double calculateRepairCost(Player player) {
        return 10.0;
    }

    private boolean repairArmor(ItemStack item){
        if (item == null) return false;
        if (!isTool(item.getType())) return false;

        if (item.getDurability() == 0) return false;
        item.setDurability((short) 0);
        return true;
    }

    private boolean isTool(Material material) {
        switch (material) {
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
            case IRON_HELMET:
            case IRON_CHESTPLATE:
            case IRON_LEGGINGS:
            case IRON_BOOTS:
            case GOLD_HELMET:
            case GOLD_CHESTPLATE:
            case GOLD_LEGGINGS:
            case GOLD_BOOTS:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_BOOTS:
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
            case DIAMOND_SWORD:
            case IRON_SWORD:
            case GOLD_SWORD:
            case STONE_SWORD:
            case WOOD_SWORD:
            case DIAMOND_PICKAXE:
            case IRON_PICKAXE:
            case GOLD_PICKAXE:
            case STONE_PICKAXE:
            case WOOD_PICKAXE:
            case DIAMOND_AXE:
            case IRON_AXE:
            case GOLD_AXE:
            case STONE_AXE:
            case WOOD_AXE:
            case DIAMOND_SPADE:
            case IRON_SPADE:
            case GOLD_SPADE:
            case STONE_SPADE:
            case WOOD_SPADE:
            case DIAMOND_HOE:
            case IRON_HOE:
            case GOLD_HOE:
            case STONE_HOE:
            case WOOD_HOE:
            case BOW:
                return true;
            default:
                return false;
        }
    }
}
