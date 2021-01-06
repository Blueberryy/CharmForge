package svenhjol.charm.module;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.base.enums.VanillaVariantMaterial;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.BookcaseBlock;
import svenhjol.charm.client.BookcasesClient;
import svenhjol.charm.container.BookcaseContainer;
import svenhjol.charm.item.AtlasItem;
import svenhjol.charm.tileentity.BookcaseTileEntity;

import java.util.*;

@Module(mod = Charm.MOD_ID, client = BookcasesClient.class, description = "Bookshelves that can hold up to 9 stacks of books and maps.")
public class Bookcases extends CharmModule {
    public static final ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "bookcase");
    public static final Map<IVariantMaterial, BookcaseBlock> BOOKCASE_BLOCKS = new HashMap<>();

    public static ContainerType<BookcaseContainer> CONTAINER;
    public static TileEntityType<BookcaseTileEntity> TILE_ENTITY;

    public static List<Class<? extends Item>> validItems = new ArrayList<>();

    @Config(name = "Valid books", description = "Additional items that may be placed in bookcases.")
    public static List<String> configValidItems = new ArrayList<>();

    @Override
    public void register() {
        validItems.addAll(Arrays.asList(
            Items.BOOK.getClass(),
            Items.ENCHANTED_BOOK.getClass(),
            Items.WRITTEN_BOOK.getClass(),
            Items.WRITABLE_BOOK.getClass(),
            Items.KNOWLEDGE_BOOK.getClass(),
            Items.PAPER.getClass(),
            Items.MAP.getClass(),
            Items.FILLED_MAP.getClass(),
            AtlasItem.class
        ));

        VanillaVariantMaterial.getTypes().forEach(type -> {
            BOOKCASE_BLOCKS.put(type, new BookcaseBlock(this, type));
        });

        configValidItems.forEach(string -> {
            Item item = Registry.ITEM.getOrDefault(new ResourceLocation(string));
            validItems.add(item.getClass());
        });

        CONTAINER = RegistryHandler.container(ID, BookcaseContainer::new);
        TILE_ENTITY = RegistryHandler.tileEntity(ID, BookcaseTileEntity::new);
    }

    public static boolean canContainItem(ItemStack stack) {
        return validItems.contains(stack.getItem().getClass());
    }
}
