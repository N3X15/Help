/**
 * 
 */
package me.taylorkelly.help.commands;

import me.taylorkelly.help.Help;
import me.taylorkelly.help.Searcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Rob
 *
 */
public class CommandSearch implements CommandExecutor {

	private Help plugin;

	public CommandSearch(Help help) {
		this.plugin=help;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command,
			String[] split) {
		String name = "";
        for (int i = 1; i < split.length; i++) {
            name += split[i];
            if (i + 1 < split.length) {
                name += " ";
            }
        }
        Searcher searcher = new Searcher(plugin.helpList);
        searcher.addPlayer((Player) sender);
        searcher.setQuery(name);
        searcher.search();
		return true;
	}

}
