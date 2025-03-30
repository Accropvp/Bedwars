package org.accropvp.bedwars;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

//import static org.bukkit.Bukkit.getLogger;

public class ItemShop implements Listener {

    // Step 1: Get the main scoreboard
    private final Scoreboard scoreboard;

    // Predefined sets for fast checks
    private static final Set<Material> ARMOR_MATERIALS = EnumSet.of(
            Material.CHAINMAIL_BOOTS,
            Material.IRON_BOOTS,
            Material.DIAMOND_BOOTS
    );

    private static final ItemStack wool16 = MakeWool();
    private static final ItemStack terracotta16 = MakeTerracotta();
    private static final ItemStack glass4 = MakeGlass();
    private static final ItemStack endStone12 = MakeEndStone();
    private static final ItemStack ladder8 = MakeLadder();
    private static final ItemStack wood16 = MakeWood();
    private static final ItemStack obsidian4 = MakeObsidian();
    private static final ItemStack[] blocks = {wool16, terracotta16, glass4, endStone12, ladder8, wood16, obsidian4};

    private final ItemStack stoneSword = MakeStoneSword();
    private final ItemStack ironSword = MakeIronSword();
    private final ItemStack diamondSword = MakeDiamondSword();
    private final ItemStack knockbackStick = MakeKnockbackStick();
    private final ItemStack[] weapons = {stoneSword, ironSword, diamondSword, knockbackStick};

    private final ItemStack chainmailBoots = MakeChainmailBoots();
    private final ItemStack ironBoots = MakeIronBoots();
    private final ItemStack diamondBoots = MakeDiamondBoots();
    private final ItemStack[] armors = {chainmailBoots, ironBoots, diamondBoots};

    public static ItemStack shears = MakeShears();
    public static ItemStack woodenPickaxe = MakeWoodenPickaxe();
    public static ItemStack stonePickaxe = MakeStonePickaxe();
    public static ItemStack ironPickaxe = MakeIronPickaxe();
    public static ItemStack diamondPickaxe = MakeDiamondPickaxe();
    public static ItemStack woodenAxe = MakeWoodenAxe();
    public static ItemStack stoneAxe = MakeStoneAxe();
    public static ItemStack ironAxe = MakeIronAxe();
    public static ItemStack diamondAxe = MakeDiamondAxe();
    public static ItemStack[] tools = {shears, woodenPickaxe, stonePickaxe, ironPickaxe, diamondPickaxe, woodenAxe, stoneAxe, ironAxe, diamondAxe};

    private static final ItemStack arrows6 = MakeArrows();
    private static final ItemStack stock_bow = MakeStockBow();
    private static final ItemStack power_bow = MakePowerBow();
    private static final ItemStack punch_bow = MakePunchBow();
    private static final ItemStack[] bows = {arrows6, stock_bow, power_bow, punch_bow};

    private static final ItemStack[] potions = MakeCustomPotions();

    private static final ItemStack goldenApple = MakeGoldenApple();
    private static final ItemStack bedBug = MakeBedBug();
    private static final ItemStack golemEgg = MakeGolem();
    private static final ItemStack fireball = MakeFireBall();
    private static final ItemStack tnt = MakeTNT();
    private static final ItemStack enderPearl = MakeEnderPeal();
    private static final ItemStack waterBucket = MakeWaterBucket();
    private static final ItemStack bridgeEgg = MakeBridgeEgg();
    private static final ItemStack magicMilk = MakeMagicMilk();
    private static final ItemStack sponge4 = MakeSponge();
    private static final ItemStack[] utility ={goldenApple, bedBug, golemEgg, fireball, tnt, enderPearl, waterBucket, bridgeEgg, magicMilk, sponge4};

