/**
 * 
 */
package me.taylorkelly.help;

import java.util.HashMap;

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
                
                if (!executors.containsKey(subcommandName))
                    return false;
                
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

}
