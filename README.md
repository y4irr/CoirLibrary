# CoirLibrary

**CoirLibrary** is a modular, *from-scratch* Kotlin library by [y4irr](https://github.com/y4irr), built to streamline Minecraft Java plugin development. It provides clean, extensible, and high-performance APIs for creating scoreboards, menus, hotbars, and (coming soon) commands.

## ✨ Features

### ⚔️ ScoreboardAPI
A modern scoreboard system with full annotation support:
- `@Title`, `@Tickable`, `@SbOptions`, `@BoardStyle`, and more
- Score ordering (ascending, descending, or custom)
- Async updates and optimized entry rendering
- Up to 32-character lines with color-safe spacing

### 🧩 MenuAPI
A powerful GUI framework for menus with deep customization:
- Full annotation support: `@updateAfterClick`, `@CancelPlayerInventory`, `@UsePlayerInventory`, `@MoveItemToPlayer`
- Visual inventory replacement and item protection
- Smooth transitions between menus and player inventory integration
- Automatic restoration of inventory and state on close

### 🧠 Hotbar System
- Assign custom actions to LEFT, RIGHT, and MIDDLE click
- Per-player cooldowns with automatic XP bar + level display
- Optional `.blockUse()` disables default item behavior (like throwing pearls) but keeps executing code
- Hotbar automatically reapplied on join, death, and respawn
- Cooldown messages with per-player delay to prevent spam
- Items are not dropped on death
- Hotbar items can be dynamically updated or replaced at runtime

### 🧪 CommandAPI *(Coming Soon)*
A fully annotated command system with:
- Async command execution
- Custom tab completion
- Argument mapping and validation

## 🛠 Installation

CoirLibrary is not yet published to a public repository. For now, add it manually as a local dependency:

```kotlin
dependencies {
    implementation(files("libs/CoirLibrary.jar"))
}
```

## 🧪 Example Usages

### Scoreboard
- Kotlin
```kotlin
@SbOptions(isAsync = true)
@Tickable(tick = 2L)
@BoardStyle(type = BoardStyleType.ASCENDING)
@Title("EMPTY") // IF THIS IS SET AS EMPTY STRING, THE METHOD getTitle() WILL WORK
class ScoreAdapter : WaiAdapter {

    override fun getTitle(player: Player): String {
        return "&6&lSCOREBOARD" // IF @Title IS NOT ADDED THIS WILL 
    }                           // WORK INSTEAD THE ANNOTATION

    override fun getLines(player: Player): List<String> {
        return listOf(
            "&7&m--------------------------",
            "&eOnline:",
            "&f0",
            "",
            "&eRank:",
            "&aDefault",
            "",
            "&etesting.scoreboard!",
            "&7&m--------------------------")
    }
}
```
- Java
```java
@SbOptions(isAsync = true)
@Tickable(tick = 2L)
@BoardStyle(type = BoardStyleType.ASCENDING)
@Title("EMPTY") // If this is set as "EMPTY", the method getTitle() will be used instead
public class ScoreAdapter implements WaiAdapter {

    @Override
    public String getTitle(Player player) {
        return "&6&lSCOREBOARD"; // Only used if @Title is "EMPTY"
    }

    @Override
    public List<String> getLines(Player player) {
        return Arrays.asList(
            "&7&m--------------------------",
            "&eOnline:",
            "&f0",
            "",
            "&eRank:",
            "&aDefault",
            "",
            "&etesting.scoreboard!",
            "&7&m--------------------------"
        );
    }
}
```
### Menu
- Kotlin
```kotlin
@CancelPlayerInventory(false)
@UpdateAfterClick(true)
class SimpleMenu : Menu() {

    override fun getTitle(player: Player): String = "&bSimple Menu"

    override fun getButtons(player: Player): Map<Int, KButton> {
        return mapOf(
            2 to Button.of(Material.STONE, "&aItem 1") {
                player.sendMessage("&aYou clicked Item 1!")
            },
            4 to Button.of(Material.DIRT, "&eItem 2") {
                player.sendMessage("&eYou clicked Item 2!")
            },
            6 to Button.of(Material.GRASS, "&cItem 3") {
                player.sendMessage("&cYou clicked Item 3!")
            }
        )
    }
}
```
- Java
```java
@CancelPlayerInventory(false)
@UpdateAfterClick(true)
public class SimpleMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&bSimple Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(2, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.STONE)
                        .name("&aItem 1")
                        .build();
            }

            @Override
            public void onClick(Player player, int slot, ItemStack item) {
                player.sendMessage("&aYou clicked Item 1!");
            }
        });

        buttons.put(4, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.DIRT)
                        .name("&eItem 2")
                        .build();
            }

            @Override
            public void onClick(Player player, int slot, ItemStack item) {
                player.sendMessage("&eYou clicked Item 2!");
            }
        });

        buttons.put(6, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.GRASS)
                        .name("&cItem 3")
                        .build();
            }

            @Override
            public void onClick(Player player, int slot, ItemStack item) {
                player.sendMessage("&cYou clicked Item 3!");
            }
        });

        return buttons;
    }
}
```

### HotbarAPI
```kotlin
object HotbarLayout {
    fun getItems(): List<HotbarItem> {
        return listOf(
            // Example 1: Launch with ender pearl on right click
            HotbarItem(
                ItemBuilder(Material.ENDER_PEARL)
                    .setName("&6Ender Butt")
                    .setLore("&7Right click to dash forward")
                    .build(),
                0
            )
                .blockUse() // This blocks the use, it just executes the code
                .putCooldown(5)
                .setAction(HotbarActionType.RIGHT_CLICK) { player ->
                    player.velocity = player.location.direction.multiply(1.6)
                    player.world.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1f, 1f)
                    player.sendMessage("&aYou dashed forward!")
                },

            // Example 2: Heal effect on left click
            HotbarItem(
                ItemBuilder(Material.BLAZE_ROD)
                    .setName("&cHeal Wand")
                    .setLore("&7Left click to heal", "&8(10s cooldown)")
                    .build(),
                1
            )
                .putCooldown(10)
                .setAction(HotbarActionType.LEFT_CLICK) { player ->
                    player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 1)) // 4 seconds
                    player.sendMessage("&aYou used your healing wand!")
                    player.world.playSound(player.location, Sound.LEVEL_UP, 1f, 1.2f)
                },

            // Example 3: Debug message on middle click
            HotbarItem(
                ItemBuilder(Material.BOOK)
                    .setName("&eInfo Tool")
                    .setLore("&7Middle click to see info")
                    .build(),
                2
            )
                .setAction(HotbarActionType.MIDDLE_CLICK) { player ->
                    player.sendMessage("&bYour current location: &f${player.location.blockX}, ${player.location.blockY}, ${player.location.blockZ}")
                    player.world.playSound(player.location, Sound.NOTE_PLING, 1f, 2f)
                }
        )
    }
}
```
- Java
```java
public class HotbarLayout {

    public static List<HotbarItem> getItems() {
        return Arrays.asList(
            // Example 1: Launch with ender pearl on right click
            new HotbarItem(
                new ItemBuilder(Material.ENDER_PEARL)
                    .setName("&6Ender Butt")
                    .setLore("&7Right click to dash forward")
                    .build(),
                0
            )
            .blockUse()
            .putCooldown(5)
            .setAction(HotbarActionType.RIGHT_CLICK, (Player player) -> {
                player.setVelocity(player.getLocation().getDirection().multiply(1.6));
                player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
                player.sendMessage("§aYou dashed forward!");
            }),

            // Example 2: Heal effect on left click
            new HotbarItem(
                new ItemBuilder(Material.BLAZE_ROD)
                    .setName("&cHeal Wand")
                    .setLore("&7Left click to heal", "&8(10s cooldown)")
                    .build(),
                1
            )
            .putCooldown(10)
            .setAction(HotbarActionType.LEFT_CLICK, (Player player) -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 1));
                player.sendMessage("§aYou used your healing wand!");
                player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1.2f);
            }),

            // Example 3: Debug message on middle click
            new HotbarItem(
                new ItemBuilder(Material.BOOK)
                    .setName("&eInfo Tool")
                    .setLore("&7Middle click to see info")
                    .build(),
                2
            )
            .setAction(HotbarActionType.MIDDLE_CLICK, (Player player) -> {
                player.sendMessage("§bYour current location: §f" +
                        player.getLocation().getBlockX() + ", " +
                        player.getLocation().getBlockY() + ", " +
                        player.getLocation().getBlockZ());
                player.getWorld().playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 2f);
            })
        );
    }
}
```

