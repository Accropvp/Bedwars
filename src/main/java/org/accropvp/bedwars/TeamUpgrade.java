package org.accropvp.bedwars;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.lang.Math;

import static org.bukkit.Bukkit.getLogger;

public class TeamUpgrade implements Listener {

    static Scoreboard scoreboard;
    private final PersistentStorageHandler storage;
    private final Bedwars bedwars;
    private static final World overworld = Bukkit.getWorlds().getFirst();

    private final NamespacedKey key = NamespacedKey.minecraft("custom_tag");
    // Predefined sets for fast checks
    private static final Set<String> UPGRADES = Set.of(
            "Sharpness",
            "Protection",
            "Maniac Miner",
            "Forge",
            "Heal Pool",
            "Its a trap",
            "Counter Attack",
            "Alarm",
            "Mining Fatigue"
    );

    // Predefined sets for fast checks
    public static final Set<Material> SWORDS_MATERIALS = EnumSet.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.DIAMOND_SWORD
    );
    public static double UpgradeRadius;

    static Component upgradeTitle = Component.text("Team Upgrade");

    public static Map<Team,HashMap<String, Integer>> teamUpgrades = new HashMap<>();
    private final Map<Team, Queue<ItemStack>> teamTraps = new HashMap<>();

    private final ItemStack diamond = new ItemStack(Material.DIAMOND);
    private final ItemStack grayStainedGlassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

    private ItemStack[] MakeUpgrades(Team playerTeam, int trapSize){

        int trapCost = (int) Math.round(Math.pow(2, trapSize));
        ItemStack sharpness = MakeSharpness(playerTeam);
        ItemStack protection = MakeProtection(playerTeam);
        ItemStack maniacMiner = MakeManiacMiner(playerTeam);
        ItemStack itsATrap = MakeItsATrap(trapCost);
        ItemStack counterAttack = MakeCounterAttack(trapCost);
        ItemStack alarm = MakeAlarm(trapCost);
        ItemStack forge = MakeForge(playerTeam);
        ItemStack healPool = MakeHealPool(playerTeam);
        // ItemStack DragonBuff = new ItemStack(Material.DRAGON_EGG); // To be continued
        ItemStack miningFatigue = MakeMiningFatigue(trapCost);

        return new ItemStack[]{sharpness, protection, maniacMiner, null, itsATrap, counterAttack, alarm, null, null, forge, healPool, null, null, miningFatigue};
    }

    private ItemStack[] ShowTraps(Queue<ItemStack> traps){
        ItemStack[] arrayTraps = new ItemStack[3];
        Queue<ItemStack> tempQueue = new ArrayDeque<>();
        for (int i = 0; i < 3; i++) {
            ItemStack trap = traps.poll();
            if (trap == null) {
                trap = new ItemStack(Material.GRAY_STAINED_GLASS, i+1);
            }
            else {
                tempQueue.add(trap);
            }
            arrayTraps[i] = trap;
        }
        int tempQueueSize = tempQueue.size();
        for (int i = 0; i < tempQueueSize; i++) {
            traps.add(tempQueue.poll());
        }
        return arrayTraps;
    }

    private ItemStack MakeItsATrap(int cost){
        ItemStack itsATrap = new ItemStack(Material.TRIPWIRE_HOOK);
        Component title = Component.text("Its a trap");

        List<Component> lore;

        if (cost > 4){
            lore = new ArrayList<>(List.of(
                    Component.text("Max Reached",NamedTextColor.GREEN , TextDecoration.BOLD)));

        }
        else {
            lore = new ArrayList<>(List.of(
                    Component.text("Cost : ").append(Component.text(cost + " Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))));
        }
        createItemMeta(itsATrap, title, lore);
        addTag(itsATrap, "Trap");
        return itsATrap;
    }

    private ItemStack MakeCounterAttack(int cost){
        ItemStack counterAttack = new ItemStack(Material.FEATHER);

        Component title = Component.text("Counter Attack");

        List<Component> lore;
        if (cost > 4){
            lore = new ArrayList<>(List.of(
                    Component.text("Max Reached",NamedTextColor.GREEN , TextDecoration.BOLD)));

        }
        else {
            lore = new ArrayList<>(List.of(
                    Component.text("Cost : ").append(Component.text(cost + " Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))));
        }
        createItemMeta(counterAttack, title, lore);
        addTag(counterAttack, "Trap");
        return counterAttack;
    }

    private ItemStack MakeAlarm(int cost){
        ItemStack alarm = new ItemStack(Material.REDSTONE_TORCH);

        Component title = Component.text("Alarm");

        List<Component> lore;
        if (cost > 4){
            lore = new ArrayList<>(List.of(
                    Component.text("Max Reached",NamedTextColor.GREEN , TextDecoration.BOLD)));

        }
        else {
            lore = new ArrayList<>(List.of(
                    Component.text("Cost : ").append(Component.text(cost + " Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))));
        }
        createItemMeta(alarm, title, lore);
        addTag(alarm, "Trap");
        return alarm;
    }

    private ItemStack MakeMiningFatigue(int cost){
        ItemStack miningFatigue = new ItemStack(Material.IRON_PICKAXE);

        Component title = Component.text("Mining Fatigue");

        List<Component> lore;
        if (cost > 4){
            lore = new ArrayList<>(List.of(
                    Component.text("Max Reached",NamedTextColor.GREEN , TextDecoration.BOLD)));

        }
        else {
            lore = new ArrayList<>(List.of(
                    Component.text("Cost : ").append(Component.text(cost + " Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))));
        }
        createItemMeta(miningFatigue, title, lore);
        addTag(miningFatigue, "Trap");
        return miningFatigue;
    }

    private ItemStack MakeSharpness(Team playerTeam){
        ItemStack sharpness = new ItemStack(Material.IRON_SWORD);

        Component title = Component.text("Sharpness");

        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("4 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))));

        int sharpnessLevel = teamUpgrades.get(playerTeam).get("Sharpness");

        if (sharpnessLevel >= 1){
            title = Component.text("Sharpness", NamedTextColor.GREEN);
            lore.set(0, Component.text("Unlocked : ", NamedTextColor.GREEN).append(Component.text("4 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)));
        }
        createItemMeta(sharpness, title, lore);
        return sharpness;
    }

    private ItemStack MakeHealPool(Team playerTeam){
        ItemStack healPool = new ItemStack(Material.BEACON);

        Component title = Component.text("Heal Pool");

        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Cost : ").append(Component.text("1 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))));

        int healPoolLevel = teamUpgrades.get(playerTeam).get("Heal Pool");

        if (healPoolLevel >= 1){
            title = Component.text("Heal Pool", NamedTextColor.GREEN);
            lore.set(0, Component.text("Unlocked : ", NamedTextColor.GREEN).append(Component.text("1 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)));
        }
        createItemMeta(healPool, title, lore);
        return healPool;
    }


    private ItemStack MakeProtection(Team playerTeam){
        ItemStack protection = new ItemStack(Material.IRON_CHESTPLATE);

        Component title = Component.text("Protection");

        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Tier 1 Cost : ").append(Component.text("2 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)),
                Component.text("Tier 2 Cost : ").append(Component.text("4 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)),
                Component.text("Tier 3 Cost : ").append(Component.text("8 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)),
                Component.text("Tier 4 Cost : ").append(Component.text("16 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))
        ));

        int protectionLevel = teamUpgrades.get(playerTeam).get("Protection");

        for (int i = 0; i < protectionLevel; i++) {
            lore.set(i, Component.text(String.format("Tier %d Cost : ", (i+1) ), NamedTextColor.GREEN).append(Component.text(String.format("%d Diamond", (2 << i)), TextColor.color(0x00FFFF), TextDecoration.BOLD)));
        }
        if (protectionLevel == 4){
            title = Component.text("Protection", NamedTextColor.GREEN);
        }
        createItemMeta(protection, title, lore);
        return protection;
    }

    private ItemStack MakeManiacMiner(Team playerTeam){

        ItemStack maniacMiner = new ItemStack(Material.GOLDEN_PICKAXE);
        Component title = Component.text("Maniac Miner");

        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Tier 1 Cost : ").append(Component.text("2 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)),
                Component.text("Tier 2 Cost : ").append(Component.text("4 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))
        ));

        int maniacMinerLevel = teamUpgrades.get(playerTeam).get("Maniac Miner");

        for (int i = 0; i < maniacMinerLevel; i++) {
            lore.set(i, Component.text(String.format("Tier %d Cost : ", (i+1) ), NamedTextColor.GREEN).append(Component.text(String.format("%d Diamond", (2 << i)), TextColor.color(0x00FFFF), TextDecoration.BOLD)));
        }
        if (maniacMinerLevel == 2){
            title = Component.text("Maniac Miner", NamedTextColor.GREEN);
        }
        createItemMeta(maniacMiner, title, lore);
        return maniacMiner;
    }

    private ItemStack MakeForge(Team playerTeam){
        ItemStack forge = new ItemStack(Material.FURNACE);

        Component title = Component.text("Forge");

        List<Component> lore = new ArrayList<>(List.of(
                Component.text("Tier 1 Cost : ").append(Component.text("2 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)),
                Component.text("Tier 2 Cost : ").append(Component.text("4 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)),
                Component.text("Tier 3 Cost : ").append(Component.text("6 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD)),
                Component.text("Tier 4 Cost : ").append(Component.text("8 Diamond", TextColor.color(0x00FFFF), TextDecoration.BOLD))
        ));

        int forgeLevel = teamUpgrades.get(playerTeam).get("Forge");

        for (int i = 1; i <= forgeLevel; i++) {
            lore.set(i-1, Component.text(String.format("Tier %d Cost : ", (i) ), NamedTextColor.GREEN).append(Component.text(String.format("%d Diamond", (2 * i)), TextColor.color(0x00FFFF), TextDecoration.BOLD)));
        }
        if (forgeLevel == 4){
            title = Component.text("Forge", NamedTextColor.GREEN);
        }
        createItemMeta(forge, title, lore);
        return forge;
    }

    private void createItemMeta(ItemStack item, Component title, List<Component> lore) {
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

    public void addTag(ItemStack item, String value) {
        // Get the item's meta
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Access the PersistentDataContainer
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

            // Add a custom tag
            dataContainer.set(key, PersistentDataType.STRING, value);

            // Apply the modified meta back to the item
            item.setItemMeta(meta);
        }
    }

    public TeamUpgrade(PersistentStorageHandler storage, Bedwars bedwars){
        this.bedwars = bedwars;
        // Initialize the scoreboard safely during onEnable()
        Bukkit.getScoreboardManager();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        // Step 2: Get all the teams
        Set<Team> teams = scoreboard.getTeams();

        // Step 3: Print the team names
        for (Team team : teams) {
            teamTraps.put(team, new ArrayDeque<>());
            teamUpgrades.put(team, new HashMap<>());
            for (String upgrade : UPGRADES){
                teamUpgrades.get(team).put(upgrade, 0);
            }
        }
        UpgradeRadius = 5;
        this.storage = storage;
    }

    public static boolean isTeamUpgrade(Villager villager) {
        // Ensure customName() is not null
        if (villager.customName() == null) return false;

        // Serialize the Component to plain text
        String villagerName = PlainTextComponentSerializer.plainText().serialize(villager.customName());

        // Serialize the upgradeTitle to plain text
        String shopTitleText = PlainTextComponentSerializer.plainText().serialize(upgradeTitle);

        // Check if the villager's name contains the upgradeTitle
        return villagerName.contains(shopTitleText);
    }

    private void CreateInventory(Player player, Entity entity){
        if (!(entity instanceof Villager villager)){
            return;
        }
        if (!isTeamUpgrade(villager)) {
            return;
        }

        // Get the team the player is on
        Team playerTeam = scoreboard.getEntryTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§c" + "You do not have a team");
            return;
        }

        Queue<ItemStack> traps = teamTraps.get(playerTeam);
        // add items to chestInventory as needed
        ItemStack[] upgrades = MakeUpgrades(playerTeam, traps.size());
        Inventory playerTeamUpgradeShop = Bukkit.createInventory(null, 54, upgradeTitle);
        player.openInventory(playerTeamUpgradeShop);
        for (int i = 0; i < upgrades.length; i++) {
            playerTeamUpgradeShop.setItem( i + 10, upgrades[i]);
        }
        for (int i = 27; i < 36; i++) {
            playerTeamUpgradeShop.setItem( i, grayStainedGlassPane);
        }
        ItemStack[] trapsArray = ShowTraps(traps);
        for (int i = 0; i < 3; i++) {
            playerTeamUpgradeShop.setItem( i + 39 , trapsArray[i]);
        }
    }

    private void UpdateInventory(Team playerTeam, Inventory playerTeamUpgradeShop){
        Queue<ItemStack> traps = teamTraps.get(playerTeam);
        // add items to chestInventory as needed
        ItemStack[] upgrades = MakeUpgrades(playerTeam, traps.size());
        for (int i = 0; i < upgrades.length; i++) {
            playerTeamUpgradeShop.setItem( i + 10, upgrades[i]);
        }
        for (int i = 27; i < 36; i++) {
            playerTeamUpgradeShop.setItem( i, grayStainedGlassPane);
        }
        ItemStack[] trapsArray = ShowTraps(traps);
        for (int i = 0; i < 3; i++) {
            playerTeamUpgradeShop.setItem( i + 39 , trapsArray[i]);
        }
    }

    private void BuySharpness(Player player, Team playerTeam){
        if (teamUpgrades.get(playerTeam).get("Sharpness") >= 1){
            player.sendMessage("§c" + "you already have sharpness");
            return;
        }
        Inventory players_inventory = player.getInventory();
        //check if the player have enough diamond to buy the upgrade
        if (!players_inventory.containsAtLeast(diamond, 4)){
            player.sendMessage("§c" + "not enough diamond");
            return;
        }
        players_inventory.removeItem(new ItemStack(Material.DIAMOND, 4));
        teamUpgrades.get(playerTeam).put("Sharpness", 1);
        setSharpness(player);
    }

    private void BuyHealPool(Player player, Team playerTeam){
        if (teamUpgrades.get(playerTeam).get("Heal Pool") >= 1){
            player.sendMessage("§c" + "you already have Heal Pool");
            return;
        }
        Inventory players_inventory = player.getInventory();
        //check if the player have enough diamond to buy the upgrade
        if (!players_inventory.containsAtLeast(diamond, 1)){
            player.sendMessage("§c" + "not enough diamond");
            return;
        }
        players_inventory.removeItem(new ItemStack(Material.DIAMOND, 1));
        teamUpgrades.get(playerTeam).put("Heal Pool", 1);
    }

    private void BuyProtection(Player player, Team playerTeam){
        int protectionLevel = teamUpgrades.get(playerTeam).get("Protection");
        if (protectionLevel >= 4){
            player.sendMessage("§c" + "you already have the max level of Protection");
            return;
        }
        Inventory players_inventory = player.getInventory();
        //check if the player have enough diamond to buy the upgrade
        if (!players_inventory.containsAtLeast(diamond, (2 << protectionLevel))){
            player.sendMessage("§c" + "not enough diamond");
            return;
        }
        players_inventory.removeItem(new ItemStack(Material.DIAMOND, (2 << protectionLevel)));
        teamUpgrades.get(playerTeam).put("Protection", protectionLevel + 1);
        setProtection(player);
    }

    private void BuyManiacMiner(Player player, Team playerTeam){
        int maniacMinerLevel = teamUpgrades.get(playerTeam).get("Maniac Miner");
        if (maniacMinerLevel >= 2){
            player.sendMessage("§c" + "you already have the max level of Maniac Miner");
            return;
        }
        Inventory players_inventory = player.getInventory();
        //check if the player have enough diamond to buy the upgrade
        if (!players_inventory.containsAtLeast(diamond, (2 * (maniacMinerLevel + 1)))){
            player.sendMessage("§c" + "not enough diamond");
            return;
        }
        players_inventory.removeItem(new ItemStack(Material.DIAMOND, (2 * (maniacMinerLevel + 1))));
        teamUpgrades.get(playerTeam).put("Maniac Miner", maniacMinerLevel + 1);
        setManiacMiner(player);
    }

    private void BuyForge(Player player, Team playerTeam){
        int forgeLevel = teamUpgrades.get(playerTeam).get("Forge");
        if (forgeLevel >= 4){
            player.sendMessage("§c" + "you already have the max level of Forge");
            return;
        }
        Inventory players_inventory = player.getInventory();
        //check if the player have enough diamond to buy the upgrade
        if (!players_inventory.containsAtLeast(diamond, (2 * (forgeLevel + 1)))){
            player.sendMessage("§c" + "not enough diamond");
            return;
        }
        // remove the diamonds from the player inventory and put the upgrade information onto a hashmap to be treated
        players_inventory.removeItem(new ItemStack(Material.DIAMOND, (2 * (forgeLevel + 1))));
        teamUpgrades.get(playerTeam).put("Forge", forgeLevel + 1);
        bedwars.forgeSpawner(playerTeam);
    }

    private void BuyTrap(InventoryClickEvent event, Player player, Team playerTeam){
        Queue<ItemStack> traps = teamTraps.get(playerTeam);
        int numOfTraps = traps.size();
        if (numOfTraps >= 3){
            player.sendMessage("§c" + "You have the maximum amount of traps");
            return;
        }
        Inventory players_inventory = player.getInventory();

        int trapCost = (int) Math.round(Math.pow(2, numOfTraps));
        //check if the player have enough diamond to buy the upgrade
        if (!players_inventory.containsAtLeast(diamond, trapCost)){
            player.sendMessage("§c" + "not enough diamond");
            return;
        }
        // remove the diamonds from the player inventory and put the upgrade information onto a hashmap to be treated
        players_inventory.removeItem(new ItemStack(Material.DIAMOND, trapCost));
        traps.add(event.getCurrentItem());
    }

    public static void setSharpness(Player player){
        // Get the team the player is on
        Team playerTeam = scoreboard.getEntryTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§c" + "You do not have a team");
            return;
        }
        int level =  teamUpgrades.get(playerTeam).get("Sharpness");
        if (level == 0){
            return;
        }
        Inventory playerInventory = player.getInventory();
        for (ItemStack itemStack : playerInventory) {
            if (itemStack != null){
                if (SWORDS_MATERIALS.contains(itemStack.getType())){
                    itemStack.addEnchantment(Enchantment.SHARPNESS, 1);
                }
            }
        }
    }

    public static void setProtection(Player player){
        // Get the team the player is on
        Team playerTeam = scoreboard.getEntryTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§c" + "You do not have a team");
            return;
        }
        int level = teamUpgrades.get(playerTeam).get("Protection");
        if (level == 0){
            return;
        }
        EntityEquipment equipment = player.getEquipment();
        if (equipment.getBoots() != null) {
            equipment.getBoots().addEnchantment(Enchantment.PROTECTION, level);
        }
        if (equipment.getLeggings() != null) {
            equipment.getLeggings().addEnchantment(Enchantment.PROTECTION, level);
        }
        if (equipment.getChestplate() != null) {
            equipment.getChestplate().addEnchantment(Enchantment.PROTECTION, level);
        }
        if (equipment.getHelmet() != null) {
            equipment.getHelmet().addEnchantment(Enchantment.PROTECTION, level);
        }
    }

    public static void setManiacMiner(Player player){
        // Get the team the player is on
        Team playerTeam = scoreboard.getEntryTeam(player.getName());
        if (playerTeam == null) {
            player.sendMessage("§c" + "You do not have a team");
            return;
        }
        int level = teamUpgrades.get(playerTeam).get("Maniac Miner");
        if (level == 0){
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 1000000, level - 1));
    }

    public void setHealPool(Team team){
        int level = teamUpgrades.get(team).get("Heal Pool");
        if (level == 0){
            //getLogger().warning("heal pool is not bought");
            return;
        }
        Location spawnPoint = getTeamSpawnPoint(team);
        if (spawnPoint == null) {
            //getLogger().severe("There is no spawnPoint for the team");
            return;
        }
        Collection<Player> players = overworld.getNearbyPlayers(spawnPoint, UpgradeRadius);
        if (players.isEmpty()){
            //getLogger().warning("there is no player around the spawnPoint");
            return;
        }
        for (Player player : players) {
            if (scoreboard.getEntityTeam(player) == null){
                continue;
            }
            if (team.getName().equals(scoreboard.getEntityTeam(player).getName())){
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
            }
        }
    }

    private Location getTeamSpawnPoint(Team team){
        for (String entry : team.getEntries()){
            Player player = Bukkit.getPlayer(entry);
            // Add the player if they are online
            if (player != null && player.isOnline()) {
                return player.getRespawnLocation();
            }
        }
        return null;
    }

    public void PlayerTrapTriggerTitle(Player player) {
        // Create the title and subtitle components
        Component title = Component.text("Trap" + "§c" + "Triggered");
        Component subtitleComponent = Component.text("beware of the man next to your bed");

        // Send the title using Adventure
        player.showTitle(net.kyori.adventure.title.Title.title(
                title,
                subtitleComponent,
                net.kyori.adventure.title.Title.Times.times(
                        java.time.Duration.ZERO, // Fade-in duration
                        java.time.Duration.ofSeconds(3), // Stay duration
                        java.time.Duration.ZERO // Fade-out duration
                )
        ));
    }

    private void setTrapEffect(List<Player> enemies,List<Player> allies , Material trap){
        switch (trap){
            case Material.TRIPWIRE_HOOK:
                for (Player enemiePlayer : enemies){
                    enemiePlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 0));
                    enemiePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 0));
                }
                break;
            case Material.FEATHER:
                for (Player ally : allies) {
                    ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 45*20, 2));
                    ally.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 45*20, 2));
                }
                break;
            case Material.REDSTONE_TORCH:
                for (Player enemiePlayer : enemies){
                    enemiePlayer.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
                break;
            case Material.IRON_PICKAXE:
                for (Player enemiePlayer : enemies){
                    enemiePlayer.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 30*20, 1));
                }
                break;
            default:
                getLogger().warning("Was not able to set up traps");
                break;
        }
        for (Player ally : allies) {
            PlayerTrapTriggerTitle(ally);
        }
    }

    public void setTrap(Team team){

        if (teamTraps.get(team).isEmpty()){
            return;
        }
        Location bed = storage.getLocationByTag(team.getName());
        if (bed == null) {
            return;
        }
        Collection<Player> players = overworld.getNearbyPlayers(bed, UpgradeRadius);
        if (players.isEmpty()){
            return;
        }
        List<Player> allies = new ArrayList<>(List.of());
        List<Player> enemies = new ArrayList<>(List.of());
        // Get all entries in the team
        for (String entry : team.getEntries()) {
            Player player = Bukkit.getPlayer(entry); // Convert entry to Player
            // Add only online players to the list
            if (player != null && player.isOnline()) {
                allies.add(player);
            }
        }
        for (Player player : players){
            if (scoreboard.getEntityTeam(player) != null){
                if (!team.getName().equals(scoreboard.getEntityTeam(player).getName())){
                    enemies.add(player);
                }
            }
        }
        if (enemies.isEmpty()){
            return;
        }
        ItemStack trap = teamTraps.get(team).poll();
        if (trap == null){
            return;
        }
        for (Player player : players) {
            if (scoreboard.getEntityTeam(player) == null) {
                continue;
            }
            if (!team.getName().equals(scoreboard.getEntityTeam(player).getName())){
                setTrapEffect(enemies, allies, trap.getType());
            }
        }

    }

    public void ApplyBedUpgrade(){
        Set<Team> teams = scoreboard.getTeams();
        for (Team team : teams) {
            setTrap(team);
            setHealPool(team);
        }
    }

    private void menu(InventoryClickEvent event, Player player){
        int slotPos = event.getRawSlot();
        Inventory clickedInv = event.getClickedInventory();
        Inventory TeamUpgradeInventory = event.getView().getTopInventory();

        if (clickedInv != TeamUpgradeInventory){
            return;
        }

        // Get the team the player is on
        Team playerTeam = scoreboard.getEntryTeam(player.getName());
        if (playerTeam == null) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        assert currentItem != null;


        switch (slotPos){
            case 10:
                BuySharpness(player, playerTeam);
                break;
            case 11:
                BuyProtection(player, playerTeam);
                break;
            case 12:
                BuyManiacMiner(player, playerTeam);
                break;
            case 19:
                BuyForge(player, playerTeam);
                break;
            case 20:
                BuyHealPool(player, playerTeam);
                break;
            case 14, 15, 16, 23:
                BuyTrap(event, player, playerTeam);
                break;
            default:
                getLogger().warning("Was not able to get Team upgrade");
                break;
        }
        UpdateInventory(playerTeam, TeamUpgradeInventory);
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
        if (event.getView().title().equals(upgradeTitle)) {
            // Ensure the player is interacting
            if (!(event.getWhoClicked() instanceof Player player)) return;
            // Get the player
            ItemStack clickedItem = event.getCurrentItem(); // Get the clicked item
            // Ensure the clicked item is not null or air
            if (clickedItem == null || clickedItem.getType().isAir()) return;
            menu(event, player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ApplyBedUpgrade();
    }
}
