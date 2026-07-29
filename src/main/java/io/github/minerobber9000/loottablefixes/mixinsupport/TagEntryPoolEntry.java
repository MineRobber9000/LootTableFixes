package io.github.minerobber9000.loottablefixes.mixinsupport;

import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.TagEntry;

public class TagEntryPoolEntry implements LootPoolEntry {
    private TagEntry parent;
    private Holder<Item> item;

    public TagEntryPoolEntry(TagEntry parent, Holder<Item> item) {
        this.parent = parent;
        this.item = item;
    }

    @Override
    public int getWeight(float luck) {
        return Math.max(Mth.floor(parent.weight + parent.quality * luck), 0);
    }

    @Override
    public void createItemStack(Consumer<ItemStack> output, LootContext context) {
        output.accept(parent.compositeFunction.apply(new ItemStack(item), context));
    }
}
