# Changelog for Charm 1.12

## 1.4.1
* Fix spectres from showing subtitles when they despawn #196 (Sokyran)
* Fix setting random textures config to false not disabling random textures #289 (Sokyran)

## 1.4.0
* Changing dimension keeps player attributes

### Future MC compatibility fixes (SokyranTheDragon)
* Fix FutureMC compat recipes
* Added recipes in case FutureMC is present for equivalent blocks
* Added FutureMC flowers/sweet berries to the list of compostable items
* Added FutureMC blocks for world decoration when Charm equivalents are missing

## 1.3.7 
* Villages with random scattered crops are now rare by default.  For existing configs, change "Village Decorations: Crops outside" value from 0.8 to 0.1.
* Spectres no longer curse your items by default (they just apply weakness).  For existing configs, "Spectre: Apply curse" to false.  If you want curses, leave as true.
* Duplication bug: Inventories remain open when crates, barrels or bookshelf chests are broken by another player. Fixes #178

## 1.3.6

### Automation
* Variable redstone lamp drops, sound and hardness fixes. Fix #166

### Brewing
* Flavored Cakes no longer register modded cakes if the mod isn't present. Fix #161

### Smithing
* Tallow durability increase no longer increases repair cost.
For existing `charm.cfg`, set the repair cost chance from `0.75D` to `0.0D`.
New configurations will do this automatically.

### World
* Spectre haunting now disabled when structures are not enabled. Fix #165
* Added "Remove Nitwits" feature that will automatically select a new career for a nitwit
whenever it would be added to the world (via breeding or spawn eggs). Existing nitwits
will also gain a career when loaded.  You can disable this in `charm.cfg` by setting
`Remove nitwits` to `false`.

## 1.3.5

