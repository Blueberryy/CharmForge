package svenhjol.charm.module;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.ExtractEnchantmentsClient;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
@Module(mod = Charm.MOD_ID, client = ExtractEnchantmentsClient.class, description = "Extract enchantments from any enchanted item into an empty book using the grindstone.")
public class ExtractEnchantments extends CharmModule {
    @Config(name = "Initial XP cost", description = "Initial XP cost before adding XP equivalent to the enchantment level(s) of the item.")
    public static int initialCost = 2;

    @Config(name = "Treasure XP cost", description = "If the enchantment is a treasure enchantment, such as Mending, this cost will be added.")
    public static int treasureCost = 15;

    @Override
    public boolean depends() {
        return !ModHelper.isLoaded("grindenchantments");
    }

    public static Slot getGrindstoneInputSlot(int index, IInventory inputs) {
        return new Slot(inputs, index, 49, 19 + (index * 21)) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                boolean valid = stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.isEnchanted();
                return ExtractEnchantments.isEnabled() ? valid || stack.getItem() == Items.BOOK : valid;
            }
        };
    }

    public static Slot getGrindstoneOutputSlot(IWorldPosCallable context, IInventory inputs, IInventory output) {

        /**
         * Copypasta from GrindstoneScreenHandler 52-100 with Charm changes as marked.
         */
        return new Slot(output, 2, 129, 34) {

            /** vanilla **/
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            /**
             * Charm: override canTakeItems to check for extraction.
             * @param player Player
             * @return True if can take from output slot
             */
            public boolean canTakeStack(PlayerEntity player) {
                if (!isEnabled())
                    return true;

                List<ItemStack> stacks = getStacksFromInventory(inventory);

                if (shouldExtract(stacks)) {
                    int cost = getEnchantedItemFromStacks(stacks).map(ExtractEnchantments::getCost).orElse(0);
                    return hasEnoughXp(player, cost);
                }

                return true;
            }

            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                context.consume((world, blockPos) -> {
                    // ---- CHARM: SNIP ----
                    if (stack.getItem() instanceof EnchantedBookItem) {

                        if (!player.abilities.isCreativeMode) {
                            int cost = getCost(stack);
                            player.addExperienceLevel(-cost);
                        }

                        world.playEvent(1042, blockPos, 0);
                        return;
                    }
                    // ---- CHARM: SNIP ----

                    int i = this.getExperience(world);

                    while(i > 0) {
                        int j = ExperienceOrbEntity.getXPSplit(i);
                        i -= j;
                        world.addEntity(new ExperienceOrbEntity(world, (double)blockPos.getX(), (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, j));
                    }

                    world.playEvent(1042, blockPos, 0);
                });
                inputs.setInventorySlotContents(0, ItemStack.EMPTY);
                inputs.setInventorySlotContents(1, ItemStack.EMPTY);
                return stack;
            }

            /** vanilla **/
            private int getExperience(World world) {
                int ix = 0;
                int i = ix + this.getExperience(inputs.getStackInSlot(0));
                i += this.getExperience(inputs.getStackInSlot(1));
                if (i > 0) {
                    int j = (int)Math.ceil((double)i / 2.0D);
                    return j + world.rand.nextInt(j);
                } else {
                    return 0;
                }
            }

            /** vanilla **/
            private int getExperience(ItemStack stack) {
                int i = 0;
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
                Iterator var4 = map.entrySet().iterator();

                while(var4.hasNext()) {
                    Map.Entry<Enchantment, Integer> entry = (Map.Entry)var4.next();
                    Enchantment enchantment = (Enchantment)entry.getKey();
                    Integer integer = (Integer)entry.getValue();
                    if (!enchantment.isCurse()) {
                        i += enchantment.getMinEnchantability(integer);
                    }
                }

                return i;
            }
        };
    }

    public static boolean tryUpdateResult(IInventory inputs, IInventory output, @Nullable PlayerEntity player) {
        if (!isEnabled())
            return false;

        List<ItemStack> stacks = getStacksFromInventory(inputs);
        if (!shouldExtract(stacks))
            return false;

        Optional<ItemStack> enchanted = getEnchantedItemFromStacks(stacks);
        if (!enchanted.isPresent())
            return false;

        ItemStack in = enchanted.get();
        if (player != null && !hasEnoughXp(player, getCost(in)))
            return false;

        ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(in);
        enchantments.forEach((e, level) -> EnchantedBookItem.addEnchantment(out, new EnchantmentData(e, level)));
        output.setInventorySlotContents(0, out);
        return true;
    }

    private static boolean isEnabled() {
        return ModuleHandler.enabled(ExtractEnchantments.class);
    }

    public static List<ItemStack> getStacksFromInventory(IInventory inventory) {
        return Arrays.asList(inventory.getStackInSlot(0), inventory.getStackInSlot(1));
    }

    public static Optional<ItemStack> getEnchantedItemFromStacks(List<ItemStack> stacks) {
        return stacks.stream().filter(ItemStack::isEnchanted).findFirst();
    }

    public static boolean shouldExtract(List<ItemStack> stacks) {
        return getEnchantedItemFromStacks(stacks).isPresent() && stacks.stream().anyMatch(i -> i.getItem() == Items.BOOK);
    }

    public static boolean hasEnoughXp(PlayerEntity player, int cost) {
        return player.abilities.isCreativeMode || player.experienceLevel >= cost;
    }

    public static int getCost(ItemStack stack) {
        int cost = initialCost;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        // get all enchantments from the left item and create a map of enchantments for the output
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment ench = entry.getKey();
            if (ench == null)
                return 0;

            int level = entry.getValue();
            if (level > 0 && ench.isAllowedOnBooks()) {
                cost += level;

                if (ench.isTreasureEnchantment())
                    cost += treasureCost;
            }
        }

        // add repair cost on the input item
        if (stack.getTag() != null && !stack.getTag().isEmpty())
            cost += stack.getTag().getInt("RepairCost");

        return cost;
    }
}
