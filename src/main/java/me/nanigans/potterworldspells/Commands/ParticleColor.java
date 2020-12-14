package me.nanigans.potterworldspells.Commands;

import me.nanigans.potterworldspells.Utils.Config.FilePaths;
import me.nanigans.potterworldspells.Utils.Config.YamlGenerator;
import me.nanigans.potterworldspells.Utils.Config.YamlPaths;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.File;

public class ParticleColor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equals("particlecolor")){

            if(sender instanceof Player){

                Player player = (Player) sender;
                if(args.length > 0){
                    try {
                        Color color = Color.decode(args[0]);

                        File file = new File(FilePaths.USERS+"/"+player.getUniqueId()+".yml");
                        if(file.exists()){
                            YamlGenerator yaml = new YamlGenerator(file.getAbsolutePath());
                            final FileConfiguration data = yaml.getData();
                            data.set(YamlPaths.PARTICLECOLOR.getPath(), color.getRGB());
                            yaml.save();

                            player.sendMessage(ChatColor.GREEN+"Color changed!");

                        }else{
                            player.sendMessage(ChatColor.RED+"You have not set up your wand yet");
                        }
                        return true;

                    }catch(NumberFormatException err){
                        player.sendMessage(ChatColor.RED+"Please specify a valid hex color");
                        return true;
                    }
                }else{
                    player.sendMessage(ChatColor.RED+"Please specify a hex color");
                    return true;
                }

            }else{
                sender.sendMessage(ChatColor.RED+"Only players may use this command");
            }

        }

        return false;
    }
}
