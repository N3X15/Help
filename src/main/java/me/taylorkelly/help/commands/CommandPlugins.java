package me.taylorkelly.help.commands;

import me.taylorkelly.help.Help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPlugins implements CommandExecutor {

	private Help plugin;

	public CommandPlugins(Help help) {
		this.plugin=help;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command,
			String[] args) {
		this.plugin.helpList.listPlugins((Player) sender);
		return true;
	}

}
