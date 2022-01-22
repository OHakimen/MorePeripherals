package com.hakimen.peripherals.utils;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;


import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantUtils {

    static List<Enchantment> Armor = new ArrayList<>();
    static List<Enchantment> Boots = new ArrayList<>();
    static List<Enchantment> Legs = new ArrayList<>();
    static List<Enchantment> Chestplate = new ArrayList<>();
    static List<Enchantment> Helmet = new ArrayList<>();
    static List<Enchantment> Weapon = new ArrayList<>();
    static List<Enchantment> Digable = new ArrayList<>();
    static List<Enchantment> Bow = new ArrayList<>();
    static List<Enchantment> Trident = new ArrayList<>();
    static List<Enchantment> Crossbow = new ArrayList<>();
    static List<Enchantment> Wearable = new ArrayList<>();
    static List<Enchantment> Breakable = new ArrayList<>();
    static List<Enchantment> Vanishable = new ArrayList<>();
    static List<Enchantment> FishingRod = new ArrayList<>();

    public static void init(){
        Registry.ENCHANTMENT.forEach((e)->{
            switch (e.category){
                case ARMOR ->  Armor.add(e);
                case DIGGER -> Digable.add(e);
                case ARMOR_FEET -> Boots.add(e);
                case ARMOR_CHEST -> Chestplate.add(e);
                case ARMOR_HEAD -> Helmet.add(e);
                case ARMOR_LEGS -> Legs.add(e);
                case WEAPON -> Weapon.add(e);
                case BOW -> Bow.add(e);
                case TRIDENT -> Trident.add(e);
                case CROSSBOW -> Crossbow.add(e);
                case WEARABLE -> Wearable.add(e);
                case BREAKABLE -> Breakable.add(e);
                case VANISHABLE -> Vanishable.add(e);
                case FISHING_ROD -> FishingRod.add(e);
            }

        });
    }

    public static void addRandomEnchant(Random r,ItemStack itemStack){
        var item = itemStack.getItem();
        if(item instanceof PickaxeItem || item instanceof AxeItem || item instanceof ShovelItem ||  item instanceof HoeItem){
            enchant(r,itemStack,Digable);
        }
        else if(item instanceof TridentItem){
            enchant(r,itemStack,Trident);
        }
        else if(item instanceof BowItem){
            enchant(r,itemStack,Bow);
        }
        else if(item instanceof CrossbowItem){
            enchant(r,itemStack,Crossbow);
        }
        else if(item instanceof ArmorItem){
            if(((ArmorItem)item).getSlot() == EquipmentSlot.HEAD){
                if (r.nextFloat() <= 0.5f) {
                    enchant(r, itemStack, Helmet);
                } else {
                    enchant(r, itemStack, Armor);
                }
            }else if(((ArmorItem)item).getSlot() == EquipmentSlot.CHEST){
                if (r.nextFloat() <= 0.5f) {
                    enchant(r, itemStack, Chestplate);
                } else {
                    enchant(r, itemStack, Armor);
                }
            }else if(((ArmorItem)item).getSlot() == EquipmentSlot.LEGS){
                if (r.nextFloat() <= 0.5f) {
                    if(Legs.size() > 0){
                        enchant(r, itemStack, Legs);
                    }
                    else {
                        enchant(r, itemStack, Armor);
                    }

                } else {
                    enchant(r, itemStack, Armor);
                }
            }else if(((ArmorItem)item).getSlot() == EquipmentSlot.FEET){
                if (r.nextFloat() <= 0.5f) {
                    enchant(r, itemStack, Boots);
                } else {
                    enchant(r, itemStack, Armor);
                }
            } else{
                enchant(r,itemStack,Armor);
            }
        }
        else if(item instanceof SwordItem){
            enchant(r,itemStack,Weapon);
        }
        else if(item instanceof FishingRodItem){
            enchant(r,itemStack,FishingRod);
        }
        else if(item.canBeDepleted()){
            enchant(r,itemStack,Breakable);
        }
        else if(item instanceof Wearable){
            enchant(r,itemStack,Wearable);
        }
        else if(item instanceof Vanishable ){
            enchant(r,itemStack,Vanishable);
        }

    }

    private static void enchant(Random r,ItemStack item,List<Enchantment> enchantments){
        if(enchantments.size() == 0){
            return;
        }
        var enchant = enchantments.get(r.nextInt(0,enchantments.size()));
        var value = r.nextInt(enchant.getMinLevel(), enchant.getMaxLevel()+1);
        item.enchant(enchant,value);

    }
}
