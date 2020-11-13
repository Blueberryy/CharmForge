package svenhjol.charm.module;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.base.enums.VanillaVariantMaterial;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.ItemHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.CrateBlock;
import svenhjol.charm.client.CratesClient;
import svenhjol.charm.container.CrateContainer;
import svenhjol.charm.tileentity.CrateTileEntity;

import java.util.*;

@Module(mod = Charm.MOD_ID, client = CratesClient.class, description = "A smaller storage solution with the benefit of being transportable.", hasSubscriptions = true)
public class Crates extends CharmModule {
    public static final ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "crate");
    public static final Map<IVariantMaterial, CrateBlock> CRATE_BLOCKS = new HashMap<>();

    public static ContainerType<CrateContainer> CONTAINER;
    public static TileEntityType<CrateTileEntity> TILE_ENTITY;

    // add blocks and items to these lists to blacklist them from crates
    public static final List<Class<? extends Block>> INVALID_CRATE_BLOCKS = new ArrayList<>();
    public static final List<Class<? extends Block>> INVALID_SHULKER_BOX_BLOCKS = new ArrayList<>();

    @Config(name = "Show tooltip", description = "If true, hovering over a crate will show its contents in a tooltip.")
    public static boolean showTooltip = true;

    @Override
    public void register() {
        for (VanillaVariantMaterial type : VanillaVariantMaterial.values()) {
            CRATE_BLOCKS.put(type, new CrateBlock(this, type));
        }

        INVALID_CRATE_BLOCKS.add(ShulkerBoxBlock.class);
        INVALID_CRATE_BLOCKS.add(CrateBlock.class);
        INVALID_SHULKER_BOX_BLOCKS.add(CrateBlock.class);

        CONTAINER = RegistryHandler.container(ID, CrateContainer::new);
        TILE_ENTITY = RegistryHandler.tileEntity(ID, CrateTileEntity::new);
    }

    public static boolean canCrateInsertItem(ItemStack stack) {
        return !ModuleHandler.enabled(Crates.class) || !INVALID_CRATE_BLOCKS.contains(ItemHelper.getBlockClass(stack));
    }

    public static boolean canShulkerBoxInsertItem(ItemStack stack) {
        return !ModuleHandler.enabled(Crates.class) || !INVALID_SHULKER_BOX_BLOCKS.contains(ItemHelper.getBlockClass(stack));
    }

    public static CrateBlock getRandomCrateBlock(Random rand) {
        List<CrateBlock> values = new ArrayList<>(Crates.CRATE_BLOCKS.values());
        return values.get(rand.nextInt(values.size()));
    }
}
