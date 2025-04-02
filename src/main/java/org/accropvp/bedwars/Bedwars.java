package org.accropvp.bedwars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.Duration;
import java.util.*;

public final class Bedwars extends JavaPlugin implements Listener {

    // Step 1: Get the main scoreboard
    private Scoreboard scoreboard;

    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

    private World overworld = null;

    Set<Material> droppedItem = EnumSet.of(
            Material.IRON_INGOT,
            Material.GOLD_INGOT,
            Material.DIAMOND,
            Material.EMERALD
    );

    int secondsPassed = 5;
    boolean timeup = false;

    public static Map<Team, Boolean> isTeamBedHere = new HashMap<>();
    public static Map<Team, Integer> alivePlayer = new HashMap<>();

    public static int scoreboardTimer;

    private int bedwarsEventHandlerLevel;
    private boolean HasStarted;

    private PersistentStorageHandler storageHandler;

    private DynamicScoreboard dynamicScoreboard;
    private int DiamondSpawnerID;
    private int EmeraldSpawnerID;
    private BukkitTask timerScoreboardEventTask;

    private final List<Integer> taskId = new ArrayList<>();
    private final Map<Team, List<Integer>> TeamForgeTaskId = new HashMap<>();
    public final String[] ValidTeamName = {"RED", "BLUE", "CYAN", "GREEN", "YELLOW", "PINK", "GRAY", "WHITE"};


    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        // Initialize persistent storage
        storageHandler = new PersistentStorageHandler(getDataFolder());
        dynamicScoreboard = new DynamicScoreboard();
        // Initialize and register the Item Shop
        TeamUpgrade teamUpgrade = new TeamUpgrade(storageHandler, this);
        getServer().getPluginManager().registerEvents(teamUpgrade, this);
        ItemShop itemShop = new ItemShop();
        getServer().getPluginManager().registerEvents(itemShop, this);
        // Register the command
        getCommand("StartBedwars").setExecutor(new BedwarsStart(this));
        getCommand("tagbed").setExecutor(new TagBedCommand(storageHandler));
        getCommand("tagbed").setTabCompleter(new TagbedTabCompleter());
        getCommand("upgradeRadius").setExecutor(new GetUpgradeRadius(storageHandler));
        getCommand("diamondSpawnPoint").setExecutor(new DiamondSpawnPointCommand(storageHandler));
        getCommand("diamondSpawnPoint").setTabCompleter(new DiamondAndEmeraldSpawnPointTabCompleater());
        getCommand("emeraldSpawnPoint").setExecutor(new EmeraldSpawnPointCommand(storageHandler));
        getCommand("emeraldSpawnPoint").setTabCompleter(new DiamondAndEmeraldSpawnPointTabCompleater());
        getCommand("forgeSpawnPoint").setExecutor(new ForgeSpawnPointCommand(storageHandler));
        getCommand("forgeSpawnPoint").setTabCompleter(new ForgeSpawnPointTabCompleter());
        getCommand("removeTag").setExecutor(new RemoveTag(storageHandler));
        getCommand("removeTag").setTabCompleter(new RemoveTagTabCompleter(storageHandler));
        // Register the listener
        getServer().getPluginManager().registerEvents(new BedBreakListener(storageHandler, dynamicScoreboard), this);

        overworld = Bukkit.getServer().getWorlds().getFirst();

