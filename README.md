# MorePeripherals
Adds new peripherals for CC:Tweaked

The mod adds :

- Trading Interface : Permits automating villager trades, as well give some information about the villager:

```lua
local tradingInterface = peripheral.find("trading_interface")

tradingInterface.getProfession() --Returns the current profession of the villager
tradingInterface.getTrades() -- Returns a table with all the trades for a villager
tradingInterface.trade("minecraft:chest_0","minecraft:chest_1",tradeId) -- Passing a storage name within the computer for the input and output.
                         -- and the id of the trade, that is the order the trade apears in the villager.
```

- XP Collector : Collects nearby XP Orbs and stores it, also permits the storage of current player levels by right clicking it 

```lua
local xp_collector = peripheral.find("xp_collector")

xp_collector.getCurrentXP() -- Returns the value of xp points stored;
xp_collector.dumpXP() -- Voids out the xp points;
```

- XP Bottler : Bottles the XP stored in the collector

```lua
local xp_bottler = peripheral.find("xp_bottler")

xp_bottler.bottleXP("minecraft:chest_0","minecraft:chest_1","xp_collector_0") -- Passing an storage for input and output,and the xp colletor, 
                                                                              -- Fetchs an Glass Bottle from the input chest, creates a Bottle of Enchanting,

```

- Enchanting Interface : Autoenchanting

```lua
local enchanting_table = peripheral.find("enchanting_interface")
-- By placing a enchanting table in one of the sides of the interface permits the use of : 

enchanting_table.getEnchantsFor("minecraft:chest_0",slot) -- Passing an storage for input and the slot, retrives the enchants that the item can take
enchanting_table.enchant("minecraft:chest_0",slot,"minecraft:chest_1") -- Passing an storage for input and the slot and a chest that need to contain,
                                                                       -- at least 1 Lapis Lazuli and 8 Bottles of Enchanting, enchants a item with a random compatible enchant.

```


- Loom Interface : Creating and Removing Patterns from Banners

```lua
local loom_interface = peripheral.find("loom_interface")
-- By placing a loom in one of the sides of the interface permits the use of : 

loom_interface.paintBanner("minecraft:chest_0",slotForBanner,slotForDye,pattern) -- Passing an storage for input and the slot of the banner, slot of the dye,
                                                                                 -- and a number for the pattern, creates a pattern on the banner with the color of the dye
loom_interface.clearBanner("minecraft:chest_0",slot) -- Passing an storage for input and the slot of the banner, the patterns on the banner gets removed.

```

