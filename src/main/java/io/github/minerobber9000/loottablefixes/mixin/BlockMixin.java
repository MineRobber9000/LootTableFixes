package io.github.minerobber9000.loottablefixes.mixin;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.minerobber9000.loottablefixes.LootTableFixes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

@Mixin(Block.class)
public class BlockMixin {
    // Fixes MC-262347.
    @Inject(at = @At("HEAD"), method="Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;", cancellable = true)
    private static void loot_table_fixes$$getDrops(
        final BlockState state,
        final ServerLevel level,
        final BlockPos pos,
        final @Nullable BlockEntity blockEntity,
        final @Nullable Entity breaker,
        final ItemInstance tool,
        CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        LootParams.Builder params = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter(LootContextParams.TOOL, tool)
            .withOptionalParameter(LootContextParams.THIS_ENTITY, breaker)
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        if (breaker instanceof Player player) params = params.withLuck(player.getLuck());
        cir.setReturnValue(state.getDrops(params));
    }

    // Fixes MC-110336/MC-156705.
    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/world/level/block/Block;playerWillDestroy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/state/BlockState;")
    private void loot_table_fixes$$playerWillDestroy(final Level level, final BlockPos pos, final BlockState state, final Player player, CallbackInfoReturnable<BlockState> cir) {
        if (level.isClientSide()) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RandomizableContainerBlockEntity rcbe) {
            LootTableFixes.LOGGER.info("Randomizable container block entity at position ({},{},{}), unpacking loot table",pos.getX(),pos.getY(),pos.getZ());
            rcbe.unpackLootTable(player);
        }
    }
}
