# Changelog

## 2.1.6
* Fix bowl stack lost when milking Mooblooms #403
* [Forge] Fix ghost entity when using /give #395
* Remove Wandering Trader signal fire restriction
* Remove treasure enchantment cost on grindstone
* Hoe harvesting fully grown crop now plays sound

## 2.1.5
* Fix missing spruce bookshelf recipe
* Fix acquisition enchantment not dropping XP from harvested blocks
* Fix grindstone not showing the XP cost when the player has enough XP
* Fix crates showing tooltip when empty
* Fix overworld music sometimes playing when entering the nether
* Reduce spawn weights of lumberjack and beekeeper village pieces
* Raid Horns volume reduced by 25% and is configurable
* Raid Horns have cooldown after use
* Default amount of levels required to extract a treasure enchantment has been decreased from 15 to 10

## 2.1.4
* Fix mod init load event #392, [Charmonium#8](https://github.com/svenhjol/Charmonium/issues/8)
* Fix overlapping craft and chest buttons. #387
* Fix inventory button flicker when display Quark's backpack ui (F43nd1r)

## 2.1.3-forge-hotfix1
* Fix mod init race condition
* Fix loot table regression

## 2.1.3
* Added AutoRestock (F43nd1r)
* Fix Bookcases not opening #382 (F43nd1r)
* Fix revenge of extract enchantments crash #388

## 2.1.2
* Fix keybinds opening inventories when typing in chat or other guis. #368
* Fix dropped netherite-based items cause crash on dedicated server
* Fix Keybind issues #363
* Fix disabled modules do not disable recipes after change to toml config
* Fix shulker boxes not being craftable with variant chests
* Fix dispenser not firing glowballs
* Fix refined obsidian walls not connecting
* Fix glowballs converting blocks to water source blocks
* Fix super fun post-release text problem with grindstone
* Fix super fun post-release tinted enchantment loot derp
* Reduce spawn weight and group size of Coral Squids
* After much deliberation, we decided Coral Squids should no longer spawn uncontrollably and crash servers
* Enchantments are now extracted by the grindstone (Credit to [martenschaefer](https://github.com/mschae23/grind-enchantments))
* Added Mooblooms
* Added Snow storms
* Added Raid horns
* Added Acquisition enchantment
* Added Tinted enchantment
* JEI woodcutter and kiln integration

## 2.1.1
* Fix Coral Squid hitboxes. #351, #361
* Fix Candles not making sound when lighting
* Fix incompatibilities with Optifine
* Restore Quark integration for crates and bookcases
* Glow balls are now known as glowballs
* Placeable Glowstone Dust has been removed
* Update glowball texture

## 2.1.0
* Totem of Undying causing crash on server. Fixes #339
* VariantAnimalTextures has become VariantMobTextures. This changes the charm.json config file
* Added feature Coral Squids
* Added feature Coral Sea Lanterns
* Added Snow Golem derp face variants
* Added Kilns
* Added Refined Obsidian
* Added Smooth Glowstone
* Glow Pearls and placed glowstone can now light up any solid block face
* Removed invisible mineshaft spiders

## 2.0.4
* Added feature Placeable Glowstone Dust that lets you... place glowstone dust
* Added feature Glow Balls which are projectiles that light up the block they impact
* Added variant snow golem derp faces

## 2.0.3
* Darkness check for Husks and Strays, fixes them spawning in full light
* Lecterns can now be crafted from variant bookshelves. Fixes #330
* Explicit registration of Charm sounds. Relates to #333
* Changed block entity for barrel to fix a server bug. Fixes #332

## 2.0.2
* Candles are now valid decoration blocks when generating structures
* Restored vanilla chests and barrels, crafted using mixed wood
* Added hopper and minecart chest recipes with variant chests #231

## 2.0.1
* Fixed crash when trying to open lumberjack trades (dedicated server)
* Fixed missing loot tables for some custom village structures

## 2.0.0
* Fixed tidy button position on village trade screens
* Fixed broken armor invisibility when stripping in front of mobs
* Updated ladder textures
* Removed tidy button from beacon inventory
* Empty crates no longer drop themselves when in creative mode
* Added config option for chorus fruit teleport range
* Added more lumberjack and beekeeper house builds
* Added Lumberjack and Beekeeper trades
* Added Lumberjack and Beekeeper loot table for their village houses
* Beekeeper trades now use tagged flowers
* Chest texture and trim improvements
* Custom villagers now have zombie textures
* Removed "More Villager Trades" feature
* Axe now effective tool for woodcutter block
* Bump to Minecraft 1.16.3
* Removed improved lanterns due to unsolvable graphical issue
* Reduced mineshaft default ore spawn rate
* Lumberjacks log trade now only uses logs and stripped logs as requested item
* Lumberjacks now sell noteblocks and jukeboxes instead of converting logs to planks