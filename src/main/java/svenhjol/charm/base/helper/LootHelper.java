package svenhjol.charm.base.helper;

import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LootHelper {
    public static List<ResourceLocation> CUSTOM_LOOT_TABLES = new ArrayList<>();

    public static List<ResourceLocation> getAllLootTables() {
        List<ResourceLocation> allLootTables = new ArrayList<>();

        allLootTables.addAll(LootTables.getReadOnlyLootTables());
        allLootTables.addAll(CUSTOM_LOOT_TABLES);

        return allLootTables;
    }

    public static List<ResourceLocation> getVanillaChestLootTables() {
        return getVanillaLootTables("chests/");
    }

    public static List<ResourceLocation> getVanillaVillageLootTables() {
        return getVanillaLootTables("/village/");
    }

    public static List<ResourceLocation> getVanillaLootTables(String pattern) {
        return LootTables.getReadOnlyLootTables().stream()
            .filter(t -> t.getPath().contains(pattern))
            .collect(Collectors.toList());
    }

    public static ResourceLocation getLootTable(String loot, ResourceLocation fallback) {
        ResourceLocation lootTable = fallback;

        if (!loot.isEmpty()) {
            List<ResourceLocation> tables = getAllLootTables();

            for (ResourceLocation res : tables) {
                String[] s = res.getPath().split("/");
                for (String frag : s) {
                    if (frag.contains(loot)) {
                        lootTable = res;
                        break;
                    }
                }
            }
        }

        return lootTable;
    }
}