    // Manually create ItemStack objects for each item
    private static final ItemStack choiceGreyStainedGlass = new ItemStack(Material.GRAY_STAINED_GLASS,1);
    private static final ItemStack choiceTerracotta = MakeBlockChoice();
    private static final ItemStack choiceGoldenSword = MakeSwordChoice();
    private static final ItemStack choiceChainmailBoots = MakeArmorChoice();
    private static final ItemStack choiceStonePickaxe = MakeToolChoice();
    private static final ItemStack choiceBow = MakeBowChoice();
    private static final ItemStack choiceBrewingStand = MakePotionChoice();
    private static final ItemStack choiceTNT = MakeUtilityChoice();
    private static final ItemStack[] choiceItems = {choiceGreyStainedGlass, choiceTerracotta, choiceGoldenSword, choiceChainmailBoots, choiceStonePickaxe, choiceBow, choiceBrewingStand, choiceTNT, choiceGreyStainedGlass};

    private static final ItemStack greyStainedGlass = new ItemStack(Material.GRAY_STAINED_GLASS,1);
    private static final ItemStack greenStainedGlass = new ItemStack(Material.GREEN_STAINED_GLASS);
    
    Map<ItemStack, ItemStack> simpleItemCost = MakeSimpleItemMarket();
    Map<ItemStack, ItemStack> complexItemCost = MakeComplexItemMarket();

    private static final Component shopTitle = Component.text("Item Shop");


    private static void createItemMeta(ItemStack item, Component title, List<Component> lore) {
        // Get the item meta
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        // Set the display name with a color
        meta.displayName(title);
        // Set the lore with custom colors
        meta.lore(lore);
        // Apply the meta back to the item
        item.setItemMeta(meta);
    }

