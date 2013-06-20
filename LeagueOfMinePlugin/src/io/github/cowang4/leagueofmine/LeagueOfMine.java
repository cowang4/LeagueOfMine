/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.cowang4.leagueofmine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Greg
 */
public class LeagueOfMine extends JavaPlugin{

    public boolean isLOMEnabled = false;
    public LOM_Locations locations;
    public HashMap<String, LOM_Player> activePlayers = new HashMap<>();
    public LOM_Listener listener;
    public int xspawn, yspawn, zspawn;
    Location serverSpawn;
    
    @Override
    public void onEnable()
    {   
        serverSpawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        xspawn = serverSpawn.getBlockX(); yspawn = serverSpawn.getBlockY(); zspawn = serverSpawn.getBlockZ();
        listener = new LOM_Listener();
        listener.setLOM(this);
        locations = new LOM_Locations();
        getLogger().info("LeagueOfMine has been Enabled!");
        isLOMEnabled = false;
        
        Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        
        
    }
    
            
    @Override
    public void onDisable()
    {
        //Bukkit.getServer().getWorld("world").setSpawnLocation(xspawn, yspawn, zspawn); doesn't work
        
        Collection players = activePlayers.values();
        Iterator it = players.iterator();
        while(it.hasNext())
        {
            LOM_Player player = (LOM_Player) it.next();
            playerLeaveGameArena(player.name);
            playerLeaveLobby(player.name);
        }
        
        isLOMEnabled = false;
        getLogger().info("LeagueOfMine has been Disabled! :(");
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("lom"))
        {
            if(args.length > 1 || args.length < 1){//TODO: Update this argument number...
                sender.sendMessage("Incorrect amount of arguments!");
                return true;
            }

            if(args[0].equalsIgnoreCase("enable"))
            {
                isLOMEnabled = true;
                Bukkit.broadcastMessage("[" + ChatColor.DARK_GREEN + "LOM" + ChatColor.RESET + "]" + ChatColor.GOLD + "LeagueOfMine has been enabled! Normal gameplay is probs fucked up...");
                //Other stuff probs .... maybes not
                return true;
            }
            
            if(args[0].equalsIgnoreCase("disable"))
            {
                isLOMEnabled = false;
                Bukkit.broadcastMessage("[" + ChatColor.DARK_GREEN + "LOM" + ChatColor.RESET + "]" + ChatColor.GOLD + "LeagueOfMine has been disabled! Normal gameplay is probs less fucked up...");
                return true;
            }
            
            if(args[0].equalsIgnoreCase("join"))
            {
                if (isLOMEnabled){
                    
                    if(sender instanceof Player)
                    {
                        LOM_Player lomPlayer = new LOM_Player();
                        Player player = Bukkit.getServer().getPlayer(sender.getName());
                        lomPlayer.pre_game_loc = player.getLocation();
                        lomPlayer.pre_game_inventory = player.getInventory();
                        lomPlayer.pre_game_items = player.getInventory().getContents();
                        lomPlayer.name = player.getName();
                        lomPlayer.pre_game_gamemode = player.getGameMode();
                        player.sendMessage("[" + ChatColor.DARK_GREEN + "LOM" + ChatColor.RESET + "]" + ChatColor.GOLD + "Teleporting you to the Lobby...");
                        player.teleport(locations.LOM_LobbyCordinates);

                        activePlayers.put(player.getName(), lomPlayer);//if already was in list, overwrites with new data

                        
                        return true;
                    }
                    else
                    {
                        sender.sendMessage("You can't join a game from the console, silly!");
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("LeagueOfMinecraft isnt enabled. So you cannot join a game. (enable with /lom enable)");
                    return true;
                }
            }
            
            if (args[0].equalsIgnoreCase("leave"))
            {
                if (isLOMEnabled && sender instanceof Player && activePlayers.containsKey(sender.getName()) && activePlayers.get(sender.getName()).isInGameArena)
                {   
                    if(playerLeaveGameArena(sender.getName()))
                        return true;
                    else
                        return false;
                }
                else if(isLOMEnabled && sender instanceof Player && activePlayers.containsKey(sender.getName()) && !activePlayers.get(sender.getName()).isInGameArena)
                {
                    if(playerLeaveLobby(sender.getName()))
                        return true;
                    else
                        return false;
                }
                else
                {
                    sender.sendMessage("NO. Not Cool.");
                    return true;
                }
            }
            
            if(args[0].equalsIgnoreCase("joinblue"))
            {
                playerJoinTeam(true, sender);
                return true;
            }
            
            if(args[0].equalsIgnoreCase("joinred"))
            {
                playerJoinTeam(false, sender);
                return true;
            }
            
            if(args[0].equalsIgnoreCase("start"))
            {
                if(isLOMEnabled && !activePlayers.isEmpty())
                {
                    boolean starting = true;
                    manipulateGates(starting);//no magic booleans!!!
                    Bukkit.getServer().getWorld("world").setGameRuleValue("keepInventory", "true");
                }
                else{sender.sendMessage("Need to enable LOM or join a team.");}
                return true;
            }
          
        }    
        
        if(cmd.getName().equalsIgnoreCase("setserverspawn"))
        {
            if(args.length > 0 || args.length < 0)
            {
                sender.sendMessage("No arguments man");
                return false;
            }
            
            if(!(sender instanceof Player))
            {
                sender.sendMessage("You cant use this command from the console.");
                return false;
            }
            
            Player player = Bukkit.getPlayer(sender.getName());
            Location playerLoc = player.getLocation();
            Bukkit.getServer().getWorld("world").setSpawnLocation(playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());
            xspawn = playerLoc.getBlockX(); yspawn = playerLoc.getBlockY(); zspawn = playerLoc.getBlockZ();
            player.sendMessage("[" + ChatColor.DARK_GREEN + "LOM" + ChatColor.RESET + "]" + ChatColor.GOLD + "Changed Server's Spawn Point");
            return true;
        }
        return false;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
 
    }
    