        // Initialize the scoreboard
        Bukkit.getScoreboardManager();
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        HasStarted = false;
    }

    public void start(){
        HasStarted = true;
        dynamicScoreboard.updateScoreboard();
        // Step 2: Get all the teams
        Set<Team> teams = scoreboard.getTeams();
        TeamUpgrade.UpgradeRadius = Double.parseDouble(storageHandler.getValue("UpgradeRadius"));
        // Step 3: Print the team names
        for (Team team : teams) {
            if (!Arrays.asList(ValidTeamName).contains(team.getName())){
                getLogger().warning(team.getName() + " is not a valid team name");
                continue;
            }
            Set<String> storageKeys = storageHandler.getAllKeys();
            for (String key : storageKeys){
                if (Objects.equals(storageHandler.getValue(key), team.getName())){
                    isTeamBedHere.put(team, true);
                }
            }
            if (!isTeamBedHere.containsKey(team)) {
                isTeamBedHere.put(team, false);
            }
            TeamForgeTaskId.put(team, new ArrayList<>());
            forgeSpawner(team);
            int playerCount = 0;
            for (String entries : team.getEntries()){
                Player player = Bukkit.getPlayer(entries);
                if (player != null && player.isOnline()) {
                    player.setMaxHealth(20.0);  // Set max health to full health
                    player.setHealth(20.0);     // Ensure they are at full health
                    applyBasicGear(player, team);
                    if (player.getRespawnLocation() == null){
                        getLogger().severe("respawn location not set up properly, player "+ player + " respawnLocation is not set");
                    }
                    player.teleport(player.getRespawnLocation());
                    playerCount++;
                }
            }
            alivePlayer.put(team, playerCount);
            dynamicScoreboard.updateScorboardTeam(team);
        }
        bedwarsEventHandler(0);
        dynamicScoreboard.updateScoreboardNextEvent(ChatColor.AQUA + "Diamond II");
        dynamicScoreboard.updateScoreboardTimer(5*60);
    }

    public void stop(){
        HasStarted = false;
        if (timerScoreboardEventTask != null) {
            timerScoreboardEventTask.cancel();
        }
        for (int task : taskId){
            // Cancel the task on plugin disable
            Bukkit.getScheduler().cancelTask(task);
        }
        for (List<Integer> l : TeamForgeTaskId.values()){
            for (int task : l){
                // Cancel the task on plugin disable
                Bukkit.getScheduler().cancelTask(task);
            }
        }
    }

    public void timerScoreboardEvent(int time){
        scoreboardTimer = time;
        timerScoreboardEventTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (scoreboardTimer < 0){
                    bedwarsEventHandlerLevel++;
                    bedwarsEventHandler(bedwarsEventHandlerLevel);
                    this.cancel();
                    return;
                }
                dynamicScoreboard.updateScoreboardTimer(scoreboardTimer);
                scoreboardTimer--;
            }
        }.runTaskTimer(this,0L, 20L);
    }

    public void bedwarsEventHandler(int level){
        switch (level){
            case 0:
                DiamondSpawnerID = diamondsSpawner(40);
                EmeraldSpawnerID = emeraldsSpawner(60);
                dynamicScoreboard.updateScoreboardNextEvent(ChatColor.AQUA + "Diamond II");
                timerScoreboardEvent(5 * 60);
                break;
            case 1:
                scheduler.cancelTask(DiamondSpawnerID);
                taskId.remove(Integer.valueOf(DiamondSpawnerID));
                DiamondSpawnerID = diamondsSpawner(30);
                dynamicScoreboard.updateScoreboardNextEvent(ChatColor.GREEN + "Emerald II");
                timerScoreboardEvent(5*60);
                break;
            case 2:
                scheduler.cancelTask(EmeraldSpawnerID);
                taskId.remove(Integer.valueOf(EmeraldSpawnerID));
                EmeraldSpawnerID = emeraldsSpawner(45);
                dynamicScoreboard.updateScoreboardNextEvent(ChatColor.AQUA + "Diamond III");
                timerScoreboardEvent(5*60);
                break;
            case 3:
                scheduler.cancelTask(DiamondSpawnerID);
                taskId.remove(Integer.valueOf(DiamondSpawnerID));
                DiamondSpawnerID = diamondsSpawner(20);
                dynamicScoreboard.updateScoreboardNextEvent(ChatColor.GREEN + "Emerald III");
                timerScoreboardEvent(5*60);
                break;
            case 4:
                scheduler.cancelTask(EmeraldSpawnerID);
                taskId.remove(Integer.valueOf(EmeraldSpawnerID));
                EmeraldSpawnerID = emeraldsSpawner(30);
                dynamicScoreboard.updateScoreboardNextEvent(ChatColor.RED + "Sudden death");
                timerScoreboardEvent(5*60);
                break;
            case 5:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                    if (!player.isDead() && player.getGameMode() == GameMode.SURVIVAL) {
                        player.setMaxHealth(1.0);  // Set max health to half a heart
                        player.setHealth(1.0);     // Ensure they are at half a heart
                    }
                }
                for (Team team : isTeamBedHere.keySet()){
                    isTeamBedHere.put(team, false);
                    dynamicScoreboard.updateScorboardTeam(team);
                }
                break;
            default:
                getLogger().severe("bedwars next event handler not working properly");
                break;
        }
    }

    public void forgeSpawner(Team team){
        List<Integer> teamTaskList = TeamForgeTaskId.get(team);
        if (!teamTaskList.isEmpty()){
            for (int task : teamTaskList){
                Bukkit.getScheduler().cancelTask(task);
            }
            teamTaskList.clear();
        }
        Set<Location> ForgeLocations = storageHandler.getLocationsForTag(team.getName());
        if (ForgeLocations.isEmpty()){
            getLogger().warning("There is no forge location for the team : " + team.getName());
            return;
        }
        int forgeLvl = TeamUpgrade.teamUpgrades.get(team).get("Forge");
        // Schedule the task and store its ID
        int task = scheduler.scheduleSyncRepeatingTask(this, () -> {
            if (ForgeLocations.isEmpty()){
                getLogger().severe("There is no forge location for the team : " + team.getName());
            }
            for (Location location : ForgeLocations){
                overworld.dropItem(location, new ItemStack(Material.IRON_INGOT, 2 * (forgeLvl +1)));
            }
        }, 0L, 2*20L);
        teamTaskList.add(task);
        // Schedule the task and store its ID
        int task2 = scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (Location location : ForgeLocations){
                overworld.dropItem(location, new ItemStack(Material.GOLD_INGOT, (forgeLvl +1)));
            }
        }, 0L, 5*20L);

        teamTaskList.add(task2);
        if (forgeLvl < 3){
            return;
        }
        //teamTaskList.add(task);
        // Schedule the task and store its ID
        int task3 = scheduler.scheduleSyncRepeatingTask(this, () -> {
            for (Location location : ForgeLocations){
                overworld.dropItem(location, new ItemStack(Material.EMERALD, forgeLvl - 2));
            }
        }, 0L, 15*20L);
        teamTaskList.add(task3);
    }


    public int diamondsSpawner(int time){

        // Schedule the task and store its ID
        int task = scheduler.scheduleSyncRepeatingTask(this, () -> {
            Set<Location> locations = storageHandler.getLocationsForTag("DIAMOND_SPAWNER");
            for (Location location : locations){
                overworld.dropItem(location, new ItemStack(Material.DIAMOND));
            }
        }, 0L, time*20L);
        taskId.add(task);
        return task;
    }

    public int emeraldsSpawner(int time){

        // Schedule the task and store its ID
        int task = scheduler.scheduleSyncRepeatingTask(this, () -> {
            Set<Location> locations = storageHandler.getLocationsForTag("EMERALD_SPAWNER");
            for (Location location : locations){
                overworld.dropItem(location, new ItemStack(Material.EMERALD));
            }
        }, 0L, time*20L);
        taskId.add(task);
        return task;
    }

    public ItemStack createColoredEquipment(ItemStack item ,Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        if (meta != null) {
            meta.setColor(color);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void ApplyColoredEquipment(Player player , Color color){
        ItemStack helmet = createColoredEquipment(new ItemStack(Material.LEATHER_HELMET), color);
        ItemStack chestPlate = createColoredEquipment(new ItemStack(Material.LEATHER_CHESTPLATE), color);
        ItemStack leggings = createColoredEquipment(new ItemStack(Material.LEATHER_LEGGINGS), color);
        ItemStack boots = createColoredEquipment(new ItemStack(Material.LEATHER_BOOTS), color);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestPlate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    public void applyBasicGear(Player player, Team team){

        switch (team.getName()) {
            case "RED" -> ApplyColoredEquipment(player, Color.RED);
            case "BLACK" -> ApplyColoredEquipment(player, Color.BLACK);
            case "BROWN" -> ApplyColoredEquipment(player, Color.MAROON);
            case "BLUE" -> ApplyColoredEquipment(player, Color.BLUE);
            case "CYAN" -> ApplyColoredEquipment(player, Color.AQUA);
            case "GRAY" -> ApplyColoredEquipment(player, Color.GRAY);
            case "GREEN" -> ApplyColoredEquipment(player, Color.GREEN);
            case "LIME" -> ApplyColoredEquipment(player, Color.LIME);
            case "MAGENTA" -> ApplyColoredEquipment(player, Color.FUCHSIA);
            case "ORANGE" -> ApplyColoredEquipment(player, Color.ORANGE);
            case "PURPLE" -> ApplyColoredEquipment(player, Color.PURPLE);
            case "PINK" -> ApplyColoredEquipment(player, Color.fromRGB(255, 192, 203));
            case "YELLOW" -> ApplyColoredEquipment(player, Color.YELLOW);
            case "LIGHT_BLUE" -> ApplyColoredEquipment(player, Color.fromRGB(172, 223, 221));
            case "LIGHT_GRAY" -> ApplyColoredEquipment(player, Color.SILVER);
            case "WHITE" -> ApplyColoredEquipment(player, Color.WHITE);
            default -> ApplyColoredEquipment(player, Color.WHITE);
        };
    }

    @Override
    public void onDisable() {
        stop();
        // Plugin shutdown logic
        System.out.println("bye world");
    }

    private ItemStack ManageItemOnDeath(Location playerLocation, ItemStack item){
        if (item == null){
            return null;
        }
        Material mat = item.getType();
        if (droppedItem.contains(mat)){
            // Spawn the item at the location
            playerLocation.getWorld().dropItem(playerLocation, item);
            return null;
        }
        return switch (mat){
            case Material.SHEARS, Material.WOODEN_PICKAXE, Material.WOODEN_AXE -> item;
            case Material.STONE_PICKAXE -> ItemShop.woodenPickaxe;
            case Material.IRON_PICKAXE -> ItemShop.stonePickaxe;
            case Material.DIAMOND_PICKAXE -> ItemShop.ironPickaxe;
            case Material.STONE_AXE -> ItemShop.woodenAxe;
            case Material.IRON_AXE -> ItemShop.stoneAxe;
            case Material.DIAMOND_AXE -> ItemShop.ironAxe;
            default -> null;
        };
    }

    private void ManageItemsOnDeath(Location deathLocation, Inventory playerInventory){
        for (int i = 0; i < playerInventory.getSize()-5; i++) {
            playerInventory.setItem(i ,ManageItemOnDeath(deathLocation, playerInventory.getItem(i)));
        }
        for (int i = 0; i < playerInventory.getSize()-5; i++) {
            if (playerInventory.getItem(i) == null) {
                playerInventory.setItem(i, new ItemStack(Material.WOODEN_SWORD));
                return;
            }
        }
    }

    private Team hasWon(){
        Team lastTeam = null;
        Set<Team> teams = isTeamBedHere.keySet();
        for (Team team : teams){
            if (isTeamBedHere.get(team)){
                lastTeam = team;
                teams.remove(team);
                break;
            }
            if (alivePlayer.get(team) > 0){
                lastTeam = team;
                teams.remove(team);
                break;
            }
        }

        for (Team team : teams){
            if (isTeamBedHere.get(team)) return null;
            if (alivePlayer.get(team) > 0) return null;
        }
        return lastTeam;
    }
    private void showWin(String teamName){
        // Create the title and subtitle components
        Component title = Component.text(teamName + " has won");
        Component subtitleComponent = Component.text("GG everyone");

        for (Player player : Bukkit.getOnlinePlayers()){
            // Send the title using Adventure
            player.showTitle(Title.title(
                    title,
                    subtitleComponent,
                    Title.Times.times(
                            Duration.ofSeconds(1), // Fade-in duration
                            Duration.ofSeconds(3), // Stay duration
                            Duration.ofSeconds(1) // Fade-out duration
                    )
            ));
        }
    }

    public void PlayerDeathTitle(Player player, int secondsPassed) {
        // Create the title and subtitle components
        Component title = Component.text("§cYou died");
        Component subtitleComponent = Component.text("Respawn in " + secondsPassed);

        // Send the title using Adventure
        player.showTitle(Title.title(
                title,
                subtitleComponent,
                Title.Times.times(
                        Duration.ZERO, // Fade-in duration
                        Duration.ofSeconds(1), // Stay duration
                        Duration.ZERO // Fade-out duration
                )
        ));
    }

    public void PlayerDeathCooldown(Player player, Player killer){
        Team team = scoreboard.getEntityTeam(player);
        if (team == null) {
            return;
        }
        if (!isTeamBedHere.get(team)){
            alivePlayer.put(team, alivePlayer.get(team) -1);
            dynamicScoreboard.updateScorboardTeam(team);
            Team winner = hasWon();
            if (winner != null){
                showWin(winner.getName());
            }
            return;
        }
        //set the timer
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // code to be executed every 1 second
                if (secondsPassed <= 0){
                    secondsPassed = 5;
                    timeup = true;
                    cancel();
                }
                else {
                    PlayerDeathTitle(player, secondsPassed);
                    secondsPassed--;
                }
            }
        };
        // Start the timer
        timer.schedule(task, 0, 1000); // schedule the task to run every 1 second
        scheduler.runTaskLater(this, () -> {
            // Teleport the player to his spawnPoint location
            Location spawnPoint = player.getRespawnLocation();
            if (spawnPoint != null){
                player.teleport(spawnPoint);
            }
            player.setGameMode(GameMode.SURVIVAL);
            TeamUpgrade.setManiacMiner(player);
            TeamUpgrade.setSharpness(player);
        },100);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        dynamicScoreboard.setCustomScoreboard(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!HasStarted){
            return;
        }
        Player player = event.getEntity();
        // Get the location of the player at the time of death
        Location deathLocation = player.getLocation();
        ManageItemsOnDeath(deathLocation, player.getInventory());
        player.setGameMode(GameMode.SPECTATOR);
        PlayerDeathCooldown(player, event.getEntity().getKiller());
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        event.getInventory().setResult(null); // Prevent crafting the shield
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        event.setCancelled(true); // Block the crafting action
        event.getWhoClicked().sendMessage("You are not allowed to craft!");
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        Inventory playerInventory = event.getPlayer().getInventory();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        Material material = droppedItem.getType();
        // Check if the dropped item is any type of armor
        if (material.name().contains("_HELMET") ||
            material.name().contains("_CHESTPLATE") ||
            material.name().contains("_LEGGINGS") ||
            material.name().contains("_BOOTS")){
            // Cancel the drop event
            event.setCancelled(true);
            // Optionally, send a message to the player
            event.getPlayer().sendMessage("§cYou cannot drop armor!");
        } else if (material.name().contains("_SWORD")) {
            if (material == Material.WOODEN_SWORD){
                event.setCancelled(true);
                return;
            }
            for (int i = 0; i < playerInventory.getSize()-5; i++) {
                if (playerInventory.getItem(i) == null) {
                    playerInventory.setItem(i, new ItemStack(Material.WOODEN_SWORD));
                    return;
                }
            }
        }
    }

    // Prevent explosions from destroying glass blocks
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (!isBreakable(block.getType()) || block.getType() == Material.GLASS) {
                iterator.remove(); // Remove glass blocks from the explosion effect
            }
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        Iterator<Block> iterator = event.blockList().iterator();

        while (iterator.hasNext()) {
            Block block = iterator.next();
            if (!isBreakable(block.getType()) || block.getType() == Material.GLASS) {
                iterator.remove(); // Remove breakable blocks from the explosion effect
            }
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE){
            return;
        }
        if (!isBreakable(event.getBlock().getType())){
            event.setCancelled(true);
        }
    }

    private boolean isBreakable(Material material){

        if (material.name().endsWith("_BED")){
            return true;
        }
        if(material.name().endsWith("_WOOL")){
            return true;
        }
        if (material == Material.TERRACOTTA){
            return true;
        }
        if (material == Material.GLASS){
            return true;
        }
        if (material == Material.END_STONE){
            return true;
        }
        if (material == Material.LADDER){
            return true;
        }
        if (material == Material.OAK_PLANKS){
            return true;
        }
        if (material == Material.OBSIDIAN){
            return true;
        }
        return false;
    }
}