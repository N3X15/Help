package me.taylorkelly.help.commands;

import me.taylorkelly.help.Help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReload implements CommandExecutor {

	private Help plugin;

	public CommandReload(Help help) {
		this.plugin=help;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		plugin.helpList.reload((Player) sender, plugin.getDataFolder());
		return true;
	}

}