### General
* Some hardness and blast resistance changes, [PR #158](https://github.com/svenhjol/Charm/pull/158/commits)
* Ender Pearl block effective tool changed to pickaxe, hardness remains at 2.0
* JEI info tabs for flavored cake, bound compasses, moonstone and crates. Fix #139

## 1.3.4

### Automation
* The effective tool for redstone sand, gunpowder block and rotten flesh block is now shovel. Fix #157

### Brewing
* Flavored cake potion type recipe config has been restored. Fix #153

### Crafting
* Ender pearl blocks now break into 9 ender pearls when broken without silk touch. Fix #157

### General
* Updated German lang file.

## 1.3.3

### Enchantments
* Fixed: rewrote Magnetic logic to fix #147.
You may safely remove the `PlayerInteractionManager` line from your `charm_asm.cfg` as it is no longer required.

### Tweaks
* Added: now you can't accidentally shoot your tame animal with a bow.

## 1.3.2

### General
* Fixed: Features that check against other mods now respect being disabled in config. Fix #143

## 1.3.1

### World
* Fixed: Abandoned Crates loot table setup now happens before modules/features init. Fix #140 #141

## 1.3.0

### General
* Fixed: Features now write their config out even if they are disabled by another mod.  Fix #135

### Automation
* Changed: Redstone Sand can use any type of sand in its recipe.

### Brewing
* Added: Quark's "Resilience" potion added as a cake flavor.
* Changed: Flavored Cakes potions no longer configurable - this will be made into an API soon(TM) 
to fix language and texture requirements.

### Crafting
* Changed: Crate stack size limited to 1 due to NBT/tag stacking issues. Fix #109
* Changed: Rotten flesh block converts dirt above to podzol/mycelium only if it's in the applicable biome. 

### Enchanting
* Fixed: Clumsiness now only applies to "weapons", not "anything breakable".

### Tweaks
* Removed: Unearth Items. It has no special registrations so will not cause issues with existing worlds. 
You can safely remove the relevant entries from your existing charm.cfg

### World
* Changed: Spectre model redone, no longer internally based on EntityZombie. This should also fix
the chicken spawns and vehicle log spam.
* Changed: Village decoration config now sets "Trees have vines" to `false` by default, because of an
issue with cocoa beans generating in vanilla (See #133). Recommend to change this to `false` in your 
existing charm.cfg
* Changed: Emeralds are no longer converted to Charged Emeralds on lightning strike due to flaky
behavior and infinite charged emeralds. Now only found in dungeon loot.
* Fixed: End Portal Rune Frames no longer show in creative menu. Fix #138
* Fixed: Moonstone oredict entry now registers at correct time.
* Fixed: Balance "Salvage" and "Curse Break" villager trades to prevent infinite emeralds exploit. Fix #134
* Fixed: ASM obfuscation check for beacons to fix placement issue. Fix #107
* Fixed: Side check when sending a "Spectre despawned" message to the client. Fix #131

## 1.2.2

* Fixed wrong colors when right-clicking with moonstone on a dyed block.
* Moonstone can now be renamed on anvil and name is preserved on stone rebind.

## 1.2.1

* Added [Moonstone](https://charm.arcticfulmar.com/?feature=Moonstone) to help position alignment across dimensions.
* Magnetic enchantment plays item pickup sound when harvested item added to inventory. Fix #123
* Tallow repair now works on elytra, flint and steel, fishing rods and shears. Fix #122
* Barrels and Rune Frames respect Quark's rotation lock. Fix #121
* Config option to prevent spawning of vanilla items in village item frames and armor stands. Fix #94
* Added enchantment descriptions support. Fix #90

## 1.2.0

* Gold Tool improvements, bump efficiency to 17
* Structure Maps
* Crates, Bookshelf Chests and Barrels keep their custom names and show in GUI. Fix #101
* Smooth Glowstone by smelting glowstone in furnace
* Variable redstone lamps
* Spectres "haunt" strongholds and convert sand to soulsand on death
* Improved rotten flesh texture. Fix #113

## 1.1.15

* Crates max stack size 1. Fix #109
* No longer possible to add water bottles to cauldron if stack size greater than 1. Fix #110
* Updated Chinese locale provided by [ChingTing2568](https://github.com/ChingTing2586)
* French locale provided by [Neerwen](https://github.com/Neerwan)

## 1.1.14

* Spectres no longer remove enchantments when cursing. Fix #108
* Don't use treasure enchants on items in custom loot tables. Fix #106
* Nerfed loot. Fix #102

## 1.1.13

* ASM to check armor is salvagable. Fix #88
* Magnetic drop improvements. Fix #85
* Added curse check for items. Fix #91
* Moved extended state check. Ref #93
* Village chunk decoration dimension dependant now. Ref #92
* Restored ASM for shulker box.
* Handle crates being added to shulkers via Quark's drop off. Fix #82
* Magnetic may now be added to shears via anvil.
* Compatibility fix for EigenCraft.
* Tame animal entity checking issue. Ref #97
* Check for empty loot list when unearthing treasure. Fix #99
* Bookshelf chest now drops self as well as inventory. Fix #100
* Config to specify what is allowed in bookshelf chest.
* Updated recommended config for furnace recycling. Ref #98:

```
    minecraft:rail->minecraft:iron_ingot->1
    minecraft:detector_rail->minecraft:iron_ingot->1
    minecraft:activator_rail->minecraft:iron_ingot->1
    minecraft:golden_rail->minecraft:gold_ingot->1
    minecraft:iron_door->minecraft:iron_ingot->2
    minecraft:iron_bars->minecraft:iron_ingot->1
```

## 1.1.12

* Removed ItemHandler transformer ASM, too many problems

## 1.1.11

* Check casting of handler. Fix #84, Fix #86.
* Armor duping issue. Fix #83

## 1.1.10

* Added Magentic enchantment.

## 1.1.9

* Config to specify how many lanterns are crafted. Fix #81
* Config to force enable lanterns. Other force config will be added as needed. Fix #70
* Crates with no items no longer show hover. Fix #80
* Composter no longer opaque cube to fix light level. Fix #51

## 1.1.8

* Flavored Cakes now supports quark potions.  Fix #72.
* New configs add them automatically, but you can add quark's to your existing config like so:
```
    S:"Effect potion types" <
        swiftness
        strength
        leaping
        regeneration
        fire_resistance
        water_breathing
        invisibility
        night_vision
        quark:danger_sight
        quark:haste
        quark:resistance
     >
```
* Now checking for homing enchantment on hoe.  Fix #79

## 1.1.7

* Wither now can't destroy End Portal Rune blocks. For real this time. Fix #76
* Crates have hover tooltip when in the inventory. Fix #74
* Rune portal frame lighting fix to match End portal frame
* Extra records have volume explicitly set to 1.0 (max) now. Fix #77

## 1.1.5

* Wither no longer able to destroy End Portal Rune blocks and portal. Fix #75
* No longer performs config backup

## 1.1.4

* Issue with treasure/junk loot table. Related to #69
* Debug mode now enabled when a file called "charm.debug.cfg" exists in config dir
* Added sealed crate loot table debug in creative mode

## 1.1.3

* Rune portal frames and blocks no longer breakable in survival mode. Fix #68
* Barrel sounds no longer play when sneaking. Fix #67

## 1.1.2

* Added End Portal Runes

## 1.1.1

* Bound compass can now be reset to vanilla compass on anvil with iron ingot. Fix #61
* Suspicious soup no longer stacks. Fix #64
* Incorrect language string for record6. Fix #65
* Changed black hover text to white text for bound compass. Fix #60
* Sealed crate anvil recipe fixes. Fix #59
* Empty bowl now returned after eating suspicious soup. Fix #63
* Abandoned crates now checks for crate enabled again. Fix #57
    
## 1.1.0

* Add Rotten flesh block
* Dirt conversion should happen when water is above and below as well as any side
* Add Beacon compass
* Add Gunpower block
* Add composter
* Composter shouldn't consume items from hand when in creative
* JEI tab for composter materials
* Extend ToR material and number of increases
* Tooltip on ToR to show how many uses remain
* Inspirations integration for composter
* Reduce weight of enchanted books in villages
* Stop Abandoned Crates void checks
* Replace wood open sounds from 1.14
* Composter gives more items than config #42
* Effective tool for pistons with Redstone++ #41
* Infinite diamonds, remove RecycleWithFlint #40
* Bookshelf Chests don't give correct comparator output #43
* Add FurnacesRecycleMore
* Add RestrictFurnaceItems
* Config category names change to camelcase rather than lowercase
* Separate charm_asm.cfg for enabling/disabling core patches
* Fix issue with village well not being decorated (introduced by #32)
* Rotten flesh now converts grass path to podzol
* Add Endermite powder, drops from Endermites. Use it to find End Cities
* Correct creative tab distribution #44
* If Quark is loaded, chance for anvil to have a tool on it
* Bookshelf chest now uses different inventory drop check #4
* Barrel and Crate wood variants
* Prevent adding shulkerboxes to crates and vice versa #47
* Abandoned crates generation config changes
* Dispenser recipe allows damaged and enchanted bows
* Flavored cake recipes removed, right click cake with long potion to imbue
* Cake of speed changed to cake of swiftness, jump boost to leaping
* Only allow single enchanted book when extracting XP  #54
* Fixed recipe register for bookshelf chest #53
* Blocks without variants not appearing in creative tabs #50
* Crate nesting in shulker boxes issue, broke hopper inserting #49
* Anvils no longer consume entire book stack in middle slot #48
* Old configs will be backed up
* Use correct effect amplifier for flavored cakes #56
* Removed ShearedFlowers feature, vanilla makes this redundant
* Removed RecycleWithFlint feature in favor of FurnacesRecycleMore
* Recategorise modules and features. Breaks config - backup charm.cfg!
* Buffed salvage enchant, compatible with mending now
* Reduced default spawn chance of Illusioners
    
### 1.0.1

* Clumsiness curse now prevents block drops rather than not mining
* Fix lingering potion and cake lang strings
* Totem of Shielding should not be enchantable
* TEST Ender Sight issue when structures disabled #38
* Inspirations integration (lantern connects to chain/rope, disable Charm's Decay)
    
### 1.0.0

* Increase weight of totems in building/structure loot
* Remove old loot tables, nerf rare and valuable
* Movable TEs cause dupe #4
* Flavored cake ghost effects #2
* Add new Nether ore texture #5
* Disable NetherGoldDeposits if Nether gold ore mod is present #6
* Mystcraft profile village biome crash #9
* Decrease bat glowing time
* increase village deco spawn rate for common
* increase spectre spawn rate and group size
* mobs in villages should not be so tame
* outer deco config options #10
* Reduce golem spawn rate by default #11
* JEI layer appearing above tooltips #12
* cactuses could smash without drops on water collect
* Swamphut cat is supposed to be black
* More cake issues #16
* Village bookshelf chests don't show the right texture #14
* pumpkins spawn in villages as part of outer deco
* more village variations: blocks in house 3, rug when bed in house 4 and woodhut
* Ender sight stronghold detection issues #19
* Nether gold deposits config #18
* Suspicious soup doesn't work with all flowers #20
* Same village mob spawning in each village chunk, need eventRand as well as deterministic rand
* Container compatibility with Inventory Tweaks #15
* Test dispenser behaviour with Charged Emeralds #3
* Test modpack for bookshelf dupe issue #4
* Compatibility with Future MC #21
* Comparator support for containers #1
* Wooden blocks (crates, barrels, bookshelf chests) should burn
* fisherman and shepherd themes, improve carpenter theme
* Change "corruption" to "decay", nerf: remove damage, curse and mob conversion
* vines configurable on tree decoration
* improve desert village outer deco, trees should spawn
* Horses should be a mob that spawns in village outer deco
* TEST Zombie villages spawn zombie/skeleton horses
* Fix inventory icons for lanterns
* Rename "Miscellaneous" to "Tweaks" globally, note about breaking config change
* Fix url in mcmod.info
* more wood open/close sounds, remember refs
* PT nether fortress loot
* Lantern null pointer exception
* Fix AABB on lantern and gold lantern texture
* Flavored Cakes should not stack
* Extra checks on TileBookshelfChest, inventory drop is crashing #27
* Disable Suspicious Soup feature when SSM is installed #28
* Reduce tame sitting cats from villages #8
* Crash with Quark r1.5-147 #31
* A crate has no name #34
* Cubic Chunks compatibility #22
* Village buildings are on fire in a specific modpack #13
* Weirdness with bookshelves #32
* Zombie villages have eroded buildings, some mossy cobblestone, some cobwebs.
* Test Abyssalcraft, blockstate have been fixed in beta3 #35