    public boolean playerLeaveLobby(String playerName)
    {
        Player player = Bukkit.getServer().getPlayer(playerName);
        if(Bukkit.getServer().getPlayer(playerName).isOnline() && activePlayers.containsKey(playerName))
        {
            
            LOM_Player lomPlayer = activePlayers.get(playerName);
            player.sendMessage("[" + ChatColor.DARK_GREEN + "LOM" + ChatColor.RESET + "]" + ChatColor.GOLD + "Teleporting You back to previous location...");
            player.teleport(lomPlayer.pre_game_loc);
            activePlayers.remove(playerName);
            
            return true;
        }
        else
        {
            player.sendMessage("You need to be in a game or something");
            return false;
        }
    }
    public boolean playerLeaveGameArena(String playerName)
    {
        Player player = Bukkit.getServer().getPlayer(playerName);
        if (activePlayers.containsKey(playerName))
        {
            player.sendMessage("[" + ChatColor.DARK_GREEN + "LOM" + ChatColor.RESET + "]" + ChatColor.GOLD + "Teleporting you back to the lobby...");
            player.teleport(locations.LOM_LobbyCordinates);
            activePlayers.get(playerName).isInGameArena = false;
            ItemStack[] iss = activePlayers.get(playerName).pre_game_inventory.getContents();
            player.getInventory().clear();
            player.getInventory().setContents(iss);
            player.getInventory().setContents(activePlayers.get(player.getName()).pre_game_items);
            player.setGameMode(activePlayers.get(player.getName()).pre_game_gamemode);
            
            if(activePlayers.isEmpty())
            {
                gameCleanup();
            }
            
            return true;
        }
        else
        {
            player.sendMessage("You need to be in a game or something");
            return false;
        }
        
    }
    
    public void manipulateGates(boolean openGates)
    {
        if (!activePlayers.isEmpty() && isLOMEnabled)
        {
            World world = Bukkit.getWorld("world");
            Block redGateBlock1 = world.getBlockAt(locations.LOM_RedGate1);
            Block redGateBlock2 = world.getBlockAt(locations.LOM_RedGate2);
            Block redGateBlock3 = world.getBlockAt(locations.LOM_RedGate3);
            Block blueGateBlock1 = world.getBlockAt(locations.LOM_BlueGate1);
            Block blueGateBlock2 = world.getBlockAt(locations.LOM_BlueGate2);
            Block blueGateBlock3 = world.getBlockAt(locations.LOM_BlueGate3);
            
            if (openGates)//opens the gates
            {
                redGateBlock1.setTypeId(0);
                redGateBlock2.setTypeId(0);
                redGateBlock3.setTypeId(0);
                blueGateBlock1.setTypeId(0);
                blueGateBlock2.setTypeId(0);
                blueGateBlock3.setTypeId(0);
                
            }
            else//closes the gates
            {
                redGateBlock1.setTypeId(85);
                redGateBlock2.setTypeId(85);
                redGateBlock3.setTypeId(85);
                blueGateBlock1.setTypeId(85);
                blueGateBlock2.setTypeId(85);
                blueGateBlock3.setTypeId(85);
            }
        }
    }
    
    public void playerJoinTeam(boolean blueIsTrue, CommandSender sender)
    {
        if (isLOMEnabled){

                        if(sender instanceof Player && activePlayers.containsKey(sender.getName()))
                        {
                            sender.sendMessage("[" + ChatColor.DARK_GREEN + "LOM" + ChatColor.RESET + "]" + ChatColor.GOLD + "Teleporting you to Blue team spawnpoint...");
                            activePlayers.get(sender.getName()).isInGameArena = true;
                            activePlayers.get(sender.getName()).bounty = 10;
                            Player player = Bukkit.getServer().getPlayer(sender.getName());
                            player.getInventory().clear();
                            player.setGameMode(GameMode.ADVENTURE);
                            
                            if(blueIsTrue)
                            {
                                activePlayers.get(sender.getName()).isOnBlueTeam = true;
                                player.teleport(locations.LOM_BlueTeamSpawnPoint);
                            }
                            else
                            {
                                activePlayers.get(sender.getName()).isOnBlueTeam = false;
                                player.teleport(locations.LOM_RedTeamSpawnPoint);
                            }
                            
                            return;
                        }
                        else
                        {
                            sender.sendMessage("NAHHHH!");
                            return;
                        }
                    }
                    else
                    {
                        sender.sendMessage("LeagueOfMinecraft isnt enabled. So you cannot join a game. (enable with /lom enable)");
                        return;
                    }

    }
    
    public void gameCleanup()
    {
        manipulateGates(false);//close
        Bukkit.getServer().getWorld("world").setGameRuleValue("keepInventory", "false");
    }
    
}