    private static ItemStack MakeBlockChoice(){
        ItemStack item = new ItemStack(Material.TERRACOTTA);
        Component title = Component.text("Blocks");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Click to view", NamedTextColor.YELLOW)
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeSwordChoice(){
        ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
        Component title = Component.text("Swords");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Click to view", NamedTextColor.YELLOW)
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeArmorChoice(){
        ItemStack item = new ItemStack(Material.CHAINMAIL_BOOTS);
        Component title = Component.text("Armors");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Click to view", NamedTextColor.YELLOW)
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeToolChoice(){
        ItemStack item = new ItemStack(Material.STONE_PICKAXE);
        Component title = Component.text("Tools");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Click to view", NamedTextColor.YELLOW)
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeBowChoice(){
        ItemStack item = new ItemStack(Material.BOW);
        Component title = Component.text("Bows");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Click to view", NamedTextColor.YELLOW)
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakePotionChoice(){
        ItemStack item = new ItemStack(Material.BREWING_STAND);
        Component title = Component.text("Potions");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Click to view", NamedTextColor.YELLOW)
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeUtilityChoice(){
        ItemStack item = new ItemStack(Material.STONE_PICKAXE);
        Component title = Component.text("Utility");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Click to view", NamedTextColor.YELLOW)
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack[] MakeCustomPotions() {
        // Create potions ItemStack
        ItemStack speed = new ItemStack(Material.POTION);
        ItemStack jumpBoost = new ItemStack(Material.POTION);
        ItemStack invisibility = new ItemStack(Material.POTION);

        // Get the PotionMeta to customize the potion
        PotionMeta speedMeta = (PotionMeta) speed.getItemMeta();
        if (speedMeta != null) {
            // Add custom effects (type, duration in ticks, amplifier)
            speedMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 900, 1), true);

            // Optionally, set a custom name or lore
            speedMeta.displayName(Component.text("Speed Potion"));
            // Set the lore using Adventure Components
            speedMeta.lore(List.of(
                    Component.text("Gives Speed 2 for 45 seconds"),
                    Component.text("Cost : ").append(Component.text("1 emerald", NamedTextColor.GREEN))
            ));

            // Apply metadata back to the potion
            speed.setItemMeta(speedMeta);
        }

        // Get the PotionMeta to customize the potion
        PotionMeta jumpBoostMeta = (PotionMeta) jumpBoost.getItemMeta();
        if (jumpBoostMeta != null) {
            // Add custom effects (type, duration in ticks, amplifier)
            jumpBoostMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 900, 4), true);

            // Optionally, set a custom name or lore
            jumpBoostMeta.displayName(Component.text("jumpBoost Potion"));
            // Set the lore using Adventure Components
            jumpBoostMeta.lore(List.of(
                    Component.text("Gives Jump Boost 5 for 45 seconds"),
                    Component.text("Cost : ").append(Component.text("1 emerald", NamedTextColor.GREEN))
            ));

            // Apply metadata back to the potion
            jumpBoost.setItemMeta(jumpBoostMeta);
        }

        // Get the PotionMeta to customize the potion
        PotionMeta invisibilityMeta = (PotionMeta) invisibility.getItemMeta();
        if (invisibilityMeta != null) {
            // Add custom effects (type, duration in ticks, amplifier)
            invisibilityMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 1), true);

            // Optionally, set a custom name or lore
            invisibilityMeta.displayName(Component.text("invisibility Potion"));
            // Set the lore using Adventure Components
            invisibilityMeta.lore(List.of(
                    Component.text("Gives invisibility 2 for 30 seconds"),
                    Component.text("Cost : ").append(Component.text("2 emerald", NamedTextColor.GREEN))
            ));

            // Apply metadata back to the potion
            invisibility.setItemMeta(invisibilityMeta);
        }

        return new ItemStack[]{speed, jumpBoost, invisibility};
    }

    private static ItemStack MakeWool(){
        ItemStack wool = new ItemStack(Material.WHITE_WOOL, 16);
        Component title = Component.text("Wool");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 iron", NamedTextColor.WHITE))));
        createItemMeta(wool, title, lore);
        return wool;
    }

    private static ItemStack MakeTerracotta(){
        ItemStack item = new ItemStack(Material.TERRACOTTA, 16);
        Component title = Component.text("Terracotta");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("12 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeGlass(){
        ItemStack item = new ItemStack(Material.GLASS, 4);
        Component title = Component.text("Glass");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("12 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeEndStone(){
        ItemStack item = new ItemStack(Material.END_STONE, 12);
        Component title = Component.text("End stone");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("24 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeLadder(){
        ItemStack item = new ItemStack(Material.LADDER, 8);
        Component title = Component.text("Ladder");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeWood(){
        ItemStack item = new ItemStack(Material.OAK_PLANKS, 16);
        Component title = Component.text("Wood");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeObsidian(){
        ItemStack item = new ItemStack(Material.OBSIDIAN, 4);
        Component title = Component.text("Obsidian");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 emerald", NamedTextColor.GREEN))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeStoneSword(){
        ItemStack item = new ItemStack(Material.STONE_SWORD);
        Component title = Component.text("Stone sword");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("10 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeIronSword(){
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        Component title = Component.text("Iron sword");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("7 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeDiamondSword(){
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        Component title = Component.text("Diamond sword");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 emerald", NamedTextColor.GREEN))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeChainmailBoots(){
        ItemStack item = new ItemStack(Material.CHAINMAIL_BOOTS);
        Component title = Component.text("Chainmail Boots");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("gives you permanent armor"),
                Component.text("Cost : ").append(Component.text("40 iron", NamedTextColor.WHITE))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeIronBoots(){
        ItemStack item = new ItemStack(Material.IRON_BOOTS);
        Component title = Component.text("Iron Boots");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("gives you permanent armor"),
                Component.text("Cost : ").append(Component.text("12 gold", NamedTextColor.GOLD))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeDiamondBoots(){
        ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
        Component title = Component.text("Diamond Boots");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("gives you permanent armor"),
                Component.text("Cost : ").append(Component.text("6 emerald", NamedTextColor.GREEN))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeShears(){
        ItemStack item = new ItemStack(Material.SHEARS);
        Component title = Component.text("Shears");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item is permanent"),
                Component.text("Cost : ").append(Component.text("20 iron", NamedTextColor.WHITE))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeWoodenPickaxe(){
        ItemStack item = new ItemStack(Material.WOODEN_PICKAXE);
        Component title = Component.text("Wooden Pickaxe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item is permanent"),
                Component.text("Cost : ").append(Component.text("10 iron", NamedTextColor.WHITE))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeStonePickaxe(){
        ItemStack item = new ItemStack(Material.STONE_PICKAXE);
        Component title = Component.text("Stone Pickaxe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item lose 1 level after death"),
                Component.text("Cost : ").append(Component.text("10 iron", NamedTextColor.WHITE))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeIronPickaxe(){
        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        Component title = Component.text("Iron Pickaxe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item lose 1 level after death"),
                Component.text("Cost : ").append(Component.text("3 gold", NamedTextColor.GOLD))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeDiamondPickaxe(){
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        Component title = Component.text("Diamond Pickaxe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item lose 1 level after death"),
                Component.text("Cost : ").append(Component.text("6 gold", NamedTextColor.GOLD))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeWoodenAxe(){
        ItemStack item = new ItemStack(Material.WOODEN_AXE);
        Component title = Component.text("Wooden Axe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item is permanent"),
                Component.text("Cost : ").append(Component.text("10 iron", NamedTextColor.WHITE))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeStoneAxe(){
        ItemStack item = new ItemStack(Material.STONE_AXE);
        Component title = Component.text("Stone Axe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item lose 1 level after death"),
                Component.text("Cost : ").append(Component.text("10 iron", NamedTextColor.WHITE))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeIronAxe(){
        ItemStack item = new ItemStack(Material.IRON_AXE);
        Component title = Component.text("Iron Axe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item lose 1 level after death"),
                Component.text("Cost : ").append(Component.text("3 gold", NamedTextColor.GOLD))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeDiamondAxe(){
        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        Component title = Component.text("Diamond Axe");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("This item lose 1 level after death"),
                Component.text("Cost : ").append(Component.text("6 gold", NamedTextColor.GOLD))
        ));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeKnockbackStick(){
        ItemStack item = new ItemStack(Material.STICK);
        item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        Component title = Component.text("Knockback stick");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("5 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }


    private static ItemStack MakeArrows(){
        ItemStack item = new ItemStack(Material.ARROW, 6);
        Component title = Component.text("Arrows");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("2 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeStockBow(){
        ItemStack item = new ItemStack(Material.BOW);
        Component title = Component.text("Normal bow");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("12 emerald", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakePowerBow(){
        ItemStack item = new ItemStack(Material.BOW);
        item.addEnchantment(Enchantment.POWER, 1);
        Component title = Component.text("Power bow");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("20 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakePunchBow(){
        ItemStack item = new ItemStack(Material.BOW);
        item.addEnchantment(Enchantment.POWER, 1);
        item.addEnchantment(Enchantment.PUNCH, 1);
        Component title = Component.text("Punch bow");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("6 emerald", NamedTextColor.GREEN))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeGoldenApple(){
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        Component title = Component.text("Golden apple");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("3 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeBedBug(){
        ItemStack item = new ItemStack(Material.SNOWBALL);
        Component title = Component.text("Bed Bugs");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("24 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeGolem(){
        ItemStack item = new ItemStack(Material.IRON_GOLEM_SPAWN_EGG);
        Component title = Component.text("Dream Defender");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("120 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeFireBall(){
        ItemStack item = new ItemStack(Material.FIRE_CHARGE);
        Component title = Component.text("Fire ball");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("40 iron", NamedTextColor.WHITE))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeTNT(){
        ItemStack item = new ItemStack(Material.TNT);
        Component title = Component.text("TNT");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeEnderPeal(){
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        Component title = Component.text("Ender Pearl");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 emerald", NamedTextColor.GREEN))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeWaterBucket(){
        ItemStack item = new ItemStack(Material.WATER_BUCKET);
        Component title = Component.text("Water Bucket");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("2 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeBridgeEgg(){
        ItemStack item = new ItemStack(Material.EGG);
        Component title = Component.text("Bridge egg");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("1 emerald", NamedTextColor.GREEN))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeMagicMilk(){
        ItemStack item = new ItemStack(Material.MILK_BUCKET);
        Component title = Component.text("Magic Milk");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private static ItemStack MakeSponge(){
        ItemStack item = new ItemStack(Material.SPONGE);
        Component title = Component.text("Sponge");
        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("2 gold", NamedTextColor.GOLD))));
        createItemMeta(item, title, lore);
        return item;
    }

    private Map<ItemStack, ItemStack> MakeSimpleItemMarket(){
        // create a hashmap of the buy-able item and it's cost
        Map<ItemStack, ItemStack> itemCost = new HashMap<>();

        itemCost.put(terracotta16, new ItemStack(Material.IRON_INGOT, 12));
        itemCost.put(glass4, new ItemStack(Material.IRON_INGOT, 12));
        itemCost.put(endStone12, new ItemStack(Material.IRON_INGOT, 24));
        itemCost.put(ladder8, new ItemStack(Material.IRON_INGOT, 4));
        itemCost.put(wood16, new ItemStack(Material.GOLD_INGOT, 4));
        itemCost.put(obsidian4, new ItemStack(Material.EMERALD, 4));

        itemCost.put(stoneSword, new ItemStack(Material.IRON_INGOT, 10));
        itemCost.put(ironSword, new ItemStack(Material.GOLD_INGOT, 7));
        itemCost.put(diamondSword, new ItemStack(Material.EMERALD, 4));
        itemCost.put(knockbackStick, new ItemStack(Material.GOLD_INGOT, 5));

        itemCost.put(arrows6, new ItemStack(Material.GOLD_INGOT, 2));
        itemCost.put(stock_bow, new ItemStack(Material.GOLD_INGOT, 12));
        itemCost.put(power_bow, new ItemStack(Material.GOLD_INGOT, 20));
        itemCost.put(punch_bow, new ItemStack(Material.EMERALD, 6));

        itemCost.put(potions[0], new ItemStack(Material.EMERALD));
        itemCost.put(potions[1], new ItemStack(Material.EMERALD));
        itemCost.put(potions[2], new ItemStack(Material.EMERALD, 2));

        itemCost.put(goldenApple, new ItemStack(Material.GOLD_INGOT, 3));
        itemCost.put(bedBug, new ItemStack(Material.IRON_INGOT, 24));
        itemCost.put(golemEgg, new ItemStack(Material.IRON_INGOT, 120));
        itemCost.put(fireball, new ItemStack(Material.IRON_INGOT, 40));
        itemCost.put(tnt, new ItemStack(Material.GOLD_INGOT, 4));
        itemCost.put(enderPearl, new ItemStack(Material.EMERALD, 4));
        itemCost.put(waterBucket, new ItemStack(Material.GOLD_INGOT, 2));
        itemCost.put(bridgeEgg, new ItemStack(Material.EMERALD));
        itemCost.put(magicMilk, new ItemStack(Material.GOLD_INGOT, 4));
        itemCost.put(sponge4, new ItemStack(Material.GOLD_INGOT, 2));

        return itemCost;
    }

    private Map<ItemStack, ItemStack> MakeComplexItemMarket(){
        // create a hashmap of the buy-able item and it's cost
        Map<ItemStack, ItemStack> itemCost = new HashMap<>();

        itemCost.put(wool16, new ItemStack(Material.IRON_INGOT, 4));

        itemCost.put(chainmailBoots, new ItemStack(Material.IRON_INGOT, 40));
        itemCost.put(ironBoots, new ItemStack(Material.GOLD_INGOT,12));
        itemCost.put(diamondBoots, new ItemStack(Material.EMERALD, 6));

        itemCost.put(shears, new ItemStack(Material.IRON_INGOT, 20));
        itemCost.put(woodenPickaxe, new ItemStack(Material.IRON_INGOT, 10));
        itemCost.put(stonePickaxe, new ItemStack(Material.IRON_INGOT, 10));
        itemCost.put(ironPickaxe, new ItemStack(Material.GOLD_INGOT, 3));
        itemCost.put(diamondPickaxe, new ItemStack(Material.GOLD_INGOT, 6));
        itemCost.put(woodenAxe, new ItemStack(Material.IRON_INGOT, 10));
        itemCost.put(stoneAxe, new ItemStack(Material.IRON_INGOT, 10));
        itemCost.put(ironAxe, new ItemStack(Material.GOLD_INGOT, 3));
        itemCost.put(diamondAxe, new ItemStack(Material.GOLD_INGOT, 6));

        return itemCost;
    }

    public ItemShop(){
        // Initialize the scoreboard safely during onEnable()
        Bukkit.getScoreboardManager();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    private void setTools(Inventory players_inventory, Inventory player_item_shop){
        player_item_shop.setItem(4+9, greenStainedGlass);
        player_item_shop.setItem(19, shears);

        if(players_inventory.containsAtLeast(diamondPickaxe, 1)){
            player_item_shop.setItem(20, diamondPickaxe);
        } else if (players_inventory.containsAtLeast(ironPickaxe, 1)){
            player_item_shop.setItem(20, diamondPickaxe);
        } else if (players_inventory.containsAtLeast(stonePickaxe, 1)){
            player_item_shop.setItem(20, ironPickaxe);
        } else if (players_inventory.containsAtLeast(woodenPickaxe, 1)) {
            player_item_shop.setItem(20,stonePickaxe);
        } else {
            player_item_shop.setItem(20, woodenPickaxe);
        }

        if(players_inventory.containsAtLeast(diamondAxe, 1)){
            player_item_shop.setItem(21, diamondAxe);
        } else if (players_inventory.containsAtLeast(ironAxe, 1)) {
            player_item_shop.setItem(21, diamondAxe);
        } else if (players_inventory.containsAtLeast(stoneAxe, 1)) {
            player_item_shop.setItem(21, ironAxe);
        } else if (players_inventory.containsAtLeast(woodenAxe, 1)) {
            player_item_shop.setItem(21, stoneAxe);
        } else {
            player_item_shop.setItem(21, woodenAxe);
        }
    }

    private void WoolColor(Player player, Inventory players_inventory, ItemStack currentItem, ItemStack complexCost){
        if (currentItem.getType() != Material.WHITE_WOOL){
            return;
        }
        Team team = scoreboard.getEntityTeam(player);
        assert team != null;
        String teamName = team.getName();
        ItemStack wool = switch (teamName) {
            case "RED" -> new ItemStack(Material.RED_WOOL, 16);
            //case "BLACK" -> new ItemStack(Material.BLACK_WOOL, 16);
            //case "BROWN" -> new ItemStack(Material.BROWN_WOOL, 16);
            case "BLUE" -> new ItemStack(Material.BLUE_WOOL, 16);
            case "CYAN" -> new ItemStack(Material.CYAN_WOOL, 16);
            case "GRAY" -> new ItemStack(Material.GRAY_WOOL, 16);
            case "GREEN" -> new ItemStack(Material.GREEN_WOOL, 16);
            //case "LIME" -> new ItemStack(Material.LIME_WOOL, 16);
            //case "MAGENTA" -> new ItemStack(Material.MAGENTA_WOOL, 16);
            //case "ORANGE" -> new ItemStack(Material.ORANGE_WOOL, 16);
            //case "PURPLE" -> new ItemStack(Material.PURPLE_WOOL, 16);
            case "PINK" -> new ItemStack(Material.PINK_WOOL, 16);
            case "YELLOW" -> new ItemStack(Material.YELLOW_WOOL, 16);
            //case "LIGHT_BLUE" -> new ItemStack(Material.LIGHT_BLUE_WOOL, 16);
            //case "LIGHT_GRAY" -> new ItemStack(Material.LIGHT_GRAY_WOOL, 16);
            default -> new ItemStack(Material.WHITE_WOOL, 16);
        };

        if (players_inventory.containsAtLeast(complexCost, complexCost.getAmount())){
            players_inventory.removeItem(complexCost);
            players_inventory.addItem(wool);
        } else {
            player.sendMessage("§c" + "not enough " + complexCost.getType());
        }
    }

    private void armorMarket(Player player, Inventory players_inventory,  ItemStack currentItem, ItemStack complexCost){
        Material type = currentItem.getType();
        Material bootType = null;
        if (!ARMOR_MATERIALS.contains(type)){
            return;
        }

        if (player.getEquipment().getBoots() != null){
            bootType = player.getEquipment().getBoots().getType();
        }

        switch (type){
            case Material.CHAINMAIL_BOOTS:
                if (bootType == Material.CHAINMAIL_BOOTS || bootType == Material.IRON_BOOTS || bootType == Material.DIAMOND_BOOTS){
                    player.sendMessage("§c" + "you already have the same or better armor");
                    return;
                }
                if (players_inventory.containsAtLeast(complexCost, complexCost.getAmount())){
                    players_inventory.removeItem(complexCost);
                    // Equip the player with the chainmail
                    player.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
                    player.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                } else {
                    player.sendMessage("§c" + "not enough " + complexCost.getType());
                }
                break;
            case Material.IRON_BOOTS:
                if (bootType == Material.IRON_BOOTS || bootType == Material.DIAMOND_BOOTS){
                    player.sendMessage("§c" + "you already have the same or better armor");
                    return;
                }
                if (players_inventory.containsAtLeast(complexCost, complexCost.getAmount())){
                    players_inventory.removeItem(complexCost);
                    // Equip the player with the iron armor
                    player.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
                    player.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                } else {
                    player.sendMessage("§c" + "not enough " + complexCost.getType());
                }
                break;
            case Material.DIAMOND_BOOTS:
                if (bootType == Material.DIAMOND_BOOTS){
                    player.sendMessage("§c" + "you already have the same armor");
                    return;
                }
                if (players_inventory.containsAtLeast(complexCost, complexCost.getAmount())){
                    players_inventory.removeItem(complexCost);
                    // Equip the player with the diamond armor
                    player.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    player.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                } else {
                    player.sendMessage("§c" + "not enough " + complexCost.getType());
                }
                break;
            default:
                break;
        }
        TeamUpgrade.setProtection(player);
    }

    private void toolMarket(Player player, Inventory players_inventory, Inventory player_item_shop, ItemStack currentItem, ItemStack complexCost){
        int toolIndex = ArrayUtils.indexOf(tools, currentItem);
        if (toolIndex == -1){
            return;
        }
        if(players_inventory.containsAtLeast(currentItem, 1)){
            player.sendMessage("§c" + "you already have the max upgrade");
            return;
        }
        if (toolIndex == 0 || toolIndex == 1 || toolIndex == 5){
            if (players_inventory.containsAtLeast(complexCost, complexCost.getAmount())){
                players_inventory.removeItem(complexCost);
                players_inventory.addItem(currentItem);
                setTools(players_inventory, player_item_shop);
            } else {
                player.sendMessage("§c" + "not enough " + complexCost.getType());
            }
            return;
        }

        if (players_inventory.containsAtLeast(complexCost, complexCost.getAmount())){
            players_inventory.removeItem(tools[toolIndex - 1]);
            players_inventory.removeItem(complexCost);
            players_inventory.addItem(currentItem);
            setTools(players_inventory, player_item_shop);
        } else {
            player.sendMessage("§c" + "not enough " + complexCost.getType());
        }
    }

    private void buyItem(InventoryClickEvent event, Player player, Inventory players_inventory, Inventory player_item_shop){
        if (event.getCurrentItem() == null) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem().clone();

        ItemStack simpleCost = simpleItemCost.get(currentItem);
        ItemStack complexCost = complexItemCost.get(currentItem);

        if (simpleCost != null) {
            if (players_inventory.containsAtLeast(simpleCost, simpleCost.getAmount())){
                players_inventory.removeItem(simpleCost);
                //ItemStack resultItem = new ItemStack(currentItem.getType(), currentItem.getAmount());
                //ItemMeta resultItemMeta = resultItem.getItemMeta();
                //resultItemMeta.displayName(currentItem.displayName());
                //resultItem.setItemMeta(resultItemMeta);
                //players_inventory.addItem(resultItem);
                players_inventory.addItem(currentItem);
                if (TeamUpgrade.SWORDS_MATERIALS.contains(currentItem.getType())){
                    TeamUpgrade.setSharpness(player);
                }
            } else {
                player.sendMessage("§c" + "not enough " + simpleCost.getType());
            }
        }
        else if (complexCost != null){
            WoolColor(player, players_inventory, currentItem, complexCost);
            armorMarket(player, players_inventory, currentItem, complexCost);
            toolMarket(player, players_inventory, player_item_shop, currentItem, complexCost);
        }
    }

    public void refreshItem(Inventory inventory){
        for (int i = 9; i < inventory.getSize(); i++) {
            inventory.setItem(i, null);
        }

        for (int i = 9; i < 18; i++) {
            inventory.setItem(i, greyStainedGlass);
        }
    }

    private void menu(InventoryClickEvent event,Player player){

        Inventory players_inventory = player.getInventory();
        Inventory player_item_shop = event.getClickedInventory();

        // Check if the clicked inventory is null or not the top inventory
        if (!event.getView().title().equals(Component.text("Item Shop")) || player_item_shop != event.getView().getTopInventory()) {
            return; // Ignore clicks in the player's inventory or other inventories
        }

        if (event.getRawSlot() < 9){
            refreshItem(player_item_shop);
        }

        switch (event.getRawSlot()){
            case 1:
                player_item_shop.setItem(1+9, greenStainedGlass);
                for (int i = 0; i < blocks.length; i++) {
                    player_item_shop.setItem(i + 19, blocks[i]);
                }
                break;

            case 2:
                player_item_shop.setItem(2+9, greenStainedGlass);
                ItemStack[] weapons1 = weapons.clone();
                for (int i = 0; i < 4; i++) player_item_shop.setItem(i + 19, weapons1[i]);
                break;

            case 3:
                player_item_shop.setItem(3+9, greenStainedGlass);
                ItemStack[] armors1 = armors.clone();
                for (int i = 0; i < armors1.length; i++) {
                    player_item_shop.setItem(i + 19, armors1[i]);
                }
                break;

            case 4:
                setTools(players_inventory, player_item_shop);
                break;

            case 5:
                player_item_shop.setItem(5+9, greenStainedGlass);
                for (int i = 0; i < bows.length; i++) {
                    player_item_shop.setItem(i + 19, bows[i]);
                }
                break;

            case 6:
                player_item_shop.setItem(6+9, greenStainedGlass);
                for (int i = 0; i < potions.length; i++) {
                    player_item_shop.setItem(i + 19, potions[i]);
                }
                break;

            case 7:
                player_item_shop.setItem(7+9, greenStainedGlass);
                for (int i = 0; i <= utility.length / 7; i++) {
                    for (int j = 0; j < utility.length - (i*7) && j < 7; j++) {
                        player_item_shop.setItem((2+i) * 9 + j + 1, utility[(i*7) + j]);
                    }
                }
            default:
                break;
        }

        if (event.getRawSlot() > 18){
            buyItem(event, player, players_inventory, player_item_shop);
        }
        event.setCancelled(true);
    }

    public static boolean isItemShop(Villager villager) {
        // Ensure customName() is not null
        if (villager.customName() == null) return false;

        // Serialize the Component to plain text
        String villagerName = PlainTextComponentSerializer.plainText().serialize(villager.customName());

        // Serialize the shopTitle to plain text
        String shopTitleText = PlainTextComponentSerializer.plainText().serialize(shopTitle);

        // Check if the villager's name contains the shopTitle
        return villagerName.contains(shopTitleText);
    }

    private void CreateInventory(Player player, Entity entity){
        if (!(entity instanceof Villager villager)){
            return;
        }
        if (!isItemShop(villager)) {
            return;
        }
        // add items to chestInventory as needed
        Inventory player_item_shop = Bukkit.createInventory(null, 54, shopTitle);
        player.openInventory(player_item_shop);
        for (int i = 0; i < choiceItems.length; i++) {
            player_item_shop.setItem( i, choiceItems[i]);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        CreateInventory(player, entity);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if the clicked inventory is the Item Shop
        if (event.getView().title().equals(shopTitle)) {
            // event.setCancelled(true); // Cancel interactions in the Item Shop
            // Handle shop logic here (e.g., buying items)

            // Ensure the player is interacting
            if (!(event.getWhoClicked() instanceof Player player)) return;
            // Get the player
            ItemStack clickedItem = event.getCurrentItem(); // Get the clicked item
            // Ensure the clicked item is not null or air
            if (clickedItem == null || clickedItem.getType().isAir()) return;

            menu(event, player);
        }
    }
    
}
