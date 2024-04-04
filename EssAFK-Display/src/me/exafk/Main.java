package me.exafk;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		System.out.println("Thank you for using ExAFK!");
	}
	
	public void RemoveAFKEffect(PlayerJoinEvent e) {
		String effect = getConfig().getString("afk-effect.effect");
		PotionEffect afkEffect = e.getPlayer().getPotionEffect(PotionEffectType.getByName(effect));
		if ( afkEffect != null ) {
			e.getPlayer().removePotionEffect(PotionEffectType.getByName(effect));
		}
	}

	@EventHandler
	public void PlayerGoesAFK(AfkStatusChangeEvent e) {
		@SuppressWarnings("deprecation")
		Player p = e.getAffected().getBase();
		
		Integer getTitleDuration = getConfig().getInt("afk-duration.title");
		Integer titleDuration = (getTitleDuration) * 20;
		
		Integer getTitleFadein = getConfig().getInt("title-settings.fade-in");
		Integer titleFadein = (getTitleFadein) * 20;
		
		Integer getTitleFadeout = getConfig().getInt("title-settings.fade-out");
		Integer titleFadeout = (getTitleFadeout) * 20;
		
		if (e.getValue()) {
			
			Random goneAFKTitle = new Random();
		    List<String> listTitle = PlaceholderAPI.setPlaceholders(p, getConfig().getStringList("afk-messages.titles"));
		    int randomMessageTitle = goneAFKTitle.nextInt(listTitle.size());
		    String newMessageTitle = (String)PlaceholderAPI.setPlaceholders(p, listTitle.get(randomMessageTitle));
			
			Random goneAFK = new Random();
		    List<String> listSub = PlaceholderAPI.setPlaceholders(p, getConfig().getStringList("afk-messages.subtitles"));
		    int randomMessage = goneAFK.nextInt(listSub.size());
		    String newMessage = (String)PlaceholderAPI.setPlaceholders(p, listSub.get(randomMessage));
		    
			p.sendTitle(ChatColor.translateAlternateColorCodes('&', format(newMessageTitle)), ChatColor.translateAlternateColorCodes('&', format(newMessage)) , titleFadein, titleDuration, titleFadeout);
			
			Integer getEffectDuration = getConfig().getInt("afk-duration.effect");
			Integer effectDuration = (getEffectDuration) * 20;
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 1));
			
			if (getConfig().getString("player-command.enabled").equalsIgnoreCase("true")) {
				
				p.performCommand(String.valueOf(getConfig().getString("player-command.initial-command")));
			
			}
			
		} else {
			
			Random welcomeBackTitle = new Random();
			List<String> wbListTitle = PlaceholderAPI.setPlaceholders(p, getConfig().getStringList("return-messages.titles"));
		    int wbTitleRandomMessage = welcomeBackTitle.nextInt(wbListTitle.size());
		    String wbTitleNewMessage = (String)PlaceholderAPI.setPlaceholders(p, wbListTitle.get(wbTitleRandomMessage));
			
			Random welcomeBack = new Random();
			List<String> wbListSub = PlaceholderAPI.setPlaceholders(p, getConfig().getStringList("return-messages.subtitles"));
		    int wbRandomMessage = welcomeBack.nextInt(wbListSub.size());
		    String wbNewMessage = (String)PlaceholderAPI.setPlaceholders(p, wbListSub.get(wbRandomMessage));
		    
		    Integer getReturnTitleDuration = getConfig().getInt("title-settings.return-stay");
			Integer titleReturnDuration = (getReturnTitleDuration) * 20;
		    
			p.sendTitle(ChatColor.translateAlternateColorCodes('&', format(wbTitleNewMessage)), ChatColor.translateAlternateColorCodes('&', format(wbNewMessage)) , titleFadein, titleReturnDuration, titleFadeout);
			
			String effect = getConfig().getString("afk-effect.effect");
			p.removePotionEffect(PotionEffectType.getByName(effect));
		
			if (getConfig().getString("player-command.enabled").equalsIgnoreCase("true")) {
				
				p.performCommand(String.valueOf(getConfig().getString("player-command.return-command")));
			
			}
		
		}
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if(cmd.getName().equalsIgnoreCase("exafk")) {
			if (args.length < 1){
	      sender.sendMessage("§8§l§m====================================");
   	      sender.sendMessage("§f ");
   	      sender.sendMessage(" §9§lExAFK v1.0.3 §7Developed by Jackerugo");
   	   if (sender.hasPermission("exafk.reload")){
   	      sender.sendMessage(" §c/exafk reload §7- §eReload configuration file");
   	   }
   	      sender.sendMessage("§f ");
   	      sender.sendMessage("§8§l§m====================================");
			} else {
				if (sender.hasPermission("exafk.reload")){
		  if(args[0].equalsIgnoreCase("reload")) { 
			  reloadConfig();
			  getLogger().log(Level.SEVERE, "config.yml saved and reloaded successfully!");
			        Player player = (Player) sender;
			        player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&f "), ChatColor.translateAlternateColorCodes('&', format(getConfig().getString("plugin-messages.config-reloaded"))) , 10, 40, 20);
		  }
		  } else {
			  Player player = (Player) sender;  
			  player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&f "), ChatColor.translateAlternateColorCodes('&', format(getConfig().getString("plugin-messages.no-permission"))) , 10, 40, 20);
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
