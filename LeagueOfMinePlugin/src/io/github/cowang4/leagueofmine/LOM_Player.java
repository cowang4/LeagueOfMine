/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.cowang4.leagueofmine;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author Greg
 */
public class LOM_Player {
    public String name;
    public Location pre_game_loc;
    public PlayerInventory pre_game_inventory;
    public boolean isOnBlueTeam;
    public boolean isInGameArena;
    public int bounty;
    public ItemStack[] pre_game_items;
    public GameMode pre_game_gamemode;
}
