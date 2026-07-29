package io.github.minerobber9000.loottablefixes.mixin;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.logging.LogUtils;

import io.github.minerobber9000.loottablefixes.LootTableFixes;
import io.github.minerobber9000.loottablefixes.mixinsupport.TagEntryPoolEntry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

@Mixin(TagEntry.class)
public abstract class TagEntryMixin extends LootPoolSingletonContainer {
    protected TagEntryMixin(int weight, int quality, List<LootItemCondition> conditions,
            List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
    }

    public static final Logger LOGGER = LogUtils.getLogger();

    @Inject(
        method = "expandTag(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void loot_table_fixes$$expandTag(LootContext context, Consumer<LootPoolEntry> output, CallbackInfoReturnable<Boolean> cir) {
        if (!this.canRun(context)) {
            cir.setReturnValue(false);
            return;
        }
        TagEntry te = (TagEntry) (Object) this;
        try {
            for (final Holder<Item> item : context.getLevel().registryAccess().getOrThrow(te.tag)) {
                output.accept(new TagEntryPoolEntry(te, item));
            }
            cir.setReturnValue(true);
        } catch (Exception exc) {
            LootTableFixes.LOGGER.error("Error getting registry:",exc);
            cir.setReturnValue(false);
        }
    }
}
