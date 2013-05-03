/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.cowang4.leagueofmine;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author Greg
 */
public class LOM_Listener implements Listener{
    
    LeagueOfMine lomineMainClass;
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTakeDamage(EntityDamageEvent event)
    {
        lomineMainClass.getLogger().info("Found Damage on Entity: " + event.getEntity().toString());
        lomineMainClass.getLogger().info("Coming from: " + event.getCause().name());
        lomineMainClass.getLogger().info("Coming from: " + event.getCause().toString());
        //i give up for now
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        lomineMainClass.getLogger().info(player.getName());
        
        if (lomineMainClass.isPlayingLOM && lomineMainClass.activePlayers.containsKey(event.getEntity().getName()) && lomineMainClass.activePlayers.get(player.getName()).isInGameArena)
        {
            lomineMainClass.getLogger().info("GOOD TO GO!");
            if(lomineMainClass.activePlayers.get(player.getName()).isOnBlueTeam)
            {
                lomineMainClass.getLogger().info("GOOD TO GO! BLUEEEEE");
                Bukkit.getServer().getWorld("world").setSpawnLocation(lomineMainClass.locations.LOM_BlueTeamSpawnPoint.getBlockX(), lomineMainClass.locations.LOM_BlueTeamSpawnPoint.getBlockY(), lomineMainClass.locations.LOM_BlueTeamSpawnPoint.getBlockZ()); 
            }
            else if (!lomineMainClass.activePlayers.get(player.getName()).isOnBlueTeam)
            {
                lomineMainClass.getLogger().info("GOOD TO GO! REDDDDDD");
                Bukkit.getServer().getWorld("world").setSpawnLocation(lomineMainClass.locations.LOM_RedTeamSpawnPoint.getBlockX(), lomineMainClass.locations.LOM_RedTeamSpawnPoint.getBlockY(), lomineMainClass.locations.LOM_RedTeamSpawnPoint.getBlockZ()); 
            }
        }
        else
        {
             Bukkit.getServer().getWorld("world").setSpawnLocation(lomineMainClass.xspawn, lomineMainClass.yspawn, lomineMainClass.zspawn);
        }
    }
    
    public void setLOM(LeagueOfMine lom)
    {
        lomineMainClass = lom;
    }
    
}
