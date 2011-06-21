/**
 * 
 */
package me.taylorkelly.help;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author Rob
 *
 */
public class HelpCommand {
	public String name="";
	public String usage="";
	public String description="";
	public String permission="";
	public Plugin plugin;
	public List<String> permissions=new ArrayList<String>();
	public List<String> aliases=new ArrayList<String>();
	
	public HelpCommand(){}
	
	
	public HelpCommand(String name,Plugin plugin) {
		this.name=name;
		this.plugin=plugin;
	}


	public void setDescription(String string) {
		this.description=string;
	}


	public void setUsage(String string) {
		this.usage=string;
	}


	public void setAliases(List<String> aliasList) {
		this.aliases=aliasList;
	}

	public void setPermissions(List<String> permList) {
		this.permissions=permList;
	}
	
	public boolean visible(Player p) {
		if(permissions.size()==0) return true;
		for(String perm:permissions) {
			if(!HelpPermissions.permission(p, perm))
				return false;
		}
		return true;
	}
}
