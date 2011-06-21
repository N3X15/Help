package me.taylorkelly.help;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.taylorkelly.help.commands.CommandPlugins;
import me.taylorkelly.help.commands.CommandReload;
import me.taylorkelly.help.commands.CommandSearch;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Help extends JavaPlugin {

    private String name;
    private String version;
    public HelpList helpList;
	public List<HelpCommand> commandList = new ArrayList<HelpCommand>();

    public Help() {
        helpList = new HelpList();
        File folder = new File("plugins", "Help");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, "ExtraHelp");
        if (file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        LegacyHelpLoader.load(this.getDataFolder(), helpList);
        HelpLoader.load(this.getDataFolder(), helpList);

        HelpPermissions.initialize(getServer());
        HelpSettings.initialize(getDataFolder());

        MasterHelpCommand mhc = new MasterHelpCommand(this);
        mhc.registerExecutor("plugins", new CommandPlugins(this));
        mhc.registerExecutor("search", new CommandSearch(this));
        mhc.registerExecutor("reload", new CommandReload(this));
        getCommand("help").setExecutor(mhc);
        
        this.registerCommand("help Help", "Displays more /help options", this, true);
        this.registerCommand("help", "Displays the basic Help menu", this);
        this.registerCommand("help [plugin]", "Displays the full help for [plugin]", this, true);
        this.registerCommand("help plugins", "Show all the plugins with Help entries", this);
        this.registerCommand("help search [query]", "Search the help entries for [query]", this);
        this.registerCommand("help reload", "Reload the ExtraHelp.yml entries", this);

        loadCommands();
        HelpLogger.info(name + " " + version + " enabled");
    }
    
    /**
     * Stolen from CraftBukkit, with added Permissions checks
     * @param plugin
     */
    @SuppressWarnings("unchecked")
    public void parsePluginCommands(Plugin plugin) {
        Object object = plugin.getDescription().getCommands();

        if (object == null) {
            return;
        }

        Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;

        if (map != null) {
            for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
                HelpCommand newCmd = new HelpCommand(entry.getKey(), plugin);
                Object description = entry.getValue().get("description");
                Object usage = entry.getValue().get("usage");
                Object aliases = entry.getValue().get("aliases");
                Object permissions = entry.getValue().get("permissions");

                if (description != null) {
                    newCmd.setDescription(description.toString());
                }

                if (usage != null) {
                    newCmd.setUsage(usage.toString());
                }

                if (permissions != null) {
                    List<String> permList = new ArrayList<String>();

                    if (permissions instanceof List) {
                        for (Object o : (List<Object>) permissions) {
                            permList.add(o.toString());
                        }
                    } else {
                        permList.add(permissions.toString());
                    }

                    newCmd.setPermissions(permList);
                }

                if (aliases != null) {
                    List<String> aliasList = new ArrayList<String>();

                    if (aliases instanceof List) {
                        for (Object o : (List<Object>) aliases) {
                            aliasList.add(o.toString());
                        }
                    } else {
                        aliasList.add(aliases.toString());
                    }

                    newCmd.setAliases(aliasList);
                }

                commandList.add(newCmd);
            }
        }
    }

    private void loadCommands() {
    	commandList.clear();
		for(Plugin p : getServer().getPluginManager().getPlugins()) {
			parsePluginCommands(p);
		}
		HelpLogger.info("Added "+commandList.size()+" registered commands.");
	}
    
    public HelpCommand getRegisteredCommand(String name) {
    	for(HelpCommand hc : commandList) {
    		if(hc.name.equalsIgnoreCase(name))
    			return hc;
    		for(String alias : hc.aliases) {
    			if(alias.equalsIgnoreCase(name))
    				return hc;
    		}
    	}
		return null;
    }

/*    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (commandName.equals("help")) {
                // /help (#)
                if (split.length == 0 || (split.length == 1 && isInteger(split[0]))) {
                    Lister lister = new Lister(helpList, player);
                    if (split.length == 1) {
                        int page = Integer.parseInt(split[0]);
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

                // /help plugins
                } else if (split.length == 1 && split[0].equalsIgnoreCase("plugins")) {
                    helpList.listPlugins(player);

                // /help reload
                } else if (split.length == 1 && split[0].equalsIgnoreCase("reload")) {
                    helpList.reload(player, getDataFolder());

                // /help search [query]
                } else if (split.length > 1 && split[0].equalsIgnoreCase("search")) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    Searcher searcher = new Searcher(helpList);
                    searcher.addPlayer(player);
                    searcher.setQuery(name);
                    searcher.search();

                // /help [plugin] (#)
                } else if (split.length == 1 || (split.length == 2 && isInteger(split[1]))) {
                    Lister lister = new Lister(helpList, split[0], player);
                    if (split.length == 2) {
                        int page = Integer.parseInt(split[1]);
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
                } else {
                    return false;
                }
                return true;
            }
        } //TODO Console help
        return false;
    }
*/
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean registerCommand(String command, String description, Plugin plugin) {
        return helpList.registerCommand(command, description, plugin.getDescription().getName(), false, new String[]{}, this.getDataFolder());
    }

    public boolean registerCommand(String command, String description, Plugin plugin, boolean main) {
        return helpList.registerCommand(command, description, plugin.getDescription().getName(), main, new String[]{}, this.getDataFolder());
    }

    public boolean registerCommand(String command, String description, Plugin plugin, String... permissions) {
        return helpList.registerCommand(command, description, plugin.getDescription().getName(), false, permissions, this.getDataFolder());
    }

    public boolean registerCommand(String command, String description, Plugin plugin, boolean main, String... permissions) {
        return helpList.registerCommand(command, description, plugin.getDescription().getName(), main, permissions, this.getDataFolder());
    }

	public String getName() {
		return name;
	}
	public String getVersion() {
		return version;
	}

	public enum HelpReciever {

        PLAYER, CONSOLE;
    }

	public boolean containsCommand(String commandName) {
		return getRegisteredCommand(commandName)!=null;
	}
}
