package me.kyllian.gameboy.commands;

import me.kyllian.gameboy.GameboyPlugin;
import me.kyllian.gameboy.data.Pocket;
import nitrous.Cartridge;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class GameboyExecutor implements CommandExecutor {

    private GameboyPlugin plugin;

    public GameboyExecutor(GameboyPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorTranslate("&cThis command can only be run by players"));
            return true;
        }
        Player player = (Player) sender;
        Pocket pocket = plugin.getPlayerHandler().getPocket(player);
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {
                if (pocket.isEmpty()) {
                    player.sendMessage(colorTranslate("&cNo running game!"));
                    return true;
                }
                pocket.stopEmulator(player);
                player.sendMessage(colorTranslate("&aStopped game succesfully"));
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                try {
                    plugin.getRomHandler().loadRoms();
                    sender.sendMessage(colorTranslate("&AReloading complete!"));
                } catch (IOException exception) {
                    sender.sendMessage(colorTranslate("&cReloading failed!"));
                }
                return true;
            }
        }
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("play")) {
                if (!pocket.isEmpty()) {
                    player.sendMessage(colorTranslate("&cAlready playing a game, stop by doing /gameboy stop"));
                    return true;
                }
                String gameName = "";
                for (int i = 1; i != args.length; i++) {
                    gameName += args[i] + " ";
                }
                gameName = gameName.trim();
                Cartridge foundCartridge = plugin.getRomHandler().getRoms().get(gameName);
                if (foundCartridge == null) {
                    sender.sendMessage(colorTranslate("&cGame not found!"));
                    showHelp(sender);
                    return true;
                }
                player.sendMessage(colorTranslate("&aNow playing: " + foundCartridge.gameTitle));
                plugin.getPlayerHandler().loadGame(player, foundCartridge);
                return true;
            }
        }
        showHelp(sender);
        return true;
    }

    public void showHelp(CommandSender sender) {
        sender.sendMessage(colorTranslate("&7The current games you can play are: " + String.join(", ", plugin.getRomHandler().getRoms().keySet()) + "\n&7Type /gameboy play 'name' to play a game!\n&7Unsure how the plugin works? Join my discord: https://discord.gg/zgKr2YM"));
    }

    public String colorTranslate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
