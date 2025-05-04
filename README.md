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
An intuitive hotbar handler for interactive server lobbies or kits:
- Dynamic item assignment per slot
- Built-in cooldown system with EXP bar visual feedback
- Automatic item blocking while on cooldown
- Supports toggle-type items (e.g. hide/show players)
- Fully integrated with MenuAPI and ScoreboardAPI

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

