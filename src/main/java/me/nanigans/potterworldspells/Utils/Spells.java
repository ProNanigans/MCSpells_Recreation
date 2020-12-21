package me.nanigans.potterworldspells.Utils;

import me.nanigans.potterworldspells.Utils.Config.JsonPaths;
import me.nanigans.potterworldspells.Utils.Config.JsonUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public enum Spells {

    ANTI_APPARATE("Anti Apparate", 2, "CrowdControl", 20, 20D),//name, custom model data
    ACCIO("Accio", 3, "CrowdControl", 0, 12D),
    CONFUNDUS("Confundus", 30, "CrowdControl", 0, 23D),
    DEPULSO("Depulso", 149, "CrowdControl", 0, 5D),
    EXPELLIARMUS("Expelliarmus", 51, "CrowdControl", 0, 17D),
    FLIPENDO("Flipendo", 68, "CrowdControl", 0, 2D),
    IMMOBULUS("Immobulus", 73, "CrowdControl", 0, 20D),
    INCARCERUM("Incarcerum", 76, "CrowdControl", 0, 22D),
    APPARATE("Apparate", 9, "Mobility", 0, 20D),
    ASCENDIO("Ascendio", 14, "Mobility", 0, 17D),
    TRIPUDIO("Tripudo", 181, "Mobility", 0, 6D),
    VOLO("Volo", 182, "Mobility", 0, 6D),
    EPISKEY("Episkey", 55, "Healing", 0, 15D),
    FERUVIO("Feruvio", 141, "Healing", 0, 16D),
    VULNERA("Vulnera", 140, "Healing", 0, 40D),
    ARRESTO_MOMENTUM("Arresto Momentum", 15, "Defensive", 0, 5D),
    DISAPPEARUS("Disappearus", 43, "Defensive", 0, 20D),
    FORTIFICUS("Fortificus", 183, "Defensive", 0, 12D),
    FINITE_INCANTATEM("Finite Incantatem", 60, "Defensive", 0, 20D),
    FLAME_FREEZE("Flame Freeze", 56, "Defensive", 0, 5D),
    PROTEGO("Protego", 112, "Defensive", 0, 0D),
    AGUAMENTI("Aguamenti", 4, "Combat", 0, 0D);
    String name, spellType;
    int data;
    int cost;
    double cooldown;
    private final JsonUtils json = new JsonUtils();


    Spells(String name, int modelData, String spellType, int manaCost, double cooldown) {
        this.name = name;
        this.data = modelData;
        this.spellType = spellType;
        this.cooldown = cooldown;
        this.cost = manaCost;
    }

    public ItemStack toItemStack(){

        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(this.name);
        itemMeta.setCustomModelData(this.data);
        item.setItemMeta(itemMeta);
        ItemUtils.setData(ItemUtils.setData(item, Data.SPELLNAME.toString(), Data.SPELLNAME.getType(), this.getName()),
                Data.SPELLTYPE.toString(), Data.SPELLTYPE.getType(), this.spellType);
        ItemUtils.setData(item, Data.SPELLVALUE.toString(), Data.SPELLVALUE.getType(), this.toString());
        return item;
    }

    public double getCooldown() {
        try {
            final Object data = json.getData(JsonPaths.getSpell(this.getName()) + "." + JsonPaths.COOLDOWN);
            if (data != null) {
                return (double) data;
            }
        }catch (IOException | ParseException ignored){
            return this.cooldown;
        }
        return this.cooldown;
    }

    public int getCost(){return this.cost;}

    public String getSpellType() {
        return spellType;
    }

    public String getName() {
        return name;
    }

    public int getData() {
        return data;
    }
}
