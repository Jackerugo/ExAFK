package me.exafk;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.clip.placeholderapi.PlaceholderAPI;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements Listener {
	Integer timeout = (getConfig().getInt("afk-timeout")) * 20;
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
	}
	
	public void RemoveAFKEffect(PlayerJoinEvent e) { 
		PotionEffect afkEffect = e.getPlayer().getPotionEffect(PotionEffectType.getByName(getConfig().getString("afk-effect.effect")));
		if ( afkEffect != null ) {
			e.getPlayer().removePotionEffect(PotionEffectType.getByName(getConfig().getString("afk-effect.effect")));
		}
	}

	@EventHandler
	public void PlayerGoesAFK(AfkStatusChangeEvent e) {
		@SuppressWarnings("deprecation")
		Player p = e.getAffected().getBase();
	
		Integer titleFadein = (getConfig().getInt("title-settings.fade-in")) * 20;
		Integer titleFadeout = (getConfig().getInt("title-settings.fade-out")) * 20;
		
		if (e.getValue()) {
			
			Random goneAFKTitle = new Random();
		    List<String> listTitle = getConfig().getStringList("afk-messages.titles");
		    int randomMessageTitle = goneAFKTitle.nextInt(listTitle.size());
		    String newMessageTitle = (String)PlaceholderAPI.setPlaceholders(p, listTitle.get(randomMessageTitle));
			
			Random goneAFK = new Random();
		    List<String> listSub = getConfig().getStringList("afk-messages.subtitles");
		    int randomMessage = goneAFK.nextInt(listSub.size());
		    String newMessage = (String)PlaceholderAPI.setPlaceholders(p, listSub.get(randomMessage));
		    
			p.sendTitle(ChatColor.translateAlternateColorCodes('&', format(newMessageTitle)), ChatColor.translateAlternateColorCodes('&', format(newMessage)) , titleFadein, timeout, titleFadeout);
			
			if (getConfig().getString("afk-effect.enabled").equalsIgnoreCase("true")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.getByName(getConfig().getString("afk-effect.effect")), timeout, 1));
			}
			
			if (getConfig().getString("afk-sounds.enabled").equalsIgnoreCase("true")) {
				p.getLocation().getWorld().playSound(p.getLocation(), Sound.valueOf(getConfig().getString("afk-sounds.afk")), 1.0F, 1.0F);
			}
			
			if (getConfig().getString("player-command.enabled").equalsIgnoreCase("true")) {
				p.performCommand(String.valueOf(getConfig().getString("player-command.initial-command")));
			}			
			
		} else {
			
			Random welcomeBackTitle = new Random();
			List<String> wbListTitle = getConfig().getStringList("return-messages.titles");
		    String wbTitleNewMessage = (String)PlaceholderAPI.setPlaceholders(p, wbListTitle.get(welcomeBackTitle.nextInt(wbListTitle.size())));
			
			Random welcomeBack = new Random();
			List<String> wbListSub = PlaceholderAPI.setPlaceholders(p, getConfig().getStringList("return-messages.subtitles"));
		    String wbNewMessage = (String)PlaceholderAPI.setPlaceholders(p, wbListSub.get(welcomeBack.nextInt(wbListSub.size())));
			
		    Integer titleReturnDuration = (getConfig().getInt("title-settings.return-stay")) * 20;
		    
			p.sendTitle(ChatColor.translateAlternateColorCodes('&', format(wbTitleNewMessage)), ChatColor.translateAlternateColorCodes('&', format(wbNewMessage)) , titleFadein, titleReturnDuration, titleFadeout);
			
			if (getConfig().getString("afk-effect.enabled").equalsIgnoreCase("true")) {
				p.removePotionEffect(PotionEffectType.getByName(getConfig().getString("afk-effect.effect")));
			}
			
			if (getConfig().getString("afk-sounds.enabled").equalsIgnoreCase("true")) {
				p.getLocation().getWorld().playSound(p.getLocation(), Sound.valueOf(getConfig().getString("afk-sounds.return")), 1.0F, 1.0F);
			}
		
			if (getConfig().getString("player-command.enabled").equalsIgnoreCase("true")) {
				
				p.performCommand(String.valueOf(getConfig().getString("player-command.return-command")));
			
			}
		
		}
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if(cmd.getName().equalsIgnoreCase("exafk")) {
			Player player = (Player) sender;
			if (args.length < 1){
   	      sender.sendMessage("§f ");
   	      sender.sendMessage(" §9§lExAFK " + this.getDescription().getVersion() + " §7Developed by Jackerugo");
   	   if (sender.hasPermission("exafk.reload")){
   	      sender.sendMessage(" §c/exafk reload §7- §eReload configuration file");
   	   }
   	      sender.sendMessage("§f ");
			} else {
				if (sender.hasPermission("exafk.reload")){
		  if(args[0].equalsIgnoreCase("reload")) { 
			  reloadConfig();
			  Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes("&".toCharArray()[0], "&9[ExAFK] &7config.yml has been reloaded successfully!"));
			        player.sendTitle("§f ", ChatColor.translateAlternateColorCodes('&', format(getConfig().getString("plugin-messages.config-reloaded"))) , 10, 40, 20);
		  }
		  } else { 
			  player.sendTitle("§f ", ChatColor.translateAlternateColorCodes('&', format(getConfig().getString("plugin-messages.no-permission"))) , 10, 40, 20);
		  }
			}
		}
		return false;
	}
	
	public final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public final char COLOR_CHAR = ChatColor.COLOR_CHAR;

    public String format(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return matcher.appendTail(buffer).toString();
    }
	
}
