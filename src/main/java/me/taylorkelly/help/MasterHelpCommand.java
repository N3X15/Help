/**
 * 
 */
package me.taylorkelly.help;

import java.util.HashMap;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * @author Rob
 *
 */
public class MasterHelpCommand implements CommandExecutor {

	private Help plugin;
	private HashMap<String, CommandExecutor> executors = new HashMap<String, CommandExecutor>();
    
	public MasterHelpCommand(Help p) {
		plugin=p;
	}
    
    public void registerExecutor(String subcmd, CommandExecutor cmd) {
        executors.put(subcmd.toLowerCase(), cmd);
    }

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName().toLowerCase();
     
        //args=groupArgs(args);
        if (sender instanceof Player) {
            if (commandName.equals("help")) {
            	Player player = (Player) sender;
                if (args.length == 0)
                    return false;
                // Handle /help (#)
                if (args.length == 0 || (args.length == 1 && Help.isInteger(args[0]))) {
                    Lister lister = new Lister(plugin.helpList, player);
                    if (args.length == 1) {
                        int page = Integer.parseInt(args[0]);
                        if (page < 1) {
                            player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                            return true;
                        } else if (page > lister.getMaxPages(player)) {
                            player.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages(player) + " pages of help");
                            return true;
                        }
                        lister.setPage(page);
                    } else {
                        lister.setPage(1);
                    }
                    lister.list();
                    return true;
                }
                
                String subcommandName = args[0].toLowerCase();
                
                if (!executors.containsKey(subcommandName)) {
                	if(plugin.helpList.containsPlugin(subcommandName)) {
                		showPluginHelp(subcommandName,args, player);
                		return true;
                	} else {
                		if(plugin.containsCommand(subcommandName)) {
                			showCommandHelp(subcommandName, player);
                			return true;
                		} else {
                			return false;
                		}
                	}
                }
                
                return executors.get(subcommandName).onCommand(sender, command, commandLabel, args);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (commandName.equals("help")) {
                ConsoleCommandSender console = (ConsoleCommandSender) sender;
                if (args.length == 0) {
                    return false;
                } else if (args[0].equalsIgnoreCase("version")) {
                    console.sendMessage("You're running: " + ChatColor.AQUA.toString() + plugin.getName() + " " + plugin.getVersion());
                } else
                return true;
            }
            return false;
        }
        return false;
    }

	private void showPluginHelp(String subcommandName, String[] split,Player player) {
		Lister lister = new Lister(plugin.helpList, split[0], player);
        if (split.length == 2) {
            int page = Integer.parseInt(split[1]);
            if (page < 1) {
                player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                return;
            } else if (page > lister.getMaxPages(player)) {
                player.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages(player) + " pages of help");
                return;
            }
            lister.setPage(page);
        } else {
            lister.setPage(1);
        }
        lister.list();
	}

	private void showCommandHelp(String cmd, Player player) {
		HelpCommand command = plugin.getRegisteredCommand(cmd);
		
        ChatColor commandColor = ChatColor.RED;
        ChatColor descriptionColor = ChatColor.WHITE;
        ChatColor introDashColor = ChatColor.GOLD;
        ChatColor introTextColor = ChatColor.WHITE;
        int width = 325;
        
        String subtro = " /" + cmd.toUpperCase() + " HELP (1/1) ";
        int sizeRemaining = (int) ((width - MinecraftFontWidthCalculator.getStringWidth(subtro)) * 0.93);
        String dashes = dashes(sizeRemaining / 2);
        sizeRemaining = (int) ((width - MinecraftFontWidthCalculator.getStringWidth(dashes + subtro)) * 0.93);
        player.sendMessage(introDashColor.toString() + dashes + introTextColor.toString() + subtro + introDashColor.toString() + dashes(sizeRemaining));
        
        StringBuilder entryBuilder = new StringBuilder();
        entryBuilder.append(commandColor.toString());
        entryBuilder.append("/");
        entryBuilder.append(command.name);
        entryBuilder.append(ChatColor.WHITE.toString());
        entryBuilder.append(" : ");
        entryBuilder.append(descriptionColor.toString());
        
        //Find remaining length left
        sizeRemaining = width - MinecraftFontWidthCalculator.getStringWidth(entryBuilder.toString());
        entryBuilder = new StringBuilder(entryBuilder.toString().replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]" + commandColor.toString()));

        int descriptionSize = MinecraftFontWidthCalculator.getStringWidth(command.description);
        if (sizeRemaining > descriptionSize) {
            entryBuilder.append(whitespace(sizeRemaining - descriptionSize));
            entryBuilder.append(command.description.replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]" + descriptionColor.toString()));
        } else if (sizeRemaining < descriptionSize) {
            player.sendMessage(entryBuilder.toString());
            player.sendMessage("  " + command.description.replace("[", ChatColor.GRAY.toString() + "[").replace("]", "]" + descriptionColor.toString()));
        }
        player.sendMessage(entryBuilder.toString());
	}


    public String whitespace(int length) {
        int spaceWidth = MinecraftFontWidthCalculator.getCharWidth(' ');

        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < length-spaceWidth; i += spaceWidth) {
            ret.append(" ");
        }

        return ret.toString();
    }

    public String dashes(int length) {
        int spaceWidth = MinecraftFontWidthCalculator.getCharWidth('-');

        StringBuilder ret = new StringBuilder();

        for (int i = 0; i < length-spaceWidth; i += spaceWidth) {
            ret.append("-");
        }

        return ret.toString();
    }
